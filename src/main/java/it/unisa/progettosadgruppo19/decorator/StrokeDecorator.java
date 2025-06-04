package it.unisa.progettosadgruppo19.decorator;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import java.io.Serializable;
import javafx.scene.paint.Color;

/**
 * Decorator che imposta il colore del bordo di una shape.
 */
public class StrokeDecorator extends ShapeDecorator implements Serializable {

    private Color stroke; 

    public StrokeDecorator(Shape decorated, Color stroke) {
        super(decorated);
        this.stroke = stroke != null ? stroke : Color.BLACK;
        applyStroke();
    }

    public void setStroke(Color newStroke) {
        this.stroke = newStroke != null ? newStroke : Color.BLACK;
        applyStroke();
    }

    public Color getStroke() {
        return stroke;
    }

    private void applyStroke() {
        try {
            javafx.scene.Node node = decorated.getNode();
            if (node instanceof javafx.scene.shape.Shape fxShape) {
                fxShape.setStroke(stroke);
            } else {
                System.err.println("[STROKE DECORATOR] Nodo non è una Shape JavaFX: " + node.getClass().getSimpleName());
            }
        } catch (Exception e) {
            System.err.println("[STROKE DECORATOR ERROR] " + e.getMessage());
            e.printStackTrace();
        }
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
            return clonedDecorator;
        } catch (Exception e) {
            System.err.println("[STROKE DECORATOR ERROR] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
