package it.unisa.progettosadgruppo19.command.commands;

import it.unisa.progettosadgruppo19.command.MouseMultiInputs;
import it.unisa.progettosadgruppo19.command.UndoableCommand;
import it.unisa.progettosadgruppo19.decorator.StrokeDecorator;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

public class ChangeStroke implements MouseMultiInputs, UndoableCommand {

    private final Shape shape;
    private Color oldStroke;
    private Color newStroke;

    public ChangeStroke(Shape shape, Color oldStroke, Color newStroke) {
        this.shape = shape;
        this.oldStroke = oldStroke;
        this.newStroke = newStroke;
    }

    @Override
    public void execute() {
        if (shape instanceof StrokeDecorator strokeDecorator) {
            strokeDecorator.setStroke(newStroke);
        } else {
            // fallback, se la shape non è decorata
            ((javafx.scene.shape.Shape) shape.getNode()).setStroke(newStroke);
        }
    }

    @Override
    public void undo() {
        if (shape instanceof StrokeDecorator strokeDecorator) {
            strokeDecorator.setStroke(oldStroke);
        } else {
            ((javafx.scene.shape.Shape) shape.getNode()).setStroke(oldStroke);
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
