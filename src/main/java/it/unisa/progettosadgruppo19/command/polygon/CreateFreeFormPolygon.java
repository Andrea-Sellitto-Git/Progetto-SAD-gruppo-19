package it.unisa.progettosadgruppo19.command.polygon;

import it.unisa.progettosadgruppo19.command.UndoableCommand;
import it.unisa.progettosadgruppo19.command.receivers.ShapeManagerReceiver;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

/**
 * Comando undoable per l’inserimento di un FreeFormPolygonShape già completato.
 */
public class CreateFreeFormPolygon implements UndoableCommand {

    private final ShapeManagerReceiver shapeManager;
    private final Shape polygon;

    public CreateFreeFormPolygon(ShapeManagerReceiver shapeManager, Shape polygon) {
        this.shapeManager = shapeManager;
        this.polygon = polygon;
    }

    @Override
    public void execute() {
        if (!shapeManager.containsNode(polygon.getNode())) {
            shapeManager.addShape(polygon);
        } else {
            shapeManager.registerOnly(polygon);
        }
    }

    @Override
    public void undo() {
        shapeManager.removeShape(polygon);
    }
}
