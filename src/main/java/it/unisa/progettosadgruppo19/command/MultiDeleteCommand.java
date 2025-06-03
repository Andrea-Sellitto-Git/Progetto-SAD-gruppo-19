package it.unisa.progettosadgruppo19.command;

import it.unisa.progettosadgruppo19.command.receiver.ShapeManagerReceiver;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Comando per eliminare multiple shape simultaneamente.
 */
public class MultiDeleteCommand implements UndoableCommand {
    
    private final ShapeManagerReceiver shapeManager;
    private final List<Shape> shapesToDelete;
    private final Map<Shape, Integer> originalIndices;
    
    public MultiDeleteCommand(ShapeManagerReceiver shapeManager, List<Shape> shapes) {
        this.shapeManager = shapeManager;
        this.shapesToDelete = new ArrayList<>(shapes);
        this.originalIndices = new HashMap<>();
        
        // Salva gli indici originali per l'undo
        for (Shape shape : shapes) {
            originalIndices.put(shape, shapeManager.getShapeIndex(shape));
        }
    }
    
    @Override
    public void execute() {
        System.out.println("[MULTI-DELETE] Eliminazione di " + shapesToDelete.size() + " shape");
        for (Shape shape : shapesToDelete) {
            shapeManager.removeShape(shape);
        }
    }
    
    @Override
    public void undo() {
        System.out.println("[MULTI-DELETE UNDO] Ripristino di " + shapesToDelete.size() + " shape");
        // Ripristina in ordine di indice per mantenere l'ordine originale
        shapesToDelete.stream()
            .sorted((a, b) -> Integer.compare(originalIndices.get(a), originalIndices.get(b)))
            .forEach(shape -> {
                int originalIndex = originalIndices.get(shape);
                shapeManager.insertShapeAt(shape, originalIndex);
            });
    }
}