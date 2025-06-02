package it.unisa.progettosadgruppo19.model.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

/**
 * Implementazione di un'ellisse che si ridimensiona durante il drag.
 */
public class EllipseShape extends AbstractShape implements Shape {

    private final double startX, startY;
    private final Ellipse e;
    
    private EllipseShape(double startX, double startY, Ellipse ellipse, Color stroke) {
        super(ellipse);
        this.startX = startX;
        this.startY = startY;
        this.e = (Ellipse) node;
        e.setStroke(stroke);
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
        this.e = (Ellipse) node;
        e.setStroke(stroke);
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
        this.e = (Ellipse) node;
        e.setStroke(stroke);
    }

    @Override
    public void onDrag(double x, double y) {
        e.setCenterX((startX + x) / 2);
        e.setCenterY((startY + y) / 2);
        e.setRadiusX(Math.abs(x - startX) / 2);
        e.setRadiusY(Math.abs(y - startY) / 2);
    }

    @Override
    public void onRelease() {
    }

    @Override
    public double getX() {
        return e.getCenterX() - e.getRadiusX();
    }

    @Override
    public double getY() {
        return e.getCenterY() - e.getRadiusY();
    }

    @Override
    public void setX(double x) {
        e.setCenterX(x + e.getRadiusX());
    }

    @Override
    public void setY(double y) {
        e.setCenterY(y + e.getRadiusY());
    }    

    @Override
    public double getWidth() {
        return e.getRadiusX() * 2;
    }

    @Override
    public double getHeight() {
        return e.getRadiusY() * 2;
    }

    @Override
    public AbstractShape clone() {
        Ellipse ell = (Ellipse) this.node;

        Ellipse newEllipse = new Ellipse(
            ell.getCenterX(),
            ell.getCenterY(),
            ell.getRadiusX(),
            ell.getRadiusY()
        );

        newEllipse.setStroke((Color) ell.getStroke());
        newEllipse.setFill(ell.getFill());
        newEllipse.setTranslateX(ell.getTranslateX());
        newEllipse.setTranslateY(ell.getTranslateY());
        newEllipse.setStrokeWidth(ell.getStrokeWidth());
        newEllipse.getStrokeDashArray().setAll(ell.getStrokeDashArray());

        return new EllipseShape(this.startX, this.startY, newEllipse, (Color) ell.getStroke());
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
