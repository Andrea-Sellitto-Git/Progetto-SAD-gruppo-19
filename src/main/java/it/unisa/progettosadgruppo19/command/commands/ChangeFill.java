package it.unisa.progettosadgruppo19.command.commands;

import it.unisa.progettosadgruppo19.command.MouseMultiInputs;
import it.unisa.progettosadgruppo19.command.UndoableCommand;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

public class ChangeFill implements MouseMultiInputs, UndoableCommand {

    private final Shape shape;
    private final Color oldFill;
    private Color newFill;

    public ChangeFill(Shape shape, Color oldFill, Color newFill) {
        this.shape = shape;
        this.oldFill = oldFill;
        this.newFill = newFill;
    }

    @Override
    public void execute() {
        if (shape instanceof it.unisa.progettosadgruppo19.decorator.FillDecorator fd) {
            fd.setFill(newFill); // questo aggiorna anche il valore interno fill
        } else if (shape.getNode() instanceof javafx.scene.shape.Shape fxShape) {
            fxShape.setFill(newFill); // fallback
        }
    }

    @Override
    public void undo() {
        if (shape instanceof it.unisa.progettosadgruppo19.decorator.FillDecorator fd) {
            fd.setFill(oldFill); // stessa cosa per l'annullamento
        } else if (shape.getNode() instanceof javafx.scene.shape.Shape fxShape) {
            fxShape.setFill(oldFill); // fallback
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
