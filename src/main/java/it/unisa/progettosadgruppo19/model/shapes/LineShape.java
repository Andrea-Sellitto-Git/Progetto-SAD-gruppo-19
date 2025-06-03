package it.unisa.progettosadgruppo19.model.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * Implementazione di una linea che si ridimensiona durante il drag.
 */
public class LineShape extends AbstractShape{

    private double startX, startY;
    private final Line lineNode;

    /**
     * Costruisce una LineShape iniziale con estremi coincidenti.
     *
     * @param startX coordinata X di partenza
     * @param startY coordinata Y di partenza
     * @param stroke colore del contorno
     */
    public LineShape(double startX, double startY, Color stroke) {
        super(new Line(startX, startY, startX, startY));
        this.startX = startX;
        this.startY = startY;
        this.lineNode = (Line) node;
        lineNode.setStroke(stroke);
    }
    
    public LineShape(double startX, double startY, double endX, double endY, Color stroke) {
        super(new Line(startX, startY, endX, endY));
        this.startX = startX;
        this.startY = startY;
        this.lineNode = (Line) node;
        lineNode.setStroke(stroke);
    }
    
    /**
     * Costruttore da nodo (per clonazione).
     */
    public LineShape(Line line) {
        super(line);
        this.lineNode = line;
        this.startX = line.getStartX();
        this.startY = line.getStartY();
    }


    /**
     * Ridefinisce la fine della linea durante il drag.
     */
    @Override
    public void onDrag(double x, double y) {
        lineNode.setEndX(x);
        lineNode.setEndY(y);
    }

    /**
     * Nessuna logica aggiuntiva al rilascio.
     */
    @Override
    public void onRelease() {
    }

    @Override
    public double getX() {
        return Math.min(lineNode.getStartX(), lineNode.getEndX());
    }

    @Override
    public double getY() {
        return Math.min(lineNode.getStartY(), lineNode.getEndY());
    }
    @Override
    public void setX(double x) {
        double deltaX = x - getX();
        lineNode.setStartX(lineNode.getStartX() + deltaX);
        lineNode.setEndX(lineNode.getEndX() + deltaX);
        startX += deltaX;
    }

    @Override
    public void setY(double y) {
        double deltaY = y - getY();
        lineNode.setStartY(lineNode.getStartY() + deltaY);
        lineNode.setEndY(lineNode.getEndY() + deltaY);
        startY += deltaY;
    }
    @Override
    public double getWidth() {
        return Math.abs(lineNode.getEndX() - lineNode.getStartX());
    }

    @Override
    public double getHeight() {
        return Math.abs(lineNode.getEndY() - lineNode.getStartY());
    }
    
    @Override
    public AbstractShape clone() {
        try {
            Line original = this.lineNode;
            Line newLine = new Line(
                original.getStartX(), original.getStartY(),
                original.getEndX(), original.getEndY()
            );

            newLine.setStroke(original.getStroke());
            newLine.setFill(original.getFill());
            newLine.setStrokeWidth(original.getStrokeWidth());
            newLine.getStrokeDashArray().setAll(original.getStrokeDashArray());
            newLine.setRotate(original.getRotate());
            newLine.setScaleX(original.getScaleX());
            newLine.setScaleY(original.getScaleY());

            return new LineShape(newLine);
        } catch (Exception e) {
            System.err.println("[CLONE LINE ERROR] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
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
