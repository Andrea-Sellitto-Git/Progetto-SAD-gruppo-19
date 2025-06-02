package it.unisa.progettosadgruppo19.command;

import it.unisa.progettosadgruppo19.command.receiver.ClipboardReceiver;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

/**
 * Comando undoable per copiare una {@link Shape} nel {@link ClipboardReceiver}.
 */
public class Copy implements Command {

    private final ClipboardReceiver receiver;
    private final Shape shape;

    /**
     * Costruisce un comando Copy per la shape indicata.
     *
     * @param receiver il receiver del clipboard in cui salvare la clone della shape; non può essere {@code null}
     * @param shape la shape da copiare; non può essere {@code null}
     */
    public Copy(ClipboardReceiver receiver, Shape shape) {
        this.receiver = receiver;
        this.shape = shape;
    }

    /**
     * Esegue la copia creando un clone della shape e impostandolo nel clipboard.
     * Stampa un messaggio di debug su console.
     */
    @Override
    public void execute() {
        receiver.setClipboard(shape.clone());
        System.out.println("[COPY] Figura copiata: " + shape.getClass().getSimpleName());
    }

}
