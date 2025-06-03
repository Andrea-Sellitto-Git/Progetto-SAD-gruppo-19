package it.unisa.progettosadgruppo19.model.shapes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Implementazione di un rettangolo che si ridimensiona durante il drag.
 */
public class RectangleShape extends AbstractShape {

    private final double startX, startY;
    private final Rectangle r;

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
        this.r = (Rectangle) node;
        r.setStroke(stroke);
    }
    
    public RectangleShape(double x, double y, double width, double height) {
        super(new Rectangle(x, y, width, height));
        this.startX = x;  // puoi anche impostarli a 0 se non ti servono nel clone
        this.startY = y;
        this.r = (Rectangle) node;
        r.setStroke(Color.BLACK);  // o un colore di default
    }


    @Override
    public void onDrag(double x, double y) {
        Rectangle r = (Rectangle) node;
        r.setX(Math.min(startX, x));
        r.setY(Math.min(startY, y));
        r.setWidth(Math.abs(x - startX));
        r.setHeight(Math.abs(y - startY));
    }

    @Override
    public void onRelease() {
    }

    @Override
    public double getX() {
        return r.getX();
    }

    @Override
    public double getY() {
        return r.getY();
    }
    
    @Override
    public void   setX(double x){
        r.setX(x);
    }

    @Override
    public void   setY(double y){
        r.setY(y);
    }
    @Override
    public double getWidth() {
        return r.getWidth();
    }

    @Override
    public double getHeight() {
        return r.getHeight();
    }
    
    @Override
    public AbstractShape clone() {
    try {
        javafx.scene.shape.Rectangle originalRect = (javafx.scene.shape.Rectangle) this.node;
        
        // Crea un nuovo rettangolo con le stesse dimensioni ma SENZA posizione
        javafx.scene.shape.Rectangle newRect = new javafx.scene.shape.Rectangle(
            originalRect.getWidth(), 
            originalRect.getHeight()
        );
        
        // Copia tutte le proprietà visive
        newRect.setStroke(originalRect.getStroke());
        newRect.setFill(originalRect.getFill());
        newRect.setStrokeWidth(originalRect.getStrokeWidth());
        newRect.getStrokeDashArray().setAll(originalRect.getStrokeDashArray());
        newRect.setRotate(originalRect.getRotate());
        newRect.setScaleX(originalRect.getScaleX());
        newRect.setScaleY(originalRect.getScaleY());
        
        // Imposta la posizione iniziale uguale all'originale
        newRect.setX(originalRect.getX());
        newRect.setY(originalRect.getY());
        
        // Crea la nuova shape wrapper
        RectangleShape clone = new RectangleShape(
            originalRect.getX(), 
            originalRect.getY(), 
            originalRect.getWidth(), 
            originalRect.getHeight()
        );
        
        // Sostituisce il nodo con quello configurato
        // Hack: accedi al campo protetto attraverso reflection o modifica l'architettura
        try {
            java.lang.reflect.Field nodeField = AbstractShape.class.getDeclaredField("node");
            nodeField.setAccessible(true);
            nodeField.set(clone, newRect);
        } catch (Exception reflectionEx) {
            System.err.println("[CLONE RECT] Impossibile accedere al campo node: " + reflectionEx.getMessage());
        }
        
        System.out.println("[CLONE RECT] Creata copia indipendente @ (" + 
                          newRect.getX() + ", " + newRect.getY() + ")");
        
        return clone;
        
    } catch (Exception e) {
        System.err.println("[CLONE RECT ERROR] " + e.getMessage());
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
