package it.unisa.progettosadgruppo19.decorator;

import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import java.io.Serializable;
import javafx.scene.paint.Color;

/**
 * Decorator che imposta il colore del bordo di una shape.
 */
public class StrokeDecorator extends ShapeDecorator implements Serializable {

    private final Color stroke;

    /**
     * Applica il colore di stroke alla shape decorata.
     *
     * @param decorated shape originale
     * @param stroke colore del contorno
     */
    public StrokeDecorator(Shape decorated, Color stroke) {
        super(decorated);
        this.stroke = stroke;
        ((javafx.scene.shape.Shape) decorated.getNode()).setStroke(stroke);
    }
    
    @Override
    public Shape clone() {
        return new StrokeDecorator(decorated.clone(), (Color) ((javafx.scene.shape.Shape) decorated.getNode()).getStroke() );
    }
    
}
