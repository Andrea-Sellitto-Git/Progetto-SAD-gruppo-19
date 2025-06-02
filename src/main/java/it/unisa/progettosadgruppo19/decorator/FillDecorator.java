package it.unisa.progettosadgruppo19.decorator;

import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import java.io.Serializable;
import javafx.scene.paint.Color;

/**
 * Decorator che imposta il colore di riempimento di una shape.
 */
public class FillDecorator extends ShapeDecorator implements Serializable{

    /**
     * Applica il colore di fill alla shape decorata.
     *
     * @param decorated shape originale
     * @param fill colore di riempimento
     */
    private final Color fill;
    
    public FillDecorator(Shape decorated, Color fill) {
        super(decorated);
        this.fill = fill;
        ((javafx.scene.shape.Shape) decorated.getNode()).setFill(fill);
    }
    
    @Override
    public Shape clone() {      
        return new FillDecorator(decorated.clone(), (Color) ((javafx.scene.shape.Shape) decorated.getNode()).getFill()  );
    }

}
