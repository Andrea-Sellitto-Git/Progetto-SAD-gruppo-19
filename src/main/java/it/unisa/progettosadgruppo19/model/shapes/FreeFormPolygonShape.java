package it.unisa.progettosadgruppo19.model.shapes;

import it.unisa.progettosadgruppo19.util.GeometryUtils;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione di un poligono a forma libera con supporto completo per:
 * - Creazione interattiva con click per aggiungere vertici
 * - Movimento e ridimensionamento
 * - Calcoli geometrici (area, perimetro, convessità)
 * - Operazioni di trasformazione (traslazione, scalatura)
 * - Utilizza GeometryUtils per operazioni geometriche avanzate
 * - Supporta poligoni con qualsiasi numero di vertici (anche 2 per creare linee)
 */
public class FreeFormPolygonShape extends AbstractShape {
    
    private final Polygon polygon;           // nodo JavaFX
    private final List<Double> points;       // lista di coordinate [x0,y0, x1,y1, ...]
    private final double startX, startY;     // primo vertice

    /**
     * Costruisce un poligono con primo vertice (startX,startY).
     * Il contorno (stroke) è impostato via parametro, il fill resta trasparente.
     */
    public FreeFormPolygonShape(double startX, double startY, Color stroke) {
        super(new Polygon());
        this.polygon = (Polygon) super.node;
        this.startX = startX;
        this.startY = startY;
        this.points = new ArrayList<>();
        this.polygon.setStroke(stroke);
        this.polygon.setFill(Color.TRANSPARENT);

        // Aggiungo subito il primo punto
        addPoint(startX, startY);
    }

    /**
     * Aggiunge un vertice (x,y) al poligono, rinfrescando i punti nel Polygon JavaFX.
     */
    public void addPoint(double x, double y) {
        points.add(x);
        points.add(y);
        polygon.getPoints().setAll(points);
    }

    /**
     * Verifica se (x,y) è abbastanza vicino al primo vertice: 
     * serve per capire quando l'utente "chiude" il poligono.
     */
    public boolean isNearStart(double x, double y, double tolleranza) {
        double dx = x - startX;
        double dy = y - startY;
        return Math.hypot(dx, dy) <= tolleranza;
    }

    /**
     * Verifica se il poligono può essere chiuso.
     * Ora basta avere almeno 2 punti (1 vertice + il punto iniziale).
     */
    public boolean canClose() {
        return points.size() >= 4; // Almeno 2 vertici (4 coordinate)
    }

    /**
     * Restituisce il numero di vertici del poligono.
     */
    public int getVertexCount() {
        return points.size() / 2;
    }

    /**
     * Restituisce una copia modificabile della lista dei punti.
     * Attenzione: modifiche a questa lista devono essere seguite da updatePolygon().
     */
    public List<Double> getPoints() {
        return points; // Restituisce la lista originale per permettere modifiche dirette
    }

    /**
     * Imposta tutti i punti del poligono e aggiorna il nodo JavaFX.
     */
    public void setAllPoints(List<Double> newPoints) {
        points.clear();
        points.addAll(newPoints);
        polygon.getPoints().setAll(points);
    }

    /**
     * Aggiorna il nodo JavaFX con i punti correnti.
     * Utile dopo aver modificato direttamente la lista dei punti.
     */
    public void updatePolygon() {
        polygon.getPoints().setAll(points);
    }

    /**
     * Restituisce i punti come stringa per la serializzazione.
     */
    public String getPointsAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < points.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(points.get(i));
        }
        return sb.toString();
    }

    /**
     * Imposta i punti da una stringa (per la deserializzazione).
     */
    public void setPointsFromString(String pointsStr) {
        points.clear();
        if (pointsStr != null && !pointsStr.trim().isEmpty()) {
            String[] coords = pointsStr.split(",");
            for (String coord : coords) {
                points.add(Double.parseDouble(coord.trim()));
            }
            polygon.getPoints().setAll(points);
        }
    }

    /**
     * Calcola il bounding box del poligono usando GeometryUtils.
     */
    public double[] getBoundingBox() {
        return GeometryUtils.calculateBoundingBox(points);
    }

    /**
     * Calcola il centro del poligono usando GeometryUtils.
     */
    public double[] getCenter() {
        return GeometryUtils.calculateCenter(points);
    }

    /**
     * Scala il poligono rispetto al suo centro.
     * 
     * @param scaleX fattore di scala orizzontale
     * @param scaleY fattore di scala verticale
     */
    public void scale(double scaleX, double scaleY) {
        if (points.isEmpty()) return;
        
        double[] center = getCenter();
        double centerX = center[0];
        double centerY = center[1];
        
        for (int i = 0; i < points.size(); i += 2) {
            double x = points.get(i);
            double y = points.get(i + 1);
            
            // Scala rispetto al centro
            double newX = centerX + (x - centerX) * scaleX;
            double newY = centerY + (y - centerY) * scaleY;
            
            points.set(i, newX);
            points.set(i + 1, newY);
        }
        
        updatePolygon();
    }

    /**
     * Scala il poligono uniformemente rispetto al suo centro.
     * 
     * @param scaleFactor fattore di scala uniforme
     */
    public void scale(double scaleFactor) {
        scale(scaleFactor, scaleFactor);
    }

    /**
     * Trasla (sposta) tutti i punti del poligono.
     * 
     * @param deltaX spostamento orizzontale
     * @param deltaY spostamento verticale
     */
    public void translate(double deltaX, double deltaY) {
        for (int i = 0; i < points.size(); i += 2) {
            points.set(i, points.get(i) + deltaX);        // X
            points.set(i + 1, points.get(i + 1) + deltaY); // Y
        }
        updatePolygon();
    }

    /**
     * Verifica se un punto è vicino al bordo del poligono usando GeometryUtils.
     */
    public boolean isNearBorder(double x, double y, double tolerance) {
        return GeometryUtils.isNearPolygonBorder(x, y, points, tolerance);
    }

    /**
     * Quando si chiude il poligono, volendo si può applicare un fill di default (opzionale).
     * Qui non faccio nulla di particolare, limito a lasciare il fill trasparente o gestito da decorator.
     */
    public void closePolygon() {
        // Non serve fare niente di esplicito, perché Polygon chiude già la forma.
        // Se si vuole un colore di fill di default, si può:
        // polygon.setFill(Color.LIGHTGRAY);
    }

    @Override
    public void onDrag(double x, double y) {
        // Non usato per questo shape (la creazione avviene solo via click)
    }

    @Override
    public void onRelease() {
        // Non usato per questo shape
    }

    @Override
    public double getX() {
        // X minima tra i punti
        if (points.isEmpty()) return 0;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < points.size(); i += 2) {
            min = Math.min(min, points.get(i));
        }
        return min;
    }

    @Override
    public double getY() {
        // Y minima tra i punti
        if (points.isEmpty()) return 0;
        double min = Double.MAX_VALUE;
        for (int i = 1; i < points.size(); i += 2) {
            min = Math.min(min, points.get(i));
        }
        return min;
    }

    @Override
    public void setX(double x) {
        double currentX = getX();
        double deltaX = x - currentX;
        translate(deltaX, 0);
    }

    @Override
    public void setY(double y) {
        double currentY = getY();
        double deltaY = y - currentY;
        translate(0, deltaY);
    }

    /**
     * Sposta il poligono a una nuova posizione.
     * 
     * @param x nuova coordinata X (angolo sinistro del bounding box)
     * @param y nuova coordinata Y (angolo superiore del bounding box)
     */
    public void moveTo(double x, double y) {
        double currentX = getX();
        double currentY = getY();
        translate(x - currentX, y - currentY);
    }

    @Override
    public double getWidth() {
        // larghezza = maxX - minX
        if (points.isEmpty()) return 0;
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (int i = 0; i < points.size(); i += 2) {
            double px = points.get(i);
            min = Math.min(min, px);
            max = Math.max(max, px);
        }
        return max - min;
    }

    @Override
    public double getHeight() {
        // altezza = maxY - minY
        if (points.isEmpty()) return 0;
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (int i = 1; i < points.size(); i += 2) {
            double py = points.get(i);
            min = Math.min(min, py);
            max = Math.max(max, py);
        }
        return max - min;
    }
    
    @Override
    public void setRotation(double angle) {
        // Applica la rotazione direttamente al nodo JavaFX
        polygon.setRotate(angle);
    }
    
    @Override
    public double getRotation() {
        return polygon.getRotate();
    }

    /**
     * Verifica se il poligono contiene un punto usando GeometryUtils.
     * Combina il test nativo JavaFX con l'algoritmo ray casting per maggiore affidabilità.
     */
    @Override
    public boolean contains(double x, double y) {
        // Prova prima il test nativo di JavaFX
        boolean nativeResult = polygon.contains(x, y);
        
        // Se il test nativo fallisce, usa l'algoritmo ray casting di GeometryUtils
        if (!nativeResult) {
            boolean customResult = GeometryUtils.isPointInPolygon(x, y, points);
            System.out.println("[POLYGON CONTAINS] Native: " + nativeResult + ", Custom: " + customResult);
            return customResult;
        }
        
        return nativeResult;
    }

    @Override
    public FreeFormPolygonShape clone() {
        try {
            // Crea una lista completamente nuova con nuovi oggetti Double
            List<Double> clonedPoints = new ArrayList<>();
            for (Double point : this.points) {
                clonedPoints.add(new Double(point.doubleValue()));
            }
            
            // Crea un nuovo poligono con il primo punto
            FreeFormPolygonShape copia = new FreeFormPolygonShape(
                clonedPoints.get(0), 
                clonedPoints.get(1), 
                (javafx.scene.paint.Color) polygon.getStroke()
            );
            
            // Rimuove il primo punto aggiunto automaticamente dal costruttore
            copia.points.clear();
            
            // Aggiunge tutti i punti clonati
            copia.points.addAll(clonedPoints);
            copia.polygon.getPoints().setAll(clonedPoints);
            
            // Copia le proprietà del nodo JavaFX
            copia.polygon.setFill(polygon.getFill());
            copia.polygon.setStroke(polygon.getStroke());
            copia.polygon.setStrokeWidth(polygon.getStrokeWidth());
            copia.polygon.getStrokeDashArray().setAll(polygon.getStrokeDashArray());
            
            // Copia le trasformazioni (NON i translate che sono posizioni assolute)
            copia.polygon.setRotate(polygon.getRotate());
            copia.polygon.setScaleX(polygon.getScaleX());
            copia.polygon.setScaleY(polygon.getScaleY());
            
            // NON copiare translateX e translateY per evitare sovrapposizioni
            
            System.out.println("[CLONE POLYGON] Creata copia indipendente con " + 
                              copia.getVertexCount() + " vertici");
            
            return copia;
            
        } catch (Exception e) {
            System.err.println("[CLONE POLYGON ERROR] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Calcola l'area del poligono usando GeometryUtils.
     */
    public double calculateArea() {
        return GeometryUtils.calculatePolygonArea(points);
    }

    /**
     * Calcola il perimetro del poligono usando GeometryUtils.
     */
    public double calculatePerimeter() {
        return GeometryUtils.calculatePolygonPerimeter(points);
    }

    /**
     * Verifica se il poligono è convesso usando GeometryUtils.
     */
    public boolean isConvex() {
        return GeometryUtils.isPolygonConvex(points);
    }

    @Override
    public String toString() {
        return String.format("FreeFormPolygonShape[vertices=%d, area=%.2f, perimeter=%.2f, convex=%s]",
                getVertexCount(), calculateArea(), calculatePerimeter(), isConvex());
    }
}