package it.unisa.progettosadgruppo19.model.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * Implementazione di una linea che si ridimensiona durante il drag.
 */
public class LineShape extends AbstractShape{

    private final double startX, startY;
    private final Line line;

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
        this.line = (Line) node;
        line.setStroke(stroke);
    }
    
    public LineShape(double startX, double startY, double endX, double endY, Color stroke) {
        super(new Line(startX, startY, endX, endY));
        this.startX = startX;
        this.startY = startY;
        this.line = (Line) node;
        line.setStroke(stroke);
    }


    /**
     * Ridefinisce la fine della linea durante il drag.
     */
    @Override
    public void onDrag(double x, double y) {
        Line l = (Line) node;
        l.setEndX(x);
        l.setEndY(y);
    }

    /**
     * Nessuna logica aggiuntiva al rilascio.
     */
    @Override
    public void onRelease() {
    }

    @Override
    public double getX() {
        return Math.min(line.getStartX(), line.getEndX());
    }

    @Override
    public double getY() {
        return Math.min(line.getStartY(), line.getEndY());
    }
    @Override
    public void setX(double x) {
        // Calculate current left position (min X)
        double currentX = getX();
        // Calculate horizontal shift needed
        double deltaX = x - currentX;
        // Apply shift to both start and end points
        line.setStartX(line.getStartX() + deltaX);
        line.setEndX(line.getEndX() + deltaX);
    }

    @Override
    public void setY(double y) {
        // Calculate current top position (min Y)
        double currentY = getY();
        // Calculate vertical shift needed
        double deltaY = y - currentY;
        // Apply shift to both start and end points
        line.setStartY(line.getStartY() + deltaY);
        line.setEndY(line.getEndY() + deltaY);
    }
    @Override
    public double getWidth() {
        return Math.abs(line.getEndX() - line.getStartX());
    }

    @Override
    public double getHeight() {
        return Math.abs(line.getEndY() - line.getStartY());
    }
    
    @Override
    public AbstractShape clone() {
    try {
        javafx.scene.shape.Line originalLine = (javafx.scene.shape.Line) this.node;
        
        // Crea una nuova linea con le stesse coordinate
        javafx.scene.shape.Line newLine = new javafx.scene.shape.Line(
            originalLine.getStartX(), 
            originalLine.getStartY(), 
            originalLine.getEndX(), 
            originalLine.getEndY()
        );
        
        // Copia tutte le proprietà visive
        newLine.setStroke(originalLine.getStroke());
        newLine.setFill(originalLine.getFill());
        newLine.setStrokeWidth(originalLine.getStrokeWidth());
        newLine.getStrokeDashArray().setAll(originalLine.getStrokeDashArray());
        newLine.setRotate(originalLine.getRotate());
        newLine.setScaleX(originalLine.getScaleX());
        newLine.setScaleY(originalLine.getScaleY());
        
        // Crea la nuova shape wrapper
        LineShape clone = new LineShape(
            originalLine.getStartX(), 
            originalLine.getStartY(), 
            originalLine.getEndX(), 
            originalLine.getEndY(), 
            (javafx.scene.paint.Color) originalLine.getStroke()
        );
        
        // Sostituisce il nodo
        try {
            java.lang.reflect.Field nodeField = AbstractShape.class.getDeclaredField("node");
            nodeField.setAccessible(true);
            nodeField.set(clone, newLine);
        } catch (Exception reflectionEx) {
            System.err.println("[CLONE LINE] Impossibile accedere al campo node: " + reflectionEx.getMessage());
        }
        
        System.out.println("[CLONE LINE] Creata copia indipendente (" + 
                          newLine.getStartX() + "," + newLine.getStartY() + 
                          ") -> (" + newLine.getEndX() + "," + newLine.getEndY() + ")");
        
        return clone;
        
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
