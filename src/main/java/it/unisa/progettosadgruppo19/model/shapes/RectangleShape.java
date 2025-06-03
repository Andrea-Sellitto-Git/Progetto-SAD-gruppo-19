package it.unisa.progettosadgruppo19.model.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Implementazione di un rettangolo che si ridimensiona durante il drag.
 */
public class RectangleShape extends AbstractShape {

    private double startX, startY;
    private final Rectangle rectangleNode;

    /**
     * Costruisce un RectangleShape iniziale di dimensione nulla.
     *
     * @param startX coordinata X di partenza
     * @param startY coordinata Y di partenza
     * @param stroke colore del contorno
     */
    public RectangleShape(double startX, double startY, Color stroke) {
        super(new Rectangle(startX, startY, 0, 0));
        this.startX = startX;
        this.startY = startY;
        this.rectangleNode = (Rectangle) node;
        rectangleNode.setStroke(stroke);
    }

    public RectangleShape(double x, double y, double width, double height) {
        super(new Rectangle(x, y, width, height));
        this.startX = x;  // puoi anche impostarli a 0 se non ti servono nel clone
        this.startY = y;
        this.rectangleNode = (Rectangle) node;
        rectangleNode.setStroke(Color.BLACK);  // o un colore di default
    }

    @Override
    public void onDrag(double x, double y) {
        Rectangle r = (Rectangle) node;
        r.setX(Math.min(getX(), x));
        r.setY(Math.min(getY(), y));
        r.setWidth(Math.abs(x - getX()));
        r.setHeight(Math.abs(y - getY()));
    }

    @Override
    public void onRelease() {
    }

    @Override
    public double getX() {
        return rectangleNode.getX();
    }

    @Override
    public double getY() {
        return rectangleNode.getY();
    }

    @Override
    public void setX(double x) {
        this.startX = x;  // aggiorna anche startX
        rectangleNode.setX(x);
    }

    @Override
    public void setY(double y) {
        double deltaY = y - getY();
        startY = y;  // aggiorna il tuo campo interno
        if (rectangleNode != null) {
            rectangleNode.setY(rectangleNode.getY() + deltaY);  // sposta anche il nodo JavaFX
        }
    }

    @Override
    public double getWidth() {
        return rectangleNode.getWidth();
    }

    @Override
    public double getHeight() {
        return rectangleNode.getHeight();
    }

    public RectangleShape(Rectangle rectangle) {
        super(rectangle);
        this.rectangleNode = rectangle;
        this.startX = rectangle.getX();
        this.startY = rectangle.getY();
    }

    @Override
    public AbstractShape clone() {
        Rectangle original = this.rectangleNode;
        Rectangle newRect = new Rectangle(
                original.getX(), original.getY(),
                original.getWidth(), original.getHeight()
        );
        newRect.setStroke(original.getStroke());
        newRect.setFill(original.getFill());
        newRect.setStrokeWidth(original.getStrokeWidth());
        newRect.getStrokeDashArray().setAll(original.getStrokeDashArray());
        newRect.setRotate(original.getRotate());
        newRect.setScaleX(original.getScaleX());
        newRect.setScaleY(original.getScaleY());

        return new RectangleShape(newRect);
    }

    @Override
    public double getRotation() {
        return getNode().getRotate();
    }

    @Override
    public void setRotation(double degrees) {
        getNode().setRotate(degrees);
    }
}
