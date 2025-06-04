package it.unisa.progettosadgruppo19.model.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

/**
 * Implementazione di un'ellisse che si ridimensiona durante il drag.
 */
public class EllipseShape extends AbstractShape implements Shape {

    private double startX, startY;
    private final Ellipse ellipseNode;

    private EllipseShape(double startX, double startY, Ellipse ellipse, Color stroke) {
        super(ellipse);
        this.startX = startX;
        this.startY = startY;
        this.ellipseNode = (Ellipse) node;
        ellipseNode.setStroke(stroke);
    }

    /**
     * Costruisce un'EllipseShape iniziale di raggio zero.
     *
     * @param startX coordinata X del centro iniziale
     * @param startY coordinata Y del centro iniziale
     * @param stroke colore del contorno
     */
    public EllipseShape(double startX, double startY, Color stroke) {
        super(new Ellipse(startX, startY, 0, 0));
        this.startX = startX;
        this.startY = startY;
        this.ellipseNode = (Ellipse) node;
        ellipseNode.setStroke(stroke);
    }

    /**
     * Costruttore alternativo usato per il clone.
     *
     * @param centerX coordinata X centro
     * @param centerY coordinata Y centro
     * @param radiusX raggio orizzontale
     * @param radiusY raggio verticale
     * @param stroke colore del contorno
     */
    public EllipseShape(double centerX, double centerY, double radiusX, double radiusY, Color stroke) {
        super(new Ellipse(centerX, centerY, radiusX, radiusY));
        this.startX = centerX;
        this.startY = centerY;
        this.ellipseNode = (Ellipse) node;
        ellipseNode.setStroke(stroke);
    }

    /**
     * Costruttore per il clone.
     *
     * @param ellipse ellisse già configurata
     */
    public EllipseShape(Ellipse ellipse) {
        super(ellipse);
        this.ellipseNode = ellipse;
        this.startX = ellipse.getCenterX();
        this.startY = ellipse.getCenterY();
    }

    @Override
    public void onDrag(double x, double y) {
        ellipseNode.setCenterX((startX + x) / 2);
        ellipseNode.setCenterY((startY + y) / 2);
        ellipseNode.setRadiusX(Math.abs(x - startX) / 2);
        ellipseNode.setRadiusY(Math.abs(y - startY) / 2);
    }

    @Override
    public void onRelease() {
    }

    @Override
    public double getX() {
        return ellipseNode.getCenterX() - ellipseNode.getRadiusX();
    }

    @Override
    public double getY() {
        return ellipseNode.getCenterY() - ellipseNode.getRadiusY();
    }

    @Override
    public void setX(double x) {
        ellipseNode.setCenterX(x + ellipseNode.getRadiusX());
        this.startX = ellipseNode.getCenterX();
    }

    @Override
    public void setY(double y) {
        ellipseNode.setCenterY(y + ellipseNode.getRadiusY());
        this.startY = ellipseNode.getCenterY();
    }

    @Override
    public double getWidth() {
        return ellipseNode.getRadiusX() * 2;
    }

    @Override
    public double getHeight() {
        return ellipseNode.getRadiusY() * 2;
    }

    @Override
    public AbstractShape clone() {
        Ellipse original = this.ellipseNode;
        Ellipse newEllipse = new Ellipse(
                original.getCenterX(),
                original.getCenterY(),
                original.getRadiusX(),
                original.getRadiusY()
        );

        // Copia proprietà visive
        newEllipse.setStroke(original.getStroke());
        newEllipse.setFill(original.getFill());
        newEllipse.setStrokeWidth(original.getStrokeWidth());
        newEllipse.getStrokeDashArray().setAll(original.getStrokeDashArray());
        newEllipse.setRotate(original.getRotate());
        newEllipse.setScaleX(original.getScaleX());
        newEllipse.setScaleY(original.getScaleY());

        return new EllipseShape(newEllipse);
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
