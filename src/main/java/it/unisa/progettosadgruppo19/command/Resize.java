package it.unisa.progettosadgruppo19.command;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;

public class Resize implements MouseMultiInputs, UndoableCommand {

    private final Shape shape;

    // Rectangle
    private double oldX, oldY, oldW, oldH;
    private double newX, newY, newW, newH;

    // Ellipse
    private double oldCX, oldCY, oldRX, oldRY;
    private double newCX, newCY, newRX, newRY;

    // Line
    private double oldStartX, oldStartY, oldEndX, oldEndY;
    private double newStartX, newStartY, newEndX, newEndY;

    public Resize(Shape shape,
            double oldX, double oldY, double oldW, double oldH,
            double newX, double newY, double newW, double newH) {
        this.shape = shape;

        if (shape.getNode() instanceof Rectangle) {
            this.oldX = oldX;
            this.oldY = oldY;
            this.oldW = oldW;
            this.oldH = oldH;
            this.newX = newX;
            this.newY = newY;
            this.newW = newW;
            this.newH = newH;
        } else if (shape.getNode() instanceof Ellipse ell) {
            this.oldCX = ell.getCenterX();
            this.oldCY = ell.getCenterY();
            this.oldRX = ell.getRadiusX();
            this.oldRY = ell.getRadiusY();

            this.newCX = newX;
            this.newCY = newY;
            this.newRX = newW;
            this.newRY = newH;
        } else if (shape.getNode() instanceof Line line) {
            this.oldStartX = line.getStartX();
            this.oldStartY = line.getStartY();
            this.oldEndX = line.getEndX();
            this.oldEndY = line.getEndY();

            this.newStartX = newX;
            this.newStartY = newY;
            this.newEndX = newW;
            this.newEndY = newH;
        }
    }

    @Override
    public void execute() {
        if (shape.getNode() instanceof Rectangle rect) {
            rect.setX(newX);
            rect.setY(newY);
            rect.setWidth(newW);
            rect.setHeight(newH);
        } else if (shape.getNode() instanceof Ellipse ell) {
            ell.setCenterX(newCX);
            ell.setCenterY(newCY);
            ell.setRadiusX(newRX);
            ell.setRadiusY(newRY);
        } else if (shape.getNode() instanceof Line line) {
            line.setStartX(newStartX);
            line.setStartY(newStartY);
            line.setEndX(newEndX);
            line.setEndY(newEndY);
        }
    }

    @Override
    public void undo() {
        if (shape.getNode() instanceof Rectangle rect) {
            rect.setX(oldX);
            rect.setY(oldY);
            rect.setWidth(oldW);
            rect.setHeight(oldH);
        } else if (shape.getNode() instanceof Ellipse ell) {
            ell.setCenterX(oldCX);
            ell.setCenterY(oldCY);
            ell.setRadiusX(oldRX);
            ell.setRadiusY(oldRY);
        } else if (shape.getNode() instanceof Line line) {
            line.setStartX(oldStartX);
            line.setStartY(oldStartY);
            line.setEndX(oldEndX);
            line.setEndY(oldEndY);
        }
    }

    @Override
    public void onPressed(MouseEvent e) {
    }

    @Override
    public void onDragged(MouseEvent e) {
    }

    @Override
    public void onReleased(MouseEvent e) {
    }

    @Override
    public void onMouseClick(MouseEvent e) {
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}
