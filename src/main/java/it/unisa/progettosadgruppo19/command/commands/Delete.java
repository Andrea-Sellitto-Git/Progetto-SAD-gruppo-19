package it.unisa.progettosadgruppo19.command.commands;

import it.unisa.progettosadgruppo19.command.UndoableCommand;
import it.unisa.progettosadgruppo19.command.receivers.ShapeManagerReceiver;
import it.unisa.progettosadgruppo19.decorator.ShapeDecorator;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

/**
 * Comando undoable per rimuovere una {@link Shape} dal {@link ShapeManagerReceiver}.
 * Memorizza l'indice originale per permettere l'undo.
 */
public class Delete implements UndoableCommand {

    private final ShapeManagerReceiver shapeManager;
    private final Shape shape;
    private int index;

    /**
     * Costruisce un comando Delete per la shape indicata.
     *
     * @param shapeManager il receiver responsabile della gestione delle shape; non può essere {@code null}
     * @param shape la shape da rimuovere; non può essere {@code null}
     */
    public Delete(ShapeManagerReceiver shapeManager, Shape shape) {
        this.shapeManager = shapeManager;
        this.shape = shape;
        Shape baseShape = unwrap(shape);
        this.index = shapeManager.getShapeIndex(baseShape);
        System.out.println("[DELETE] Index salvato: " + index);
    }

    /**
     * Esegue la cancellazione: rimuove la shape dal manager.
     */
    @Override
    public void execute() {
        System.out.println("[DELETE] Rimozione: " + shape.getClass().getSimpleName());
        shapeManager.removeShape(shape);
    }

    /**
    * Annulla la cancellazione reinserendo la shape
    * alla posizione salvata.
    */    
    @Override
    public void undo() {
        System.out.println("[UNDO DELETE] Inserisco a indice: " + index);
        shapeManager.insertShapeAt(shape, index);
    }

    /**
    * Svolge il “nudo” della shape, rimuovendo eventuali
    * {@link ShapeDecorator} per ottenere l'istanza di base.
    *
    * @param shape la shape eventualmente decorata
    * @return la shape originale senza decorator
    */
    private Shape unwrap(Shape shape) {
        while (shape instanceof ShapeDecorator decorator) {
            shape = decorator.getWrapped();
        }
        return shape;
    }
}
