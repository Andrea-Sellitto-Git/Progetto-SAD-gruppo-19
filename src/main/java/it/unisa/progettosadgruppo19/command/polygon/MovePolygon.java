package it.unisa.progettosadgruppo19.command.polygon;

import it.unisa.progettosadgruppo19.command.UndoableCommand;
import it.unisa.progettosadgruppo19.command.MouseMultiInputs;
import it.unisa.progettosadgruppo19.model.shapes.FreeFormPolygonShape;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * Comando undoable per spostare un poligono.
 * Gestisce lo spostamento di tutti i vertici del poligono.
 */
public class MovePolygon implements MouseMultiInputs, UndoableCommand {

    private final FreeFormPolygonShape polygonShape;
    private final double oldX, oldY;
    private final double newX, newY;
    private List<Double> oldPoints;
    private List<Double> newPoints;

    /**
     * Costruisce un comando di spostamento per il poligono.
     *
     * @param polygonShape il poligono da spostare
     * @param oldX coordinata X originale
     * @param oldY coordinata Y originale  
     * @param newX nuova coordinata X
     * @param newY nuova coordinata Y
     */
    public MovePolygon(FreeFormPolygonShape polygonShape, double oldX, double oldY, double newX, double newY) {
        this.polygonShape = polygonShape;
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
        
        // Calcola le differenze
        double deltaX = newX - oldX;
        double deltaY = newY - oldY;
        
        // Salva i punti originali e calcola quelli nuovi
        this.oldPoints = new ArrayList<>(polygonShape.getPoints());
        this.newPoints = new ArrayList<>();
        
        for (int i = 0; i < oldPoints.size(); i += 2) {
            newPoints.add(oldPoints.get(i) + deltaX);      // X
            newPoints.add(oldPoints.get(i + 1) + deltaY);  // Y
        }
    }

    @Override
    public void execute() {
        applyPoints(newPoints);
        System.out.println("[MOVE POLYGON] Spostato da (" + oldX + ", " + oldY + ") a (" + newX + ", " + newY + ")");
    }

    @Override
    public void undo() {
        applyPoints(oldPoints);
        System.out.println("[UNDO MOVE POLYGON] Ripristinata posizione originale (" + oldX + ", " + oldY + ")");
    }

    /**
     * Applica i punti specificati al poligono.
     */
    private void applyPoints(List<Double> points) {
        try {
            // Aggiorna il nodo JavaFX
            Polygon fxPolygon = (Polygon) polygonShape.getNode();
            fxPolygon.getPoints().setAll(points);
            
            // Aggiorna anche la lista interna del poligono
            List<Double> internalPoints = polygonShape.getPoints();
            internalPoints.clear();
            internalPoints.addAll(points);
            
        } catch (Exception e) {
            System.err.println("[MOVE POLYGON ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onPressed(MouseEvent e) {
        // Non utilizzato per questo comando
    }

    @Override
    public void onDragged(MouseEvent e) {
        // Non utilizzato per questo comando
    }

    @Override
    public void onReleased(MouseEvent e) {
        // Non utilizzato per questo comando
    }

    @Override
    public void onMouseClick(MouseEvent e) {
        // Non utilizzato per questo comando
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}