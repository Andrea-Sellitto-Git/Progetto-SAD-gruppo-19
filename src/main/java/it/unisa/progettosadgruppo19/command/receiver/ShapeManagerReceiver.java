package it.unisa.progettosadgruppo19.command.receiver;

import it.unisa.progettosadgruppo19.model.shapes.Shape;

/**
 * Interfaccia che definisce le operazioni di gestione delle {@link Shape}
 * all’interno del contesto di disegno (aggiunta, rimozione e posizionamento).
 */
public interface ShapeManagerReceiver {

    /**
     * Aggiunge la {@link Shape} specificata alla collezione di disegno.
     *
     * @param shape la shape da aggiungere; non può essere {@code null}
     */
    void addShape(Shape shape);

    /**
     * Rimuove la {@link Shape} specificata dalla collezione di disegno.
     *
     * @param shape la shape da rimuovere; non può essere {@code null}
     */
    void removeShape(Shape shape);

     /**
     * Restituisce l'indice (zero-based) della {@link Shape} all’interno
     * della collezione di disegno.
     *
     * @param shape la shape di cui cercare l’indice; non può essere {@code null}
     * @return l’indice della shape, oppure -1 se non presente
     */
    int getShapeIndex(Shape shape);

     /**
     * Inserisce la {@link Shape} specificata nella collezione di disegno
     * alla posizione indicata.
     *
     * @param shape la shape da inserire; non può essere {@code null}
     * @param index la posizione (zero-based) in cui inserirla
     */
    void insertShapeAt(Shape shape, int index);
        
    void rotateShape(Shape shape,double degrees);
}
