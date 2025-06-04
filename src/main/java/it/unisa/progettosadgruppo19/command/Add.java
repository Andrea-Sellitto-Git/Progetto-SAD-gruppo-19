package it.unisa.progettosadgruppo19.command;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import it.unisa.progettosadgruppo19.controller.ShapeManager;

public class Add implements UndoableCommand {

    private final ShapeManager manager;
    private final Shape shape;

    public Add(ShapeManager manager, Shape shape) {
        this.manager = manager;
        this.shape = shape;
    }

    @Override
    public void execute() {
        manager.addShape(shape);
    }

    @Override
    public void undo() {
        manager.removeShape(shape);
    }
}
