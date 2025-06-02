package it.unisa.progettosadgruppo19.command;

import it.unisa.progettosadgruppo19.command.receiver.ShapeManagerReceiver;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.input.MouseEvent;

public class Create implements MouseMultiInputs, UndoableCommand {

    private final ShapeManagerReceiver shapeManager;
    private final Shape shape;

    public Create(ShapeManagerReceiver shapeManager, Shape shape) {
        this.shapeManager = shapeManager;
        this.shape = shape;
    }

    @Override
    public void execute() {
        if (!shapeManager.containsNode(shape.getNode())) {
            shapeManager.addShape(shape);
        } else {
            shapeManager.registerOnly(shape); // solo currentShapes.add(...)
        }
    }

    @Override
    public void undo() {
        shapeManager.removeShape(shape);
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
