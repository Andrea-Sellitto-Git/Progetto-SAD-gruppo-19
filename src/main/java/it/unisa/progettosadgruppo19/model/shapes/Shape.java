package it.unisa.progettosadgruppo19.model.shapes;

/**
 * Interfaccia che definisce il comportamento di base di una forma geometrica.
 */
import javafx.scene.Node;

public interface Shape {

    /**
     * Restituisce il nodo JavaFX associato alla shape.
     *
     * @return il nodo JavaFX della shape
     */
    Node getNode();

    /**
     * Eseguito durante il trascinamento del mouse.
     *
     * @param x nuova coordinata X del puntatore
     * @param y nuova coordinata Y del puntatore
     */
    void onDrag(double x, double y);

    /**
     * Eseguito al rilascio del mouse dopo un drag.
     */
    void onRelease();

    /**
     * Verifica se il punto (x,y) si trova al di dentro dell'area occupata dalla
     * shape.
     *
     * @param x coordinata X del punto di test
     * @param y coordinata Y del punto di test
     * @return true se il punto è contenuto nella shape
     */
    boolean contains(double x, double y);

    double getX();

    double getY();

    void setX(double x);

    void setY(double x);

    double getWidth();

    double getHeight();

    double getRotation();

    void setRotation(double degrees);

    Shape clone();

}
