package it.unisa.progettosadgruppo19.command.commands;

import it.unisa.progettosadgruppo19.command.UndoableCommand;
import it.unisa.progettosadgruppo19.command.receivers.ZOrderReceiver;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

/**
 * Comando undoable per portare una {@link Shape} in primo piano,
 * impostandone il livello Z all'indice massimo disponibile.
 */
public class ZLevelsToFront implements UndoableCommand {

    private final ZOrderReceiver receiver;
    private final Shape shape;
    private final int maxIndex;
    private int oldIndex;

    /**
     * Costruisce il comando per portare la shape in primo piano.
     *
     * @param receiver il receiver per modificare l'ordine Z; non può essere {@code null}
     * @param shape    la shape da spostare; non può essere {@code null}
     * @param maxIndex l'indice massimo valido per il livello Z
     */
    public ZLevelsToFront(ZOrderReceiver receiver, Shape shape, int maxIndex) {
        this.receiver = receiver;
        this.shape = shape;
        this.maxIndex = maxIndex;
    }

    /**
     * Esegue il comando salvando l'indice corrente
     * e impostando il nuovo indice Z al valore massimo.
     */
    @Override
    public void execute() {
        oldIndex = receiver.getZIndex(shape);
        receiver.setZIndex(shape, maxIndex); // Usa maxIndex valido
    }

     /**
     * Annulla l'operazione ripristinando l'indice Z precedente.
     */
    @Override
    public void undo() {
        receiver.setZIndex(shape, oldIndex);
    }
}
