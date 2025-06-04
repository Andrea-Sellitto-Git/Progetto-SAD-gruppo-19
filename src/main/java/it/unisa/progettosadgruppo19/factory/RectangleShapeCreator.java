package it.unisa.progettosadgruppo19.factory;

import it.unisa.progettosadgruppo19.model.shapes.RectangleShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.paint.Color;

public class RectangleShapeCreator extends ShapeCreator {

    @Override
    public Shape createShape(double x, double y, Color stroke) {
        return new RectangleShape(x, y, stroke);
    }
}
