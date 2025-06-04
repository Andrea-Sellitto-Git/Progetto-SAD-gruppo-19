package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.command.receivers.ShapeManagerReceiver;
import it.unisa.progettosadgruppo19.command.receivers.ZOrderReceiver;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * Responsabile della gestione delle shape attive e del loro disegno sul canvas.
 */
public class ShapeManager implements ShapeManagerReceiver, ZOrderReceiver {

    private final List<AbstractShape> currentShapes;
    private final Pane drawingPane;

    /**
     * Costruisce un gestore di shape per la lista e il Pane specificati.
     *
     * @param currentShapes lista di shape gestite.
     * @param drawingPane Pane in cui visualizzare le shape.
     */
    public ShapeManager(List<AbstractShape> currentShapes, Pane drawingPane) {
        this.currentShapes = currentShapes;
        this.drawingPane = drawingPane;
    }

    @Override
    public void addShape(Shape shape) {
        AbstractShape abs = AbstractShape.unwrapToAbstract(shape);
        currentShapes.add(abs);
        if (!drawingPane.getChildren().contains(shape.getNode())) {
            drawingPane.getChildren().add(shape.getNode());
        }

        shape.getNode().setUserData(shape);
    }

    @Override
    public void removeShape(Shape shape) {
        System.out.println("[REMOVE] Rimozione: " + shape.getClass().getSimpleName());
        if (shape instanceof AbstractShape abs) {
            currentShapes.remove(abs);
            drawingPane.getChildren().remove(abs.getNode());
        } else {
            AbstractShape unwrapped = AbstractShape.unwrapToAbstract(shape);
            currentShapes.remove(unwrapped);
            drawingPane.getChildren().remove(shape.getNode());
        }
    }

    public void clearAll() {
        currentShapes.clear();
        drawingPane.getChildren().clear();
    }

    public List<AbstractShape> getCurrentShapes() {
        return currentShapes;
    }

    public Pane getDrawingPane() {
        return drawingPane;
    }

    public AbstractShape findByNode(Node node) {
        return currentShapes.stream()
                .filter(s -> s.getNode().equals(node))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void insertShapeAt(Shape shape, int index) {
        AbstractShape abs = AbstractShape.unwrapToAbstract(shape);

        if (index < 0 || index > currentShapes.size()) {
            System.out.println("[INSERT] Indice currentShapes fuori range, aggiungo in fondo");
            currentShapes.add(abs);
        } else {
            currentShapes.add(index, abs);
        }

        if (index < 0 || index > drawingPane.getChildren().size()) {
            System.out.println("[INSERT] Indice drawingPane fuori range, aggiungo in fondo");
            drawingPane.getChildren().add(shape.getNode());
        } else {
            drawingPane.getChildren().add(index, shape.getNode());
        }

        System.out.println("[INSERT] Nodo aggiunto: " + shape.getClass().getSimpleName() + " all'indice " + index);
    }

    @Override
    public int getShapeIndex(Shape shape) {
        if (shape instanceof AbstractShape abs) {
            return currentShapes.indexOf(abs);
        }
        return -1;
    }

    @Override
    public int getZIndex(Shape shape) {
        return drawingPane.getChildren().indexOf(shape.getNode());
    }

    @Override
    public void setZIndex(Shape shape, int index) {
        Node node = shape.getNode();
        drawingPane.getChildren().remove(node);
        System.out.println("[Z-ORDER] Sposto " + shape.getClass().getSimpleName() + " a indice " + index);
        if (index >= drawingPane.getChildren().size()) {
            drawingPane.getChildren().add(node);
        } else {
            drawingPane.getChildren().add(index, node);
        }
    }

    @Override
    public void rotateShape(Shape shape, double degrees) {
        shape.setRotation(degrees);
    }

    public boolean containsNode(Node node) {
        return drawingPane.getChildren().contains(node);
    }

    @Override
    public void registerOnly(Shape shape) {
        currentShapes.add(AbstractShape.unwrapToAbstract(shape));
    }

    @Override
    public void scale(Shape shape, double delta) {
        shape.onDrag(shape.getX() + shape.getWidth() + delta, shape.getY() + shape.getHeight() + delta);
    }
}
