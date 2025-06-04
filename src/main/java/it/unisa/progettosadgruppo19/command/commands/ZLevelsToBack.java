package it.unisa.progettosadgruppo19.command.commands;

import it.unisa.progettosadgruppo19.command.UndoableCommand;
import it.unisa.progettosadgruppo19.command.receivers.ZOrderReceiver;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

/**
 * Comando che porta una shape in secondo piano (primo nella lista dei figli).
 */
public class ZLevelsToBack implements UndoableCommand {

    private final ZOrderReceiver receiver;
    private final Shape shape;
    private final int targetIndex;
    private int oldIndex;

    /**
     * Costruisce il comando per portare la shape in secondo piano.
     *
     * @param receiver    il receiver per modificare l'ordine Z; non può essere {@code null}
     * @param shape       la shape da spostare; non può essere {@code null}
     * @param targetIndex l'indice di destinazione per il livello Z
     */
    public ZLevelsToBack(ZOrderReceiver receiver, Shape shape, int targetIndex) {
        this.receiver = receiver;
        this.shape = shape;
        this.targetIndex = targetIndex;
    }

    /**
     * Esegue il comando salvando l'indice corrente
     * e impostando il nuovo indice Z specificato.
     */
    @Override
    public void execute() {
        oldIndex = receiver.getZIndex(shape);
        receiver.setZIndex(shape, targetIndex); // Inserisce sopra la griglia
    }

    /**
     * Annulla l'operazione ripristinando l'indice Z precedente.
     */
    @Override
    public void undo() {
        receiver.setZIndex(shape, oldIndex);
    }
}
