package it.unisa.progettosadgruppo19.command;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

public class ChangeFill implements MouseMultiInputs, UndoableCommand {

    private final Shape shape;
    private final Color oldFill;
    private final Color newFill;

    public ChangeFill(Shape shape, Color oldFill, Color newFill) {
        this.shape = shape;
        this.oldFill = oldFill;
        this.newFill = newFill;
    }

    @Override
    public void execute() {
        ((javafx.scene.shape.Shape) shape.getNode()).setFill(newFill);
    }

    @Override
    public void undo() {
        ((javafx.scene.shape.Shape) shape.getNode()).setFill(oldFill);
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
