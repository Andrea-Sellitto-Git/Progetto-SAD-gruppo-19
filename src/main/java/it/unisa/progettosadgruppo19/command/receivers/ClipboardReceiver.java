package it.unisa.progettosadgruppo19.command.receivers;

import it.unisa.progettosadgruppo19.model.shapes.Shape;

/**
 * Interfaccia che definisce il contratto per il receiver del clipboard,
 * responsabile di memorizzare e recuperare una {@link Shape}.
 */
public interface ClipboardReceiver {

    /**
     * Imposta la {@link Shape} corrente nel clipboard.
     * Se l'argomento è {@code null}, il clipboard viene svuotato.
     *
     * @param shape la shape da memorizzare nel clipboard; {@code null} per svuotarlo
     */
    void setClipboard(Shape shape);

    /**
     * Restituisce la {@link Shape} attualmente memorizzata nel clipboard.
     *
     * @return la shape salvata nel clipboard, o {@code null} se il clipboard è vuoto
     */
    Shape getClipboard();
}
