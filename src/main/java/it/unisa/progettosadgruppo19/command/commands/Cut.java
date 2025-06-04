package it.unisa.progettosadgruppo19.command.commands;

import it.unisa.progettosadgruppo19.command.UndoableCommand;
import it.unisa.progettosadgruppo19.command.receivers.ShapeManagerReceiver;
import it.unisa.progettosadgruppo19.command.receivers.ClipboardReceiver;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

/**
 * Comando undoable per tagliare (cut) una {@link Shape}: la rimuove dal
 * {@link ShapeManagerReceiver} e la pone nel {@link ClipboardReceiver}.
 */
public class Cut implements UndoableCommand {

    private final ClipboardReceiver clipboard;
    private final ShapeManagerReceiver shapeManager;
    private final Shape shape;
    private final AbstractShape unwrapped;
    private int index;

    /**
     * Costruisce un comando Cut per la shape indicata.
     *
     * @param clipboard il receiver del clipboard in cui salvare la clone della shape; non può essere {@code null}
     * @param shapeManager il receiver responsabile della gestione delle shape; non può essere {@code null}
     * @param shape la shape da tagliare; non può essere {@code null}
     */
    public Cut(ClipboardReceiver clipboard, ShapeManagerReceiver shapeManager, Shape shape) {
        this.clipboard = clipboard;
        this.shapeManager = shapeManager;
        this.shape = shape;
        this.unwrapped = unwrapToAbstract(shape);
        this.index = shapeManager.getShapeIndex(unwrapped);
    }

    /**
     * Esegue il cut: se l'indice è valido, clona la shape nel clipboard
     * e rimuove l'originale dal manager. Altrimenti stampa un errore.
     */
    @Override
    public void execute() {
        if (index < 0) {
            System.out.println("[CUT] Errore: shape non trovata per index");
            return;
        }
        clipboard.setClipboard(shape.clone());
        shapeManager.removeShape(shape);
        System.out.println("[CUT] Taglio figura: " + shape.getClass().getSimpleName());
    }

    /**
     * Annulla il cut: se l'indice è valido, reinserisce la shape
     * nella posizione originaria e svuota il clipboard.
     */
    @Override
    public void undo() {
        if (index < 0) {
            System.out.println("[UNDO CUT] Errore: indice non valido (" + index + ")");
            return;
        }
        shapeManager.insertShapeAt(shape, index);
        clipboard.setClipboard(null);
        System.out.println("[UNDO CUT] Figura reinserita a indice: " + index + " e clipboard svuotata");
    }

     /**
     * Rimuove eventuali decoratori dalla shape per ottenere un'istanza di {@link AbstractShape}.
     *
     * @param shape la shape eventualmente decorata
     * @return l'istanza di base non decorata
     */
    private AbstractShape unwrapToAbstract(Shape shape) {
        while (shape instanceof it.unisa.progettosadgruppo19.decorator.ShapeDecorator decorator) {
            shape = decorator.getWrapped();
        }
        return (AbstractShape) shape;
    }
}
