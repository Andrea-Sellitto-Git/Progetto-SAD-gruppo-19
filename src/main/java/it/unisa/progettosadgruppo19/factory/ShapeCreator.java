package it.unisa.progettosadgruppo19.factory;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.paint.Color;

/**
 * Classe astratta che definisce il metodo factory.
 */
public abstract class ShapeCreator {

    /**
     * Crea una nuova shape a partire da coordinate e colore.
     *
     * @param startX coordinata X iniziale
     * @param startY coordinata Y iniziale
     * @param stroke colore del contorno
     * @return nuova istanza di Shape
     */
    public abstract Shape createShape(double startX, double startY, Color stroke);
    
    public Shape createShape(String text, double x, double y, Color stroke, double fontSize) {
        throw new UnsupportedOperationException("Questa figura non supporta il testo.");
    }
}
