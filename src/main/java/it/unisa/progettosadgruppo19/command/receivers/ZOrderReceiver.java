package it.unisa.progettosadgruppo19.command.receivers;

import it.unisa.progettosadgruppo19.model.shapes.Shape;

/**
 * Interfaccia per il receiver che gestisce l’ordine Z delle {@link Shape},
 * ovvero la profondità di rendering all’interno del drawing pane.
 */
public interface ZOrderReceiver {

    /**
     * Restituisce l’indice Z corrente della {@link Shape},
     * usato per determinare la sovrapposizione delle forme.
     *
     * @param shape la shape di cui ottenere l’indice Z; non può essere {@code null}
     * @return l’indice Z corrente della shape
     */
    int getZIndex(Shape shape);

    /**
     * Imposta un nuovo indice Z per la {@link Shape}, modificandone
     * la profondità di rendering.
     *
     * @param shape la shape di cui modificare l’indice Z; non può essere {@code null}
     * @param index il nuovo indice Z da assegnare
     */
    void setZIndex(Shape shape, int index);
}
