package it.unisa.progettosadgruppo19.model.shapes;

import it.unisa.progettosadgruppo19.decorator.ShapeDecorator;
import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 * Classe astratta che implementa parzialmente l'interfaccia Shape, fornendo il
 * nodo JavaFX interno e la logica di contains.
 */
public abstract class AbstractShape implements Shape {

    /**
     * Nodo JavaFX deputato al rendering (Line, Rectangle, Ellipse).
     */
    protected final Node node;
    /**
     * Costruisce una AbstractShape avvolgendo il nodo specificato.
     *
     * @param node nodo JavaFX della forma
     */
    public AbstractShape(Node node) {
        this.node = node;
        if (node instanceof javafx.scene.shape.Shape s) {
            s.setFill(Color.TRANSPARENT);
            s.setStroke(Color.BLACK);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getNode() {
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(double x, double y) {
        return node.contains(x, y);
    }

    /**
     * Restituisce la coordinata X dell'estremo sinistro della shape.
     *
     * @return valore X minimo
     */
    public abstract double getX();

    /**
     * Restituisce la coordinata Y dell'estremo superiore della shape.
     *
     * @return valore Y minimo
     */
    public abstract double getY();

    /**
     * Restituisce la coordinata X dell'estremo sinistro della shape.
     *
     * @param  x imposta valore x minimo
     */
    public abstract void setX(double x);

    /**
     * Restituisce la coordinata Y dell'estremo superiore della shape.
     *
     * @param  y imposta valore y minimo
     */
    public abstract void setY(double y);

    
    /**
     * Restituisce la larghezza complessiva della shape.
     *
     * @return differenza orizzontale tra i bordi
     */
    public abstract double getWidth();

    /**
     * Restituisce l'altezza complessiva della shape.
     *
     * @return differenza verticale tra i bordi
     */
    public abstract double getHeight();
    
    
    @Override
    public abstract AbstractShape clone(); // Sarà implementato dalle sottoclassi


    
    public void moveBy(double dx, double dy) {
        node.setTranslateX(node.getTranslateX() + dx);
        node.setTranslateY(node.getTranslateY() + dy);
    }


    public static AbstractShape unwrapToAbstract(Shape shape) {
        while (shape instanceof ShapeDecorator) {
            shape = ((ShapeDecorator) shape).getWrapped();
        }
        if (shape instanceof AbstractShape) {
            return (AbstractShape) shape;
        } else {
            throw new IllegalStateException("Shape non è un AbstractShape dopo l'unwrapping");
        }
    }
}
