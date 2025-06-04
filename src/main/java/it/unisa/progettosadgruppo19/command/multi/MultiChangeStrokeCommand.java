package it.unisa.progettosadgruppo19.command.multi;

import it.unisa.progettosadgruppo19.command.MouseMultiInputs;
import it.unisa.progettosadgruppo19.command.UndoableCommand;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Comando per cambiare il colore del bordo di multiple shape.
 */
public class MultiChangeStrokeCommand implements MouseMultiInputs, UndoableCommand {

    private final List<Shape> shapes;
    private final Color newStroke;
    private final Map<Shape, Color> originalStrokes;

    public MultiChangeStrokeCommand(List<Shape> shapes, Color newStroke) {
        this.shapes = shapes;
        this.newStroke = newStroke;
        this.originalStrokes = new HashMap<>();

        // Salva i colori originali
        for (Shape shape : shapes) {
            javafx.scene.shape.Shape fxShape = (javafx.scene.shape.Shape) shape.getNode();
            originalStrokes.put(shape, (Color) fxShape.getStroke());
        }
    }

    @Override
    public void execute() {
        System.out.println("[MULTI-STROKE] Cambio stroke di " + shapes.size() + " shape a " + newStroke);
        for (Shape shape : shapes) {
            javafx.scene.shape.Shape fxShape = (javafx.scene.shape.Shape) shape.getNode();
            fxShape.setStroke(newStroke);
        }
    }

    @Override
    public void undo() {
        System.out.println("[MULTI-STROKE UNDO] Ripristino stroke originali di " + shapes.size() + " shape");
        for (Shape shape : shapes) {
            javafx.scene.shape.Shape fxShape = (javafx.scene.shape.Shape) shape.getNode();
            fxShape.setStroke(originalStrokes.get(shape));
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
