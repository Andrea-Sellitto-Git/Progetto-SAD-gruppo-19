package it.unisa.progettosadgruppo19.command.polygon;

import it.unisa.progettosadgruppo19.command.UndoableCommand;
import it.unisa.progettosadgruppo19.command.MouseMultiInputs;
import it.unisa.progettosadgruppo19.model.shapes.FreeFormPolygonShape;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * Comando undoable per ridimensionare un poligono. Memorizza i punti originali
 * e quelli nuovi per supportare undo/redo.
 */
public class ResizePolygon implements MouseMultiInputs, UndoableCommand {

    private final FreeFormPolygonShape polygonShape;
    private final List<Double> oldPoints;
    private final List<Double> newPoints;

    /**
     * Costruisce un comando di ridimensionamento per il poligono.
     *
     * @param polygonShape il poligono da ridimensionare
     * @param oldPoints i punti originali prima del ridimensionamento
     * @param newPoints i punti dopo il ridimensionamento
     */
    public ResizePolygon(FreeFormPolygonShape polygonShape, List<Double> oldPoints, List<Double> newPoints) {
        this.polygonShape = polygonShape;
        this.oldPoints = new ArrayList<>(oldPoints);
        this.newPoints = new ArrayList<>(newPoints);
    }

    @Override
    public void execute() {
        applyPoints(newPoints);
        System.out.println("[RESIZE POLYGON] Applicati nuovi punti: " + newPoints.size() / 2 + " vertici");
    }

    @Override
    public void undo() {
        applyPoints(oldPoints);
        System.out.println("[UNDO RESIZE POLYGON] Ripristinati punti originali: " + oldPoints.size() / 2 + " vertici");
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
            System.err.println("[RESIZE POLYGON ERROR] " + e.getMessage());
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
