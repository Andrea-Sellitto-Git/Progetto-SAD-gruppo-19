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
 * Comando per cambiare il colore di riempimento di multiple shape.
 */
public class MultiChangeFillCommand implements MouseMultiInputs, UndoableCommand {
    
    private final List<Shape> shapes;
    private final Color newFill;
    private final Map<Shape, Color> originalFills;
    
    public MultiChangeFillCommand(List<Shape> shapes, Color newFill) {
        this.shapes = shapes;
        this.newFill = newFill;
        this.originalFills = new HashMap<>();
        
        // Salva i colori originali
        for (Shape shape : shapes) {
            javafx.scene.shape.Shape fxShape = (javafx.scene.shape.Shape) shape.getNode();
            originalFills.put(shape, (Color) fxShape.getFill());
        }
    }
    
    @Override
    public void execute() {
        System.out.println("[MULTI-FILL] Cambio fill di " + shapes.size() + " shape a " + newFill);
        for (Shape shape : shapes) {
            javafx.scene.shape.Shape fxShape = (javafx.scene.shape.Shape) shape.getNode();
            fxShape.setFill(newFill);
        }
    }
    
    @Override
    public void undo() {
        System.out.println("[MULTI-FILL UNDO] Ripristino fill originali di " + shapes.size() + " shape");
        for (Shape shape : shapes) {
            javafx.scene.shape.Shape fxShape = (javafx.scene.shape.Shape) shape.getNode();
            fxShape.setFill(originalFills.get(shape));
        }
    }
    
    @Override
    public void onPressed(MouseEvent e) {}
    
    @Override
    public void onDragged(MouseEvent e) {}
    
    @Override
    public void onReleased(MouseEvent e) {}
    
    @Override
    public void onMouseClick(MouseEvent e) {}
    
    @Override
    public boolean isExecutable() {
        return true;
    }
}