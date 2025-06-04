package it.unisa.progettosadgruppo19.command.multi;

import it.unisa.progettosadgruppo19.command.UndoableCommand;
import it.unisa.progettosadgruppo19.command.receivers.ShapeManagerReceiver;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Comando per ruotare multiple shape simultaneamente.
 */
public class MultiRotateCommand implements UndoableCommand {

    private final ShapeManagerReceiver shapeManager;
    private final List<Shape> shapes;
    private final double newRotation;
    private final Map<Shape, Double> originalRotations;

    public MultiRotateCommand(ShapeManagerReceiver shapeManager, List<Shape> shapes, double rotation) {
        this.shapeManager = shapeManager;
        this.shapes = shapes;
        this.newRotation = rotation;
        this.originalRotations = new HashMap<>();

        // Salva le rotazioni originali
        for (Shape shape : shapes) {
            originalRotations.put(shape, shape.getRotation());
        }
    }

    @Override
    public void execute() {
        System.out.println("[MULTI-ROTATE] Rotazione di " + shapes.size() + " shape a " + newRotation + "°");
        for (Shape shape : shapes) {
            shapeManager.rotateShape(shape, newRotation);
        }
    }

    @Override
    public void undo() {
        System.out.println("[MULTI-ROTATE UNDO] Ripristino rotazioni originali di " + shapes.size() + " shape");
        for (Shape shape : shapes) {
            shapeManager.rotateShape(shape, originalRotations.get(shape));
        }
    }
}
