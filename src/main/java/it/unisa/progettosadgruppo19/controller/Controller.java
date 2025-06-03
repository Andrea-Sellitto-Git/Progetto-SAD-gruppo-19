package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.*;
import it.unisa.progettosadgruppo19.command.*;
import it.unisa.progettosadgruppo19.factory.TextShapeCreator;

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
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * Controller principale per l'interfaccia di disegno. Gestisce la toolbar, il
 * canvas, i comandi di file (salva/carica), undo/redo e l'applicazione di
 * stroke, fill e zoom.
 * 
 * VERSIONE AGGIORNATA con supporto per selezione multipla.
 */
public class Controller {
    
    @FXML
    private Button mirrorHorizontalButton;

    @FXML
    private Button mirrorVerticalButton;

    @FXML
    private Spinner<Integer> fontSizeSpinner;

    @FXML
    private TextField textInputField;

    @FXML
    private Button textButton;

    @FXML
    private Pane drawingPane;
    @FXML
    private ToolBar toolbar;
    @FXML
    private Button lineButton, rectButton, ellipseButton, polygonButton, saveButton, loadButton, deleteButton, copyButton,
            cutButton, pasteButton, zoomInButton, zoomOutButton, bringToFrontButton, sendToBackButton, undoButton,
            gridButton;
    @FXML
    private ColorPicker strokePicker, fillPicker;

    @FXML
    private Slider rotateSlider;
    
    // NUOVI CAMPI PER SELEZIONE MULTIPLA
    @FXML
    private Button multiSelectButton, selectAllButton, clearSelectionButton;
    @FXML
    private Label selectionCountLabel, selectionInfoLabel;
    
    private final List<AbstractShape> currentShapes = new ArrayList<>();
    private MouseEventHandler mouseHandler;
    private PolygonMouseEventHandler polygonHandler;
    private ShapeManager shapeManager;
    private ShapeFileManager fileManager = new ShapeFileManager();
    private final ZoomManager zoomManager = new ZoomManager();
    private final Scale scaleTransform = new Scale(1, 1, 0, 0);
    private StackUndoInvoker commandInvoker = new StackUndoInvoker();
    private GridManager gridManager;

    // NUOVO: Manager per selezione multipla
    private MultipleSelectionManager multipleSelectionManager;

    private String selectedShape = null;

    /**
     * Inizializza il controller: collega i trasform, i listener dei bottoni,
     * configura il mouse handler e imposta colori di default.
     * VERSIONE AGGIORNATA con supporto selezione multipla.
     */
    @FXML
    public void initialize() {
        fontSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 72, 12));
        drawingPane.getTransforms().add(scaleTransform);

        mouseHandler = new MouseEventHandler(drawingPane, currentShapes);
        shapeManager = new ShapeManager(currentShapes, drawingPane);
        gridManager = new GridManager(drawingPane);

        // NUOVO: Inizializza il manager per la selezione multipla
        multipleSelectionManager = new MultipleSelectionManager();

        mouseHandler.setSelectedShape(selectedShape);
        mouseHandler.setToolActive(true);
        mouseHandler.setInvoker(commandInvoker);
        
        // NUOVO: Imposta il manager selezione multipla nel mouse handler
        mouseHandler.setMultipleSelectionManager(multipleSelectionManager);
        
        Platform.runLater(() -> {
            mouseHandler.setToolbarHeight(toolbar.getHeight());
        });

        setNeutralTool();
        
        strokePicker.setValue(javafx.scene.paint.Color.BLACK);
        fillPicker.setValue(javafx.scene.paint.Color.TRANSPARENT);

        lineButton.setOnAction(e -> setTool("Linea"));
        rectButton.setOnAction(e -> setTool("Rettangolo"));
        ellipseButton.setOnAction(e -> setTool("Ellisse"));
        polygonButton.setOnAction(e -> setTool("Poligono"));

        // MODIFICATO: Listener colori con supporto selezione multipla
        strokePicker.setOnAction(e -> applyStrokeToSelection());
        fillPicker.setOnAction(e -> applyFillToSelection());

        saveButton.setOnAction(e -> onSave());
        loadButton.setOnAction(e -> onLoad());

        // MODIFICATO: Pulsanti con supporto selezione multipla
        deleteButton.setOnAction(e -> deleteSelection());
        copyButton.setOnAction(e -> copySelection());
        cutButton.setOnAction(e -> cutSelection());
        pasteButton.setOnAction(e -> enablePasteMode());

        // MODIFICATO: Pulsanti Z-order con supporto selezione multipla
        bringToFrontButton.setOnAction(e -> bringSelectionToFront());
        sendToBackButton.setOnAction(e -> sendSelectionToBack());

        undoButton.setOnAction(e -> onUndo());

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
            } // se la griglia è già visibile, la rimuoviamo direttamente
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

        // MODIFICATO: Slider rotazione con supporto selezione multipla
        rotateSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!rotateSlider.isValueChanging()) {
                rotateSelection(Math.floor(newValue.doubleValue()));
            }
        });
        rotateSlider.valueChangingProperty().addListener((obs, wasChanging, isNowChanging) -> {
            if (!isNowChanging && wasChanging) {
                rotateSelection(Math.floor(rotateSlider.getValue()));
            }
        });

        drawingPane.setOnMousePressed(mouseHandler::onPressed);
        drawingPane.setOnMouseDragged(mouseHandler::onDragged);
        drawingPane.setOnMouseReleased(mouseHandler::onReleased);

        // MODIFICATO: Click con supporto per menu contestuale
        setupMultiSelectionListeners();

        textButton.setOnAction(e -> setTool("Testo"));

        fontSizeSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            Shape selected = mouseHandler.getSelectedShapeInstance();
            if (selected instanceof TextShape textShape) {
                textShape.setFontSize(newVal);
            }
        });

        // NUOVO: Configura il pulsante per la selezione multipla
        if (multiSelectButton != null) {
            multiSelectButton.setOnAction(e -> toggleMultipleSelectionMode());
        }

        // NUOVO: Configura pulsanti per selezione multipla
        if (selectAllButton != null) {
            selectAllButton.setOnAction(e -> selectAllShapes());
        }

        if (clearSelectionButton != null) {
            clearSelectionButton.setOnAction(e -> clearAllSelection());
        }

        // NUOVO: Timer per aggiornare le informazioni di selezione
        javafx.animation.Timeline selectionUpdateTimer = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.millis(500), e -> updateSelectionInfo())
        );
        selectionUpdateTimer.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        selectionUpdateTimer.play();

        // NUOVO: Imposta scorciatoie da tastiera
        setupKeyboardShortcuts();
        
        mirrorHorizontalButton.setOnAction(e -> {
            Shape selected = mouseHandler.getSelectedShapeInstance();
            if (selected != null) {
                commandInvoker.execute(new Mirror(shapeManager, selected, true));
            }
        });

        mirrorVerticalButton.setOnAction(e -> {
            Shape selected = mouseHandler.getSelectedShapeInstance();
            if (selected != null) {
                commandInvoker.execute(new Mirror(shapeManager, selected, false));
            }
        });
    }
    
    /**
     * MODIFICATO: Rotazione con supporto selezione multipla.
     */
    private void rotateSelection(double value) {
        if (multipleSelectionManager.hasSelection()) {
            List<Shape> selectedShapes = multipleSelectionManager.getSelectedShapes();
            
            System.out.println("[CONTROLLER] Rotazione selezione multipla a " + value + "°");
            
            MultiRotateCommand command = new MultiRotateCommand(shapeManager, selectedShapes, value);
            commandInvoker.execute(command);
            
            System.out.println("[CONTROLLER] Rotazione applicata a " + selectedShapes.size() + " shape");
        } else {
            // Comportamento normale per singola selezione
            rotateSelected(value);
        }
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
     * Attiva la modalità "neutra", cioè nessuno strumento di disegno selezionato:
     *  – se c'era polygonHandler, lo stacca (detach e null)
     *  – disabilita mouseHandler
     *  – imposta selectedShape = null
     *  – toglie stili CSS di evidenziazione dai pulsanti
     */
    private void setNeutralTool() {
        // Detach del polygon handler se presente
        if (polygonHandler != null) {
            polygonHandler.detach();
            polygonHandler = null;
            System.out.println("[CONTROLLER] PolygonHandler detached.");
        }
        
        // Disattiva il mouse handler standard
        if (mouseHandler != null) {
            mouseHandler.setToolActive(false);
        }
        
        // Reset dello strumento selezionato
        selectedShape = null;

        // Rimuovi classe "active-tool" da tutti i pulsanti
        lineButton.getStyleClass().remove("active-tool");
        rectButton.getStyleClass().remove("active-tool");
        ellipseButton.getStyleClass().remove("active-tool");
        polygonButton.getStyleClass().remove("active-tool");
        textButton.getStyleClass().remove("active-tool");
        
        // Ripristina il comportamento standard del mouse
        drawingPane.setOnMouseClicked(mouseHandler::onMouseClick);
        
        System.out.println("[CONTROLLER] Modalità neutra attivata");
    }

    /**
     * Seleziona il tipo di shape da creare.
     *
     * @param tipo nome del tipo di shape ("Linea", "Rettangolo", "Ellisse",
     * ecc.)
     */
    private void setTool(String tipo) {
        
        setNeutralTool();
        
        if (tipo == null) {
            // Modalità neutra esplicita
            return;
        }

        if (tipo.equals("Poligono")) {
            // Attivo l'handler dedicato al poligono
            selectedShape = "Poligono";

            // Creo SEMPRE un nuovo PolygonMouseEventHandler per evitare problemi di stato
            // Non riutilizzare mai un handler precedente
            polygonHandler = new PolygonMouseEventHandler(
                    drawingPane,
                    // Faccio un cast: nella lista currentShapes ci possono essere anche altri AbstractShape,
                    // ma il costruttore di PolygonMouseEventHandler considera solo FreeFormPolygonShape.
                    (List<FreeFormPolygonShape>)(List<?>) currentShapes,
                    new ShapeManager(currentShapes, drawingPane),
                    strokePicker.getValue(),
                    fillPicker.getValue(),
                    this::setNeutralTool // Callback per tornare automaticamente in modalità neutra
            );
            
            // Evidenzia il pulsante attivo
            polygonButton.getStyleClass().add("active-tool");
            
            System.out.println("[CONTROLLER] Tool Poligono attivato.");
            
        } else {
            selectedShape = tipo;
            mouseHandler.setSelectedShape(tipo);
            mouseHandler.setStrokeColor(strokePicker.getValue());
            mouseHandler.setFillColor(fillPicker.getValue());
            mouseHandler.setToolActive(true);
            mouseHandler.unselectShape();
            
            // Evidenzia il pulsante corrispondente
            switch (tipo) {
                case "Linea" -> lineButton.getStyleClass().add("active-tool");
                case "Rettangolo" -> rectButton.getStyleClass().add("active-tool");
                case "Ellisse" -> ellipseButton.getStyleClass().add("active-tool");
                case "Testo" -> textButton.getStyleClass().add("active-tool");
            }
        }

        // Se attivo il testo, disabilita il mouseHandler per disegno shape
        if ("Testo".equals(tipo)) {
            drawingPane.setOnMouseClicked(this::onCanvasClickForText);
        } else {
            // Ripristina il comportamento standard
            drawingPane.setOnMouseClicked(mouseHandler::onMouseClick);
        }
    }

    /**
     * Esegue il comando di salvataggio su file.
     */
    private void onSave() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        new Save(stage, currentShapes, fileManager).execute();
    }

    /**
     * Esegue il comando di caricamento da file.
     */
    private void onLoad() {
        Stage stage = (Stage) loadButton.getScene().getWindow();
        new Load(stage, currentShapes, drawingPane, fileManager).execute();
    }

    /**
     * NUOVO: Attiva/disattiva la modalità di selezione multipla.
     */
    private void toggleMultipleSelectionMode() {
        boolean isActive = !multipleSelectionManager.isMultipleSelectionMode();
        multipleSelectionManager.setMultipleSelectionMode(isActive);
        
        updateMultiSelectButtonAppearance();
        
        System.out.println("[CONTROLLER] Modalità selezione multipla: " + (isActive ? "ATTIVATA" : "DISATTIVATA"));
    }

    /**
     * MODIFICATO: Applica il colore di stroke con supporto selezione multipla.
     */
    private void applyStrokeToSelection() {
        if (multipleSelectionManager.hasSelection()) {
            List<Shape> selectedShapes = multipleSelectionManager.getSelectedShapes();
            Color newStroke = strokePicker.getValue();
            
            MultiChangeStrokeCommand command = new MultiChangeStrokeCommand(selectedShapes, newStroke);
            mouseHandler.applyUndoableStrategy(command);
            
            System.out.println("[CONTROLLER] Stroke applicato a " + selectedShapes.size() + " shape");
        } else {
            // Comportamento normale per singola selezione
            applyStroke();
        }
    }

    /**
     * MODIFICATO: Applica il colore di fill con supporto selezione multipla.
     */
    private void applyFillToSelection() {
        if (multipleSelectionManager.hasSelection()) {
            List<Shape> selectedShapes = multipleSelectionManager.getSelectedShapes();
            Color newFill = fillPicker.getValue();
            
            MultiChangeFillCommand command = new MultiChangeFillCommand(selectedShapes, newFill);
            mouseHandler.applyUndoableStrategy(command);
            
            System.out.println("[CONTROLLER] Fill applicato a " + selectedShapes.size() + " shape");
        } else {
            // Comportamento normale per singola selezione
            applyFill();
        }
    }

    /**
     * Applica il colore di contorno (stroke) alla shape selezionata (metodo originale).
     */
    private void applyStroke() {
        Shape selected = mouseHandler.getSelectedShapeInstance();
        if (selected != null) {
            Color oldStroke = (Color) ((javafx.scene.shape.Shape) selected.getNode()).getStroke();
            Color newStroke = strokePicker.getValue();
            mouseHandler.applyUndoableStrategy(new ChangeStroke(selected, oldStroke, newStroke));
        }
    }

    /**
     * Applica il colore di riempimento (fill) alla shape selezionata (metodo originale).
     */
    private void applyFill() {
        Shape selected = mouseHandler.getSelectedShapeInstance();
        if (selected != null) {
            Color oldFill = (Color) ((javafx.scene.shape.Shape) selected.getNode()).getFill();
            Color newFill = fillPicker.getValue();
            mouseHandler.applyUndoableStrategy(new ChangeFill(selected, oldFill, newFill));
        }
    }

    /**
     * MODIFICATO: Elimina con supporto selezione multipla.
     */
    private void deleteSelection() {
        if (multipleSelectionManager.hasSelection()) {
            List<Shape> selectedShapes = multipleSelectionManager.getSelectedShapes();
            
            MultiDeleteCommand command = new MultiDeleteCommand(shapeManager, selectedShapes);
            commandInvoker.execute(command);
            
            // Pulisce la selezione dopo l'eliminazione
            multipleSelectionManager.clearSelection();
            
            System.out.println("[CONTROLLER] Eliminate " + selectedShapes.size() + " shape");
        } else {
            // Comportamento normale per singola selezione
            Shape selected = mouseHandler.getSelectedShapeInstance();
            if (selected != null) {
                commandInvoker.execute(new Delete(shapeManager, selected));
                mouseHandler.setSelectedShapeInstance(null);
            }
        }
    }

    /**
     * MODIFICATO: Copia con supporto selezione multipla.
     */
    private void copySelection() {
        if (multipleSelectionManager.hasSelection()) {
            List<Shape> selectedShapes = multipleSelectionManager.getSelectedShapes();
            
            MultiCopyCommand command = new MultiCopyCommand(mouseHandler, selectedShapes);
            commandInvoker.execute(command);
            
            System.out.println("[CONTROLLER] Copiate " + selectedShapes.size() + " shape");
        } else {
            // Comportamento normale per singola selezione
            Shape selected = mouseHandler.getSelectedShapeInstance();
            if (selected != null) {
                commandInvoker.execute(new Copy(mouseHandler, selected));
            }
        }
    }

    /**
     * MODIFICATO: Taglia con supporto selezione multipla.
     */
    private void cutSelection() {
        if (multipleSelectionManager.hasSelection()) {
            List<Shape> selectedShapes = multipleSelectionManager.getSelectedShapes();
            
            // Per ora, tagliamo solo la prima shape (limitazione del clipboard attuale)
            if (!selectedShapes.isEmpty()) {
                Shape firstShape = selectedShapes.get(0);
                commandInvoker.execute(new Cut(mouseHandler, shapeManager, firstShape));
                
                // Rimuovi le altre dalla selezione
                multipleSelectionManager.removeFromSelection(firstShape);
            }
            
            System.out.println("[CONTROLLER] Tagliata prima shape della selezione");
        } else {
            // Comportamento normale per singola selezione
            Shape selected = mouseHandler.getSelectedShapeInstance();
            if (selected != null) {
                commandInvoker.execute(new Cut(mouseHandler, shapeManager, selected));
                mouseHandler.setSelectedShapeInstance(null);
            }
        }
    }

    /**
     * MODIFICATO: Porta in primo piano con supporto selezione multipla.
     */
    private void bringSelectionToFront() {
        if (multipleSelectionManager.hasSelection()) {
            List<Shape> selectedShapes = multipleSelectionManager.getSelectedShapes();
            int maxIndex = drawingPane.getChildren().size() - 1;
            
            for (Shape shape : selectedShapes) {
                commandInvoker.execute(new ZLevelsToFront(shapeManager, shape, maxIndex));
            }
            
            System.out.println("[CONTROLLER] Portate in primo piano " + selectedShapes.size() + " shape");
        } else {
            // Comportamento normale per singola selezione
            Shape selected = mouseHandler.getSelectedShapeInstance();
            if (selected != null) {
                int maxIndex = drawingPane.getChildren().size() - 1;
                System.out.println("[FRONT] Portando " + selected.getClass().getSimpleName() + " all'indice " + maxIndex);
                commandInvoker.execute(new ZLevelsToFront(shapeManager, selected, maxIndex));
            }
        }
    }

    /**
     * MODIFICATO: Porta in secondo piano con supporto selezione multipla.
     */
    private void sendSelectionToBack() {
        if (multipleSelectionManager.hasSelection()) {
            List<Shape> selectedShapes = multipleSelectionManager.getSelectedShapes();
            int gridCount = gridManager.getGridLayerCount();
            
            for (Shape shape : selectedShapes) {
                commandInvoker.execute(new ZLevelsToBack(shapeManager, shape, gridCount));
            }
            
            System.out.println("[CONTROLLER] Portate in secondo piano " + selectedShapes.size() + " shape");
        } else {
            // Comportamento normale per singola selezione
            Shape selected = mouseHandler.getSelectedShapeInstance();
            if (selected != null) {
                int gridCount = gridManager.getGridLayerCount();
                System.out.println("[BACK] Portando " + selected.getClass().getSimpleName() + " all'indice " + gridCount);
                commandInvoker.execute(new ZLevelsToBack(shapeManager, selected, gridCount));
            }
        }
    }

    /**
     * NUOVO: Seleziona tutte le shape presenti nel canvas.
     */
    private void selectAllShapes() {
        if (multipleSelectionManager.isMultipleSelectionMode()) {
            List<AbstractShape> allShapes = new ArrayList<>(currentShapes);
            multipleSelectionManager.selectAll(allShapes);
            
            System.out.println("[CONTROLLER] Selezionate tutte le shape: " + allShapes.size());
        }
    }

    /**
     * NUOVO: Deseleziona tutte le shape.
     */
    private void clearAllSelection() {
        multipleSelectionManager.clearSelection();
        mouseHandler.setSelectedShapeInstance(null);
        System.out.println("[CONTROLLER] Selezione pulita");
    }

    /**
     * NUOVO: Sposta tutte le shape selezionate insieme.
     */
    public void moveSelection(double deltaX, double deltaY) {
        if (multipleSelectionManager.hasSelection()) {
            List<Shape> selectedShapes = multipleSelectionManager.getSelectedShapes();
            
            MultiMoveCommand command = new MultiMoveCommand(selectedShapes, deltaX, deltaY);
            mouseHandler.applyUndoableStrategy(command);
            
            System.out.println("[CONTROLLER] Spostate " + selectedShapes.size() + " shape di (" + deltaX + ", " + deltaY + ")");
        }
    }

    /**
     * NUOVO: Aggiorna le informazioni visualizzate sulla selezione corrente.
     */
    private void updateSelectionInfo() {
        if (multipleSelectionManager == null) return;
        
        int count = multipleSelectionManager.getSelectionCount();
        boolean isMultiMode = multipleSelectionManager.isMultipleSelectionMode();
        
        // Aggiorna label contatore
        if (selectionCountLabel != null) {
            if (isMultiMode) {
                selectionCountLabel.setText(count + " sel.");
                selectionCountLabel.setStyle("-fx-font-size: 10; -fx-text-fill: " + 
                                           (count > 0 ? "blue" : "gray") + ";");
            } else {
                Shape singleSelected = mouseHandler.getSelectedShapeInstance();
                selectionCountLabel.setText(singleSelected != null ? "1 sel." : "0 sel.");
                selectionCountLabel.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");
            }
        }
        
        // Aggiorna label informazioni
        if (selectionInfoLabel != null) {
            if (isMultiMode) {
                if (count > 0) {
                    selectionInfoLabel.setText("Modalità multi-selezione: " + count + " shape selezionate");
                    selectionInfoLabel.setStyle("-fx-font-size: 11; -fx-text-fill: blue;");
                } else {
                    selectionInfoLabel.setText("Modalità multi-selezione: clicca sulle shape per selezionarle");
                    selectionInfoLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");
                }
            } else {
                Shape singleSelected = mouseHandler.getSelectedShapeInstance();
                if (singleSelected != null) {
                    selectionInfoLabel.setText("Selezionata: " + singleSelected.getClass().getSimpleName());
                    selectionInfoLabel.setStyle("-fx-font-size: 11; -fx-text-fill: green;");
                } else {
                    selectionInfoLabel.setText("Nessuna selezione");
                    selectionInfoLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");
                }
            }
        }
        
        // Aggiorna stato pulsanti
        if (selectAllButton != null) {
            selectAllButton.setDisable(!isMultiMode || currentShapes.isEmpty());
        }
        
        if (clearSelectionButton != null) {
            clearSelectionButton.setDisable(!isMultiMode || count == 0);
        }
    }

    /**
     * NUOVO: Gestisce le scorciatoie da tastiera per la selezione multipla.
     */
    public void setupKeyboardShortcuts() {
        drawingPane.setOnKeyPressed(event -> {
            if (event.isControlDown()) {
                switch (event.getCode()) {
                    case A -> {
                        // Ctrl+A: Seleziona tutto
                        if (multipleSelectionManager.isMultipleSelectionMode()) {
                            selectAllShapes();
                            event.consume();
                        }
                    }
                    case D -> {
                        // Ctrl+D: Deseleziona tutto
                        clearAllSelection();
                        event.consume();
                    }
                    case M -> {
                        // Ctrl+M: Toggle modalità multi-selezione
                        toggleMultipleSelectionMode();
                        event.consume();
                    }
                    case DELETE -> {
                        // Ctrl+Delete: Elimina selezione
                        deleteSelection();
                        event.consume();
                    }
                    default -> {}
                }
            } else if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                // ESC: Esce dalla modalità multi-selezione
                if (multipleSelectionManager.isMultipleSelectionMode()) {
                    multipleSelectionManager.setMultipleSelectionMode(false);
                    updateMultiSelectButtonAppearance();
                    event.consume();
                }
            }
        });
        
        // Assicura che il pane possa ricevere eventi tastiera
        drawingPane.setFocusTraversable(true);
    }

    /**
     * NUOVO: Aggiorna l'aspetto del pulsante multi-selezione.
     */
    private void updateMultiSelectButtonAppearance() {
        if (multiSelectButton == null) return;
        
        boolean isActive = multipleSelectionManager.isMultipleSelectionMode();
        
        if (isActive) {
            multiSelectButton.setStyle("-fx-background-color: lightblue; -fx-text-fill: darkblue; -fx-font-weight: bold;");
            multiSelectButton.setText("🔲 Multi-Select ON");
        } else {
            multiSelectButton.setStyle("-fx-font-weight: bold;");
            multiSelectButton.setText("🔲 Multi-Select");
        }
    }

    /**
     * NUOVO: Gestisce il click destro per menu contestuale.
     */
    private void handleRightClick(MouseEvent e) {
        if (e.isSecondaryButtonDown()) {
            if (multipleSelectionManager.isMultipleSelectionMode() && multipleSelectionManager.hasSelection()) {
                showMultiSelectionContextMenu(e.getScreenX(), e.getScreenY());
            }
            e.consume();
        }
    }

    /**
     * NUOVO: Mostra un menu contestuale per la selezione multipla.
     */
    private void showMultiSelectionContextMenu(double x, double y) {
        if (!multipleSelectionManager.hasSelection()) return;
        
        ContextMenu contextMenu = new ContextMenu();
        
        // Opzioni del menu
        MenuItem deleteItem = new MenuItem("Elimina selezione");
        deleteItem.setOnAction(e -> deleteSelection());
        
        MenuItem copyItem = new MenuItem("Copia selezione");
        copyItem.setOnAction(e -> copySelection());
        
        MenuItem toFrontItem = new MenuItem("Porta in primo piano");
        toFrontItem.setOnAction(e -> bringSelectionToFront());
        
        MenuItem toBackItem = new MenuItem("Porta in secondo piano");
        toBackItem.setOnAction(e -> sendSelectionToBack());
        
        SeparatorMenuItem separator = new SeparatorMenuItem();
        
        MenuItem infoItem = new MenuItem("Info selezione");
        infoItem.setOnAction(e -> showSelectionDetails());
        
        MenuItem duplicateItem = new MenuItem("Duplica selezione");
        duplicateItem.setOnAction(e -> duplicateSelection());
        
        contextMenu.getItems().addAll(
            deleteItem, copyItem, duplicateItem, separator, 
            toFrontItem, toBackItem, separator, 
            infoItem
        );
        
        contextMenu.show(drawingPane, x, y);
    }

    /**
     * NUOVO: Imposta tutti i listener necessari per la selezione multipla.
     */
    private void setupMultiSelectionListeners() {
        // Listener per click destro
        drawingPane.setOnMouseClicked(event -> {
            if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                handleRightClick(event);
            } else {
                mouseHandler.onMouseClick(event);
            }
        });
        
        // Listener per aggiornamento stato controlli
        Platform.runLater(() -> {
            javafx.animation.Timeline controlsUpdateTimer = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(300), e -> updateControlsState())
            );
            controlsUpdateTimer.setCycleCount(javafx.animation.Timeline.INDEFINITE);
            controlsUpdateTimer.play();
        });
    }

    /**
     * NUOVO: Abilita/disabilita i controlli in base alla modalità corrente.
     */
    private void updateControlsState() {
        boolean hasMultiSelection = multipleSelectionManager.hasSelection();
        boolean hasSingleSelection = mouseHandler.getSelectedShapeInstance() != null;
        boolean hasAnySelection = hasMultiSelection || hasSingleSelection;
        
        // Abilita/disabilita controlli colore
        strokePicker.setDisable(!hasAnySelection);
        fillPicker.setDisable(!hasAnySelection);
        
        // Abilita/disabilita slider rotazione
        rotateSlider.setDisable(!hasAnySelection);
        
        // Abilita/disabilita pulsanti operazioni
        deleteButton.setDisable(!hasAnySelection);
        copyButton.setDisable(!hasAnySelection);
        cutButton.setDisable(!hasAnySelection);
        bringToFrontButton.setDisable(!hasAnySelection);
        sendToBackButton.setDisable(!hasAnySelection);
    }

    /**
     * NUOVO: Mostra i dettagli della selezione corrente.
     */
    private void showSelectionDetails() {
        if (!multipleSelectionManager.hasSelection()) return;
        
        String details = getSelectionDebugInfo();
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dettagli Selezione");
        alert.setHeaderText("Informazioni sulla selezione corrente");
        alert.setContentText(details);
        alert.showAndWait();
    }

    /**
     * NUOVO: Restituisce informazioni sulla selezione corrente per debug.
     */
    public String getSelectionDebugInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Modalità multi-selezione: ").append(multipleSelectionManager.isMultipleSelectionMode()).append("\n");
        info.append(multipleSelectionManager.getSelectionInfo()).append("\n");
        
        if (multipleSelectionManager.hasSelection()) {
            double[] bbox = multipleSelectionManager.getSelectionBoundingBox();
            double[] center = multipleSelectionManager.getSelectionCenter();
            info.append("Bounding box: (").append(String.format("%.1f", bbox[0])).append(", ")
                .append(String.format("%.1f", bbox[1])).append(") - (")
                .append(String.format("%.1f", bbox[2])).append(", ")
                .append(String.format("%.1f", bbox[3])).append(")\n");
            info.append("Centro selezione: (").append(String.format("%.1f", center[0]))
                .append(", ").append(String.format("%.1f", center[1])).append(")");
        }
        
        return info.toString();
    }

    /**
     * NUOVO: Duplica tutte le shape selezionate.
     */
    public void duplicateSelection() {
        if (!multipleSelectionManager.hasSelection()) {
            System.out.println("[DUPLICATE] Nessuna selezione da duplicare");
            return;
        }
        
        List<Shape> selectedShapes = multipleSelectionManager.getSelectedShapes();
        List<AbstractShape> duplicatedShapes = new ArrayList<>();
        
        // Offset per le copie
        double offsetX = 20;
        double offsetY = 20;
        
        for (Shape shape : selectedShapes) {
            try {
                Shape clonedShape = shape.clone();
                if (clonedShape != null) {
                    // Sposta la copia
                    clonedShape.setX(clonedShape.getX() + offsetX);
                    clonedShape.setY(clonedShape.getY() + offsetY);
                    
                    // Aggiungi al canvas
                    AbstractShape abstractClone = AbstractShape.unwrapToAbstract(clonedShape);
                    currentShapes.add(abstractClone);
                    drawingPane.getChildren().add(clonedShape.getNode());
                    clonedShape.getNode().setUserData(clonedShape);
                    
                    duplicatedShapes.add(abstractClone);
                }
            } catch (Exception e) {
                System.err.println("[DUPLICATE] Errore nella duplicazione: " + e.getMessage());
            }
        }
        
        if (!duplicatedShapes.isEmpty()) {
            // Seleziona le nuove copie
            multipleSelectionManager.clearSelection();
            multipleSelectionManager.selectAll(duplicatedShapes);
            
            System.out.println("[DUPLICATE] Duplicate " + duplicatedShapes.size() + " shape");
        }
    }

    @FXML
    public void handlePaste() {
        enablePasteMode();
    }

    /**
     * Zoom avanti applicando il fattore successivo.
     */
    @FXML
    private void onZoomIn() {
        double s = zoomManager.zoomIn();
        scaleTransform.setX(s);
        scaleTransform.setY(s);
    }

    /**
     * Zoom avanti applicando il fattore precedente.
     */
    @FXML
    private void onZoomOut() {
        double s = zoomManager.zoomOut();
        scaleTransform.setX(s);
        scaleTransform.setY(s);
    }

    /**
     * Annulla l'ultimo comando eseguito (undo).
     */
    @FXML
    private void onUndo() {
        commandInvoker.undo();
    }

    /**
     * Attiva la modalità click-per-incollare con gestione migliorata.
     */
    private void enablePasteMode() {
        Shape clipboardShape = mouseHandler.getClipboard();
        
        if (clipboardShape == null) {
            System.out.println("[PASTE MODE] Clipboard vuoto - nessuna operazione");
            return;
        }
        
        System.out.println("[PASTE MODE] Attivato: clicca sul canvas per incollare " + 
                          clipboardShape.getClass().getSimpleName());
        
        drawingPane.setOnMouseClicked(event -> {
            double clickX = event.getX();
            double clickY = event.getY();
            
            System.out.println("[PASTE MODE] Click rilevato su: " + clickX + ", " + clickY);
            
            // Esegui il comando paste migliorato
            commandInvoker.execute(new Paste(mouseHandler, shapeManager, clickX, clickY));
            
            // Ripristina il comportamento normale del mouse
            setupMultiSelectionListeners();
            
            System.out.println("[PASTE MODE] Modalità paste disattivata");
        });
    }
    
    private void onCanvasClickForText(MouseEvent event) {
        textFinalized = false;

        double x = event.getX();
        double y = event.getY();

        TextField textField = new TextField();
        textField.setLayoutX(x);
        textField.setLayoutY(y);
        textField.setPrefColumnCount(10);

        drawingPane.getChildren().add(textField);
        textField.requestFocus();

        // Disattiva subito la modalità testo dopo il primo click
        setTool(null); // Questo ripristina il comportamento del mouseHandler

        textField.setOnAction(e -> finalizeText(textField));
        textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                finalizeText(textField);
            }
        });
    }

    private boolean textFinalized = false;

    private void finalizeText(TextField textField) {
        if (textFinalized) {
            return;  // esce se già chiamato
        }
        textFinalized = true;

        setTool(null); // Questo ripristina il mouseHandler normale

        String text = textField.getText();

        if (text != null && !text.trim().isEmpty()) {
            double x = textField.getLayoutX();
            double y = textField.getLayoutY() + textField.getHeight() - 5;

            double fontSize = fontSizeSpinner.getValue();
            
            TextShapeCreator factory = new TextShapeCreator();
            Shape shape = factory.createShape(
                    text, x, y,
                    strokePicker.getValue() != null ? strokePicker.getValue() : javafx.scene.paint.Color.BLACK,
                    fontSize
            );

            currentShapes.add((AbstractShape) shape);
            commandInvoker.execute(new Add(shapeManager, shape));
            mouseHandler.setSelectedShapeInstance(shape);
        }

        drawingPane.getChildren().remove(textField); // Rimuove il box di input
    }
}