package it.unisa.progettosadgruppo19.util;

import javafx.collections.ObservableList;
import java.util.List;

/**
 * Classe di utilità per operazioni geometriche comuni.
 */
public final class GeometryUtils {
    
    // Costruttore privato per impedire istanziazione
    private GeometryUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Verifica se un punto è vicino a un segmento di linea.
     * 
     * @param px coordinata X del punto
     * @param py coordinata Y del punto
     * @param x1 coordinata X del primo estremo del segmento
     * @param y1 coordinata Y del primo estremo del segmento
     * @param x2 coordinata X del secondo estremo del segmento
     * @param y2 coordinata Y del secondo estremo del segmento
     * @param tolerance tolleranza in pixel
     * @return true se il punto è vicino al segmento
     */
    public static boolean isNearLine(double px, double py, double x1, double y1, double x2, double y2, double tolerance) {
        double dx = x2 - x1;
        double dy = y2 - y1;

        if (dx == 0 && dy == 0) {
            return Math.hypot(px - x1, py - y1) <= tolerance;
        }

        double t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));

        double projX = x1 + t * dx;
        double projY = y1 + t * dy;

        return Math.hypot(px - projX, py - projY) <= tolerance;
    }
    
    /**
     * Verifica se un punto è dentro un poligono usando l'algoritmo ray casting.
     * 
     * @param x coordinata X del punto
     * @param y coordinata Y del punto
     * @param points lista di coordinate del poligono [x0,y0,x1,y1,...]
     * @return true se il punto è dentro il poligono
     */
    public static boolean isPointInPolygon(double x, double y, List<Double> points) {
        if (points.size() < 4) return false; // Serve almeno una linea (2 punti)
        
        int intersections = 0;
        int vertexCount = points.size() / 2;
        
        for (int i = 0; i < vertexCount; i++) {
            int j = (i + 1) % vertexCount;
            
            double xi = points.get(i * 2);
            double yi = points.get(i * 2 + 1);
            double xj = points.get(j * 2);
            double yj = points.get(j * 2 + 1);
            
            // Ray casting: conta le intersezioni del raggio orizzontale verso destra
            if (((yi > y) != (yj > y)) && (x < (xj - xi) * (y - yi) / (yj - yi) + xi)) {
                intersections++;
            }
        }
        
        return (intersections % 2) == 1;
    }
    
    /**
     * Overload per ObservableList (JavaFX).
     */
    public static boolean isPointInPolygon(double x, double y, ObservableList<Double> points) {
        return isPointInPolygon(x, y, (List<Double>) points);
    }
    
    /**
     * Verifica se un punto è vicino ai bordi di un poligono.
     * 
     * @param x coordinata X del punto
     * @param y coordinata Y del punto
     * @param points lista di coordinate del poligono
     * @param tolerance tolleranza in pixel
     * @return true se il punto è vicino a un bordo
     */
    public static boolean isNearPolygonBorder(double x, double y, List<Double> points, double tolerance) {
        if (points.size() < 4) return false; // Serve almeno una linea
        
        int vertexCount = points.size() / 2;
        
        for (int i = 0; i < vertexCount; i++) {
            int j = (i + 1) % vertexCount;
            
            double x1 = points.get(i * 2);
            double y1 = points.get(i * 2 + 1);
            double x2 = points.get(j * 2);
            double y2 = points.get(j * 2 + 1);
            
            if (isNearLine(x, y, x1, y1, x2, y2, tolerance)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Overload per ObservableList (JavaFX).
     */
    public static boolean isNearPolygonBorder(double x, double y, ObservableList<Double> points, double tolerance) {
        return isNearPolygonBorder(x, y, (List<Double>) points, tolerance);
    }
    
    /**
     * Calcola l'area di un poligono usando la formula di Gauss.
     * 
     * @param points lista di coordinate del poligono
     * @return area del poligono
     */
    public static double calculatePolygonArea(List<Double> points) {
        if (points.size() < 4) return 0; // Serve almeno una linea
        
        double area = 0;
        int n = points.size() / 2;
        
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            double x1 = points.get(i * 2);
            double y1 = points.get(i * 2 + 1);
            double x2 = points.get(j * 2);
            double y2 = points.get(j * 2 + 1);
            area += x1 * y2 - x2 * y1;
        }
        
        return Math.abs(area) / 2.0;
    }
    
    /**
     * Calcola il perimetro di un poligono.
     * 
     * @param points lista di coordinate del poligono
     * @return perimetro del poligono
     */
    public static double calculatePolygonPerimeter(List<Double> points) {
        if (points.size() < 4) return 0;
        
        double perimeter = 0;
        int n = points.size() / 2;
        
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            double x1 = points.get(i * 2);
            double y1 = points.get(i * 2 + 1);
            double x2 = points.get(j * 2);
            double y2 = points.get(j * 2 + 1);
            perimeter += Math.hypot(x2 - x1, y2 - y1);
        }
        
        return perimeter;
    }
    
    /**
     * Verifica se un poligono è convesso.
     * 
     * @param points lista di coordinate del poligono
     * @return true se il poligono è convesso
     */
    public static boolean isPolygonConvex(List<Double> points) {
        if (points.size() < 6) return true; // Con meno di 3 punti è sempre "convesso"
        
        int n = points.size() / 2;
        boolean positive = false, negative = false;
        
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            int k = (i + 2) % n;
            
            double x1 = points.get(i * 2), y1 = points.get(i * 2 + 1);
            double x2 = points.get(j * 2), y2 = points.get(j * 2 + 1);
            double x3 = points.get(k * 2), y3 = points.get(k * 2 + 1);
            
            double cross = (x2 - x1) * (y3 - y2) - (y2 - y1) * (x3 - x2);
            
            if (cross > 0) positive = true;
            if (cross < 0) negative = true;
            if (positive && negative) return false;
        }
        
        return true;
    }
    
    /**
     * Calcola il bounding box di un insieme di punti.
     * 
     * @param points lista di coordinate
     * @return array con [minX, minY, maxX, maxY]
     */
    public static double[] calculateBoundingBox(List<Double> points) {
        if (points.isEmpty()) {
            return new double[]{0, 0, 0, 0};
        }
        
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        
        for (int i = 0; i < points.size(); i += 2) {
            double x = points.get(i);
            double y = points.get(i + 1);
            
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }
        
        return new double[]{minX, minY, maxX, maxY};
    }
    
    /**
     * Calcola il centro di un insieme di punti.
     * 
     * @param points lista di coordinate
     * @return array con [centerX, centerY]
     */
    public static double[] calculateCenter(List<Double> points) {
        double[] bbox = calculateBoundingBox(points);
        return new double[]{
            (bbox[0] + bbox[2]) / 2,  // centerX
            (bbox[1] + bbox[3]) / 2   // centerY
        };
    }
}
