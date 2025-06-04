package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.command.polygon.CreateFreeFormPolygon;
import it.unisa.progettosadgruppo19.command.receivers.ShapeManagerReceiver;
import it.unisa.progettosadgruppo19.model.shapes.FreeFormPolygonShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import it.unisa.progettosadgruppo19.decorator.FillDecorator;
import it.unisa.progettosadgruppo19.decorator.StrokeDecorator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestore degli eventi mouse per disegnare un poligono a forma libera.
 * VERSIONE CORRETTA che assicura UserData corretto per la selezione.
 */
public class PolygonMouseEventHandler {

    private final Pane drawingPane;
    private final List<FreeFormPolygonShape> polygonList;
    private final ShapeManagerReceiver shapeManager;
    private final Color strokeColor, fillColor;
    private final double tol = 6.0;
    private final Runnable onComplete;

    private FreeFormPolygonShape basePolygon = null;
    private Shape decoratedPolygon = null;
    private Circle startIndicator = null;
    private List<Circle> vertexIndicators = new ArrayList<>();
    private double firstX, firstY;
    private boolean isActive = true;

    public PolygonMouseEventHandler(Pane drawingPane,
                                    List<FreeFormPolygonShape> polygonList,
                                    ShapeManagerReceiver shapeManager,
                                    Color strokeColor,
                                    Color fillColor,
                                    Runnable onComplete) {
        this.drawingPane = drawingPane;
        this.polygonList = polygonList;
        this.shapeManager = shapeManager;
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;
        this.onComplete = onComplete;

        drawingPane.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClick);
    }

    private void addVertexIndicator(double x, double y) {
        Circle indicator = new Circle(x, y, 3, Color.LIGHTBLUE);
        indicator.setStroke(Color.DARKBLUE);
        indicator.setStrokeWidth(1.0);
        indicator.setMouseTransparent(true);
        drawingPane.getChildren().add(indicator);
        vertexIndicators.add(indicator);
    }

    private void removeAllVertexIndicators() {
        drawingPane.getChildren().removeAll(vertexIndicators);
        vertexIndicators.clear();
    }

    private void updateStartIndicator() {
        if (startIndicator != null && basePolygon != null) {
            if (basePolygon.canClose()) {
                startIndicator.setStroke(Color.GREEN);
                startIndicator.setStrokeWidth(2.0);
            } else {
                startIndicator.setStroke(Color.GRAY);
                startIndicator.setStrokeWidth(1.0);
            }
        }
    }

    /**
     * CORREZIONE CRITICA: Assicura che UserData sia sempre impostato correttamente
     */
    private void setUserDataSafely() {
        if (decoratedPolygon != null && decoratedPolygon.getNode() != null) {
            try {
                // Verifica se UserData è già impostato correttamente
                Object currentUserData = decoratedPolygon.getNode().getUserData();
                if (currentUserData == null || currentUserData != decoratedPolygon) {
                    decoratedPolygon.getNode().setUserData(decoratedPolygon);
                    System.out.println("[POLYGON] UserData impostato/corretto");
                    
                    // Verifica doppia
                    Object verifyUserData = decoratedPolygon.getNode().getUserData();
                    if (verifyUserData != decoratedPolygon) {
                        System.err.println("[POLYGON] ERRORE CRITICO: UserData non si è impostato correttamente!");
                    } else {
                        System.out.println("[POLYGON] UserData verificato con successo");
                    }
                }
            } catch (Exception e) {
                System.err.println("[POLYGON] Errore nell'impostazione UserData: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void onMouseClick(MouseEvent e) {
        if (!isActive) {
            return;
        }

        double x = e.getX();
        double y = e.getY();

        // 1) Se non ho un poligono in costruzione, creo il primo vertice
        if (basePolygon == null) {
            basePolygon = new FreeFormPolygonShape(x, y, strokeColor);
            polygonList.add(basePolygon);

            // Creo la versione decorata con stroke e fill
            decoratedPolygon = new StrokeDecorator(basePolygon, strokeColor);
            decoratedPolygon = new FillDecorator(decoratedPolygon, fillColor);

            // CORREZIONE CRITICA: Assicura UserData IMMEDIATAMENTE
            setUserDataSafely();
            
            // Aggiungo il nodo decorato al Pane
            drawingPane.getChildren().add(decoratedPolygon.getNode());
            
            // Riverifica UserData dopo l'aggiunta al pane
            setUserDataSafely();

            // Disegno l'indicatore attorno al primo vertice
            firstX = x;
            firstY = y;
            startIndicator = new Circle(firstX, firstY, tol, Color.TRANSPARENT);
            startIndicator.setStroke(Color.GRAY);
            startIndicator.setStrokeWidth(1.0);
            startIndicator.setMouseTransparent(true);
            drawingPane.getChildren().add(startIndicator);

            addVertexIndicator(x, y);
            
            System.out.println("[POLYGON] Primo vertice aggiunto. UserData verificato: " + 
                             (decoratedPolygon.getNode().getUserData() != null));
            return;
        }

        // 2) Se ho già un poligono in costruzione, verifico se il click è vicino al primo vertice
        if (basePolygon.isNearStart(x, y, tol)) {
            if (basePolygon.canClose()) {
                // a) Chiudo il poligono
                basePolygon.closePolygon();

                // b) Rimuovo tutti gli indicatori
                drawingPane.getChildren().remove(startIndicator);
                startIndicator = null;
                removeAllVertexIndicators();

                // c) CORREZIONE CRITICA: Verifica e corregge UserData prima del commit
                setUserDataSafely();

                // d) Eseguo il comando undoable per "committare" il poligono finale
                CreateFreeFormPolygon cmd = new CreateFreeFormPolygon(shapeManager, decoratedPolygon);
                cmd.execute();

                System.out.println("[POLYGON] Poligono chiuso con " + basePolygon.getVertexCount() + " vertici.");
                System.out.println("[POLYGON] UserData finale verificato: " + 
                                 (decoratedPolygon.getNode().getUserData() != null));
                
                if (basePolygon.getVertexCount() >= 3) {
                    System.out.println("[POLYGON] Area: " + String.format("%.2f", basePolygon.calculateArea()));
                    System.out.println("[POLYGON] Perimetro: " + String.format("%.2f", basePolygon.calculatePerimeter()));
                    System.out.println("[POLYGON] Convesso: " + basePolygon.isConvex());
                }
                
                // e) Reset delle variabili prima di disattivare
                basePolygon = null;
                decoratedPolygon = null;
                
                // f) Disattiva l'handler
                isActive = false;
                
                // g) Detach e callback
                detach();
                
                if (onComplete != null) {
                    onComplete.run();
                }
                
                System.out.println("[POLYGON] Strumento poligono disattivato automaticamente.");
            } else {
                System.out.println("[POLYGON] Serve almeno 1 vertice aggiuntivo per chiudere il poligono. Attuali: " + basePolygon.getVertexCount());
            }
            return;
        }

        // 3) Aggiungo un vertice
        if (basePolygon != null) {
            basePolygon.addPoint(x, y);
            addVertexIndicator(x, y);
            updateStartIndicator();
            
            // CORREZIONE: Ricontrolla UserData dopo ogni modifica
            setUserDataSafely();
            
            System.out.println("[POLYGON] Vertice " + basePolygon.getVertexCount() + " aggiunto.");
            if (basePolygon.canClose()) {
                System.out.println("[POLYGON] Il poligono può ora essere chiuso cliccando sul primo vertice.");
            }
        }
    }

    public void detach() {
        drawingPane.removeEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClick);

        if (startIndicator != null) {
            drawingPane.getChildren().remove(startIndicator);
            startIndicator = null;
        }

        removeAllVertexIndicators();

        if (decoratedPolygon != null && basePolygon != null) {
            drawingPane.getChildren().remove(decoratedPolygon.getNode());
            polygonList.remove(basePolygon);
            decoratedPolygon = null;
            basePolygon = null;
            System.out.println("[POLYGON] Poligono incompleto rimosso.");
        }
        
        isActive = false;
    }

    public int getCurrentVertexCount() {
        return basePolygon != null ? basePolygon.getVertexCount() : 0;
    }

    public boolean isPolygonInProgress() {
        return basePolygon != null && isActive;
    }

    public boolean canCloseCurrentPolygon() {
        return basePolygon != null && basePolygon.canClose() && isActive;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void reactivate() {
        isActive = true;
        System.out.println("[POLYGON] Handler riattivato.");
    }
}