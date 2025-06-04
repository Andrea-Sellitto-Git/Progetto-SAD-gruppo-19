package it.unisa.progettosadgruppo19.command.multi;

import it.unisa.progettosadgruppo19.command.MouseMultiInputs;
import it.unisa.progettosadgruppo19.command.UndoableCommand;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.input.MouseEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Comando per spostare multiple shape simultaneamente.
 */
public class MultiMoveCommand implements MouseMultiInputs, UndoableCommand {

    private final List<Shape> shapes;
    private final double deltaX;
    private final double deltaY;
    private final Map<Shape, Double> originalX;
    private final Map<Shape, Double> originalY;

    public MultiMoveCommand(List<Shape> shapes, double deltaX, double deltaY) {
        this.shapes = shapes;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.originalX = new HashMap<>();
        this.originalY = new HashMap<>();

        // Salva le posizioni originali
        for (Shape shape : shapes) {
            originalX.put(shape, shape.getX());
            originalY.put(shape, shape.getY());
        }
    }

    @Override
    public void execute() {
        System.out.println("[MULTI-MOVE] Spostamento di " + shapes.size() + " shape di (" + deltaX + ", " + deltaY + ")");
        for (Shape shape : shapes) {
            double newX = originalX.get(shape) + deltaX;
            double newY = originalY.get(shape) + deltaY;
            shape.setX(newX);
            shape.setY(newY);
        }
    }

    @Override
    public void undo() {
        System.out.println("[MULTI-MOVE UNDO] Ripristino posizioni originali di " + shapes.size() + " shape");
        for (Shape shape : shapes) {
            shape.setX(originalX.get(shape));
            shape.setY(originalY.get(shape));
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
        execute();
    }

    @Override
    public void onMouseClick(MouseEvent e) {
    }

    @Override
    public boolean isExecutable() {
        return true;
    }
}
