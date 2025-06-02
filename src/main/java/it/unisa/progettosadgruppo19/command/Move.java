package it.unisa.progettosadgruppo19.command;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class Move implements MouseMultiInputs, UndoableCommand {

    private final Shape shape;
    private final double oldX1, oldY1, oldX2, oldY2;
    private final double newX1, newY1, newX2, newY2;
    private boolean executed;

    public Move(Shape shape,
            double oldX1, double oldY1, double newX1, double newY1) {
        this.shape = shape;
        this.oldX1 = oldX1;
        this.oldY1 = oldY1;
        this.oldX2 = Double.NaN;
        this.oldY2 = Double.NaN;
        this.newX1 = newX1;
        this.newY1 = newY1;
        this.newX2 = Double.NaN;
        this.newY2 = Double.NaN;
    }

    public Move(Shape shape,
            double oldX1, double oldY1, double oldX2, double oldY2,
            double newX1, double newY1, double newX2, double newY2) {
        this.shape = shape;
        this.oldX1 = oldX1;
        this.oldY1 = oldY1;
        this.oldX2 = oldX2;
        this.oldY2 = oldY2;
        this.newX1 = newX1;
        this.newY1 = newY1;
        this.newX2 = newX2;
        this.newY2 = newY2;
    }

    @Override
    public void execute() {
        Node node = shape.getNode();
        if (node instanceof Line line && !Double.isNaN(newX2)) {
            line.setStartX(newX1);
            line.setStartY(newY1);
            line.setEndX(newX2);
            line.setEndY(newY2);
        } else if (node instanceof Rectangle rect) {
            double width = rect.getWidth();
            double height = rect.getHeight();
            double boundedX = Math.max(0, Math.min(newX1, rect.getParent().getLayoutBounds().getWidth() - width));
            double boundedY = Math.max(0, Math.min(newY1, rect.getParent().getLayoutBounds().getHeight() - height));
            rect.setX(boundedX);
            rect.setY(boundedY);
        } else if (node instanceof Ellipse ell) {
            double rx = ell.getRadiusX();
            double ry = ell.getRadiusY();
            double boundedCX = Math.max(rx, Math.min(newX1, ell.getParent().getLayoutBounds().getWidth() - rx));
            double boundedCY = Math.max(ry, Math.min(newY1, ell.getParent().getLayoutBounds().getHeight() - ry));
            ell.setCenterX(boundedCX);
            ell.setCenterY(boundedCY);
        } else if (node instanceof Text text) {
            double boundedX = Math.max(0, Math.min(newX1, text.getParent().getLayoutBounds().getWidth()));
            double boundedY = Math.max(0, Math.min(newY1, text.getParent().getLayoutBounds().getHeight()));
            text.setX(boundedX);
            text.setY(boundedY);
        }

        executed = true;
    }

    @Override
    public void undo() {
        Node node = shape.getNode();
        if (node instanceof Line line && !Double.isNaN(oldX2)) {
            line.setStartX(oldX1);
            line.setStartY(oldY1);
            line.setEndX(oldX2);
            line.setEndY(oldY2);
        } else if (node instanceof Rectangle rect) {
            rect.setX(oldX1);
            rect.setY(oldY1);
        } else if (node instanceof Ellipse ell) {
            ell.setCenterX(oldX1);
            ell.setCenterY(oldY1);
        } else if (node instanceof Text text) {
            text.setX(oldX1);
            text.setY(oldY1);
        }

    }

    @Override
    public boolean isExecutable() {
        return executed;
    }

    @Override
    public void onPressed(MouseEvent e) {
    }

    @Override
    public void onDragged(MouseEvent e) {
    }

    @Override
    public void onReleased(MouseEvent e) {
        execute();
    }

    @Override
    public void onMouseClick(MouseEvent e) {
    }

    @Override
    public String toString() {
        return "Move";
    }
}
