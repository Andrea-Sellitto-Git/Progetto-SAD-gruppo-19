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
        
        try {
            Shape clonedDecorated = decorated.clone();

            if (clonedDecorated == null) {
                System.err.println("[STROKE DECORATOR] Impossibile clonare la shape sottostante");
                return null;
            }

            StrokeDecorator clonedDecorator = new StrokeDecorator(clonedDecorated, this.stroke);

            System.out.println("[STROKE DECORATOR] Clone creato con stroke: " + this.stroke);
            return clonedDecorator;

        } catch (Exception e) {
            System.err.println("[STROKE DECORATOR ERROR] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
