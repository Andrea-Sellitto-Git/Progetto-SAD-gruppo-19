package it.unisa.progettosadgruppo19.factory;

import it.unisa.progettosadgruppo19.model.shapes.FreeFormPolygonShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.paint.Color;

/**
 * ShapeCreator specifico per il poligono libero.
 */
public class FreeFormPolygonShapeCreator extends ShapeCreator {

    /**
     * Crea un FreeFormPolygonShape con primo vertice (startX,startY) e stroke
     * definito.
     */
    @Override
    public Shape createShape(double startX, double startY, Color stroke) {
        return new FreeFormPolygonShape(startX, startY, stroke);
    }
}
