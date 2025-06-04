package it.unisa.progettosadgruppo19.factory;

import it.unisa.progettosadgruppo19.model.shapes.EllipseShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.paint.Color;

public class EllipseShapeCreator extends ShapeCreator {

    @Override
    public Shape createShape(double x, double y, Color stroke) {
        return new EllipseShape(x, y, stroke);
    }
}
