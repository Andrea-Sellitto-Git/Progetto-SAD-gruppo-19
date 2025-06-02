package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.decorator.FillDecorator;
import it.unisa.progettosadgruppo19.decorator.StrokeDecorator;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.*;
import it.unisa.progettosadgruppo19.command.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.transform.Scale;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import java.util.Optional;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller principale per l'interfaccia di disegno.
 * Gestisce la toolbar, il canvas, i comandi di file (salva/carica),
 * undo/redo e l'applicazione di stroke, fill e zoom.
 */
public class Controller {

    @FXML
    private Pane drawingPane;
    @FXML
    private Button lineButton, rectButton, ellipseButton, saveButton, loadButton, deleteButton, copyButton,
            cutButton, pasteButton, zoomInButton, zoomOutButton, bringToFrontButton, sendToBackButton,
            gridButton;
    @FXML
    private ColorPicker strokePicker, fillPicker;

    @FXML
    private Slider rotateSlider;
    
    private final List<AbstractShape> currentShapes = new ArrayList<>();
    private MouseEventHandler mouseHandler;
    private ShapeManager shapeManager;
    private ShapeFileManager fileManager = new ShapeFileManager();
    private final ZoomManager zoomManager = new ZoomManager();
    private final Scale scaleTransform = new Scale(1, 1, 0, 0);
    private StackUndoInvoker commandInvoker = new StackUndoInvoker();
    private GridManager gridManager;

    private String selectedShape = "Linea";

    /**
     * Inizializza il controller: collega i trasform, i listener dei bottoni,
     * configura il mouse handler e imposta colori di default.
     */
    @FXML
    public void initialize() {
        drawingPane.getTransforms().add(scaleTransform);

        mouseHandler = new MouseEventHandler(drawingPane, currentShapes);
        shapeManager = new ShapeManager(currentShapes, drawingPane);
        gridManager = new GridManager(drawingPane);

        mouseHandler.setSelectedShape(selectedShape);
        mouseHandler.setToolActive(true);

        strokePicker.setValue(javafx.scene.paint.Color.BLACK);
        fillPicker.setValue(javafx.scene.paint.Color.TRANSPARENT);

        lineButton.setOnAction(e -> setTool("Linea"));
        rectButton.setOnAction(e -> setTool("Rettangolo"));
        ellipseButton.setOnAction(e -> setTool("Ellisse"));

        strokePicker.setOnAction(e -> applyStroke());
        fillPicker.setOnAction(e -> applyFill());

        saveButton.setOnAction(e -> onSave());
        loadButton.setOnAction(e -> onLoad());

        deleteButton.setOnAction(e -> {
            Shape selected = mouseHandler.getSelectedShapeInstance();
            if (selected != null) {
                commandInvoker.execute(new Delete(shapeManager, selected));
                mouseHandler.setSelectedShapeInstance(null);
            }
        });

        copyButton.setOnAction(e -> {
            Shape selected = mouseHandler.getSelectedShapeInstance();
            if (selected != null) {
                commandInvoker.execute(new Copy(mouseHandler, selected));
            }
        });

        cutButton.setOnAction(e -> {
            Shape selected = mouseHandler.getSelectedShapeInstance();
            if (selected != null) {
                commandInvoker.execute(new Cut(mouseHandler, shapeManager, selected));
                mouseHandler.setSelectedShapeInstance(null);
            }
        });

        pasteButton.setOnAction(e -> enablePasteMode());

        bringToFrontButton.setOnAction(e -> {
            Shape selected = mouseHandler.getSelectedShapeInstance();
            if (selected != null) {
                int maxIndex = drawingPane.getChildren().size() - 1;
                commandInvoker.execute(new ZLevelsToFront(shapeManager, selected, maxIndex));
            }
        });

        sendToBackButton.setOnAction(e -> {
            Shape selected = mouseHandler.getSelectedShapeInstance();
            if (selected != null) {
                int gridCount = gridManager.getGridLayerCount();
                commandInvoker.execute(new ZLevelsToBack(shapeManager, selected, gridCount));
            }
        });

        gridButton.setOnAction(e -> {
            // se la griglia non è già presente, chiedi la dimensione
            if (gridManager.getGridLayerCount() == 0) {
                TextInputDialog dialog = new TextInputDialog("20");
                dialog.setTitle("Imposta dimensione griglia");
                dialog.setHeaderText("Dimensione dei quadrati della griglia");
                dialog.setContentText("Inserisci la dimensione del lato (px):");
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(input -> {
                    try {
                        double size = Double.parseDouble(input);
                        gridManager.setSpacing(size);
                        gridManager.toggleGrid();
                        gridButton.setStyle("-fx-background-color: lightgray;");
                    } catch (NumberFormatException ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Input non valido");
                        alert.setHeaderText("Valore non valido");
                        alert.setContentText("Per favore, inserisci un numero valido.");
                        alert.showAndWait();
                    }
                });
            }
            // se la griglia è già visibile, la rimuoviamo direttamente
            else {
                gridManager.toggleGrid();
                gridButton.setStyle("");
            }
        });
            zoomInButton.setOnAction(e -> {
                double s = zoomManager.zoomIn();
                scaleTransform.setX(s);
                scaleTransform.setY(s);
        });

        zoomOutButton.setOnAction(e -> {
            double s = zoomManager.zoomOut();
            scaleTransform.setX(s);
            scaleTransform.setY(s);
        });
    
        
        rotateSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if ( !rotateSlider.isValueChanging()) {
                rotateSelected(Math.floor(newValue.doubleValue()));
            }
        });
        rotateSlider.valueChangingProperty().addListener((obs, wasChanging, isNowChanging) -> {
            if (!isNowChanging && wasChanging ) {
                rotateSelected(Math.floor(rotateSlider.getValue()));
            }
        });

        drawingPane.setOnMousePressed(mouseHandler::onPressed);
        drawingPane.setOnMouseDragged(mouseHandler::onDragged);
        drawingPane.setOnMouseReleased(mouseHandler::onReleased);
        drawingPane.setOnMouseClicked(mouseHandler::onMouseClick);
    }
    
    private void rotateSelected(double value){
        Shape shape = mouseHandler.getSelectedShapeInstance();
        
        System.out.print("cambio rotazione a "+ value+ "°");
        
        if (shape != null){
            System.out.println(".");
            commandInvoker.execute(new Rotate(shapeManager, shape , value));
        }else{
            System.out.println(",ma nessua figura è selezionata");
        }
    }
    

    /**
     * Seleziona il tipo di shape da creare.
     *
     * @param tipo nome del tipo di shape ("Linea", "Rettangolo", "Ellisse", ecc.)
     */
    private void setTool(String tipo) {
        selectedShape = tipo;
        mouseHandler.setSelectedShape(tipo);
        mouseHandler.setStrokeColor(strokePicker.getValue());
        mouseHandler.setFillColor(fillPicker.getValue());
        mouseHandler.setToolActive(true);
        mouseHandler.unselectShape();
    }

    /** Esegue il comando di salvataggio su file. */
    private void onSave() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        new Save(stage, currentShapes, fileManager).execute();
    }

    /** Esegue il comando di caricamento da file. */
    private void onLoad() {
        Stage stage = (Stage) loadButton.getScene().getWindow();
        new Load(stage, currentShapes, drawingPane, fileManager).execute();
    }

    /** Applica il colore di contorno (stroke) alla shape selezionata. */
    private void applyStroke() {
        Shape selected = mouseHandler.getSelectedShapeInstance();
        if (selected != null) {
            Shape decorated = new StrokeDecorator(selected, strokePicker.getValue());
            int index = drawingPane.getChildren().indexOf(selected.getNode());
            drawingPane.getChildren().set(index, decorated.getNode());
            decorated.getNode().setUserData(decorated);
            mouseHandler.setSelectedShapeInstance(decorated);
        }
    }

    /** Applica il colore di riempimento (fill) alla shape selezionata. */
    private void applyFill() {
        Shape selected = mouseHandler.getSelectedShapeInstance();
        if (selected != null) {
            Shape decorated = new FillDecorator(selected, fillPicker.getValue());
            int index = drawingPane.getChildren().indexOf(selected.getNode());
            drawingPane.getChildren().set(index, decorated.getNode());
            decorated.getNode().setUserData(decorated);
            mouseHandler.setSelectedShapeInstance(decorated);
        }
    }

    
    @FXML
    public void handlePaste() {
        drawingPane.setOnMouseClicked(event -> {
            commandInvoker.execute(new Paste(mouseHandler, shapeManager, event.getX(), event.getY()));
            drawingPane.setOnMouseClicked(mouseHandler::onMouseClick); // Ripristina il comportamento standard
        });
    }

    /** Zoom avanti applicando il fattore successivo. */
    @FXML
    private void onZoomIn() {
        double s = zoomManager.zoomIn();
        scaleTransform.setX(s);
        scaleTransform.setY(s);
    }

    /** Zoom avanti applicando il fattore precedente. */
    @FXML
    private void onZoomOut() {
        double s = zoomManager.zoomOut();
        scaleTransform.setX(s);
        scaleTransform.setY(s);
    }

    /** Annulla l'ultimo comando eseguito (undo). */
    @FXML
    private void onUndo() {
        commandInvoker.undo();
    }

    /** Attiva la modalità click-per-incollare. */
    private void enablePasteMode() {
        System.out.println("[PASTE MODE] Attivato: clicca sul canvas per incollare");
        drawingPane.setOnMouseClicked(event -> {
            System.out.println("[PASTE MODE] Click rilevato su: " + event.getX() + ", " + event.getY());
            commandInvoker.execute(new Paste(mouseHandler, shapeManager, event.getX(), event.getY()));
            drawingPane.setOnMouseClicked(mouseHandler::onMouseClick);
        });
    }
}
