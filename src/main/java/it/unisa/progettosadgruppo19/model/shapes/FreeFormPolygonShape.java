package it.unisa.progettosadgruppo19.model.shapes;

import it.unisa.progettosadgruppo19.util.GeometryUtils;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione di un poligono a forma libera con supporto completo per: -
 * Creazione interattiva con click per aggiungere vertici - Movimento e
 * ridimensionamento migliorati - Calcoli geometrici (area, perimetro,
 * convessità) - Operazioni di trasformazione (traslazione, scalatura) -
 * Utilizza GeometryUtils per operazioni geometriche avanzate - Supporta
 * poligoni con qualsiasi numero di vertici (anche 2 per creare linee)
 *
 * VERSIONE CORRETTA con gestione migliorata del movimento e contains().
 */
public class FreeFormPolygonShape extends AbstractShape {

    private final Polygon polygon;           // nodo JavaFX
    private final List<Double> points;       // lista di coordinate [x0,y0, x1,y1, ...]
    private final double startX, startY;     // primo vertice

    /**
     * Costruisce un poligono con primo vertice (startX,startY). Il contorno
     * (stroke) è impostato via parametro, il fill resta trasparente.
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
     * Aggiunge un vertice (x,y) al poligono, rinfrescando i punti nel Polygon
     * JavaFX.
     */
    public void addPoint(double x, double y) {
        points.add(x);
        points.add(y);
        polygon.getPoints().setAll(points);
        System.out.println("[POLYGON] Punto aggiunto: (" + x + ", " + y + ") - Totale vertici: " + getVertexCount());
    }

    /**
     * Verifica se (x,y) è abbastanza vicino al primo vertice: serve per capire
     * quando l'utente "chiude" il poligono.
     */
    public boolean isNearStart(double x, double y, double tolleranza) {
        double dx = x - startX;
        double dy = y - startY;
        return Math.hypot(dx, dy) <= tolleranza;
    }

    /**
     * Verifica se il poligono può essere chiuso. Ora basta avere almeno 2 punti
     * (1 vertice + il punto iniziale).
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
     * Restituisce una copia modificabile della lista dei punti. Attenzione:
     * modifiche a questa lista devono essere seguite da updatePolygon().
     */
    public List<Double> getPoints() {
        return points; // Restituisce la lista originale per permettere modifiche dirette
    }

    /**
     * CORREZIONE: Imposta tutti i punti del poligono e aggiorna il nodo JavaFX.
     * Versione migliorata con validazione e logging.
     */
    public void setAllPoints(List<Double> newPoints) {
        if (newPoints == null) {
            System.err.println("[POLYGON] Tentativo di impostare punti null");
            return;
        }

        if (newPoints.size() < 4) {
            System.err.println("[POLYGON] Tentativo di impostare meno di 2 vertici (" + newPoints.size() / 2 + ")");
            return;
        }

        try {
            points.clear();
            points.addAll(newPoints);
            polygon.getPoints().setAll(points);

            System.out.println("[POLYGON] Punti aggiornati: " + getVertexCount() + " vertici");

        } catch (Exception e) {
            System.err.println("[POLYGON] Errore nell'aggiornamento dei punti: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Aggiorna il nodo JavaFX con i punti correnti. Utile dopo aver modificato
     * direttamente la lista dei punti.
     */
    public void updatePolygon() {
        try {
            polygon.getPoints().setAll(points);
            System.out.println("[POLYGON] Nodo JavaFX aggiornato");
        } catch (Exception e) {
            System.err.println("[POLYGON] Errore nell'aggiornamento del nodo: " + e.getMessage());
        }
    }

    /**
     * Restituisce i punti come stringa per la serializzazione.
     */
    public String getPointsAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < points.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
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
                try {
                    points.add(Double.parseDouble(coord.trim()));
                } catch (NumberFormatException e) {
                    System.err.println("[POLYGON] Errore nel parsing coordinate: " + coord);
                }
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
        if (points.isEmpty()) {
            return;
        }

        double[] center = getCenter();
        double centerX = center[0];
        double centerY = center[1];

        System.out.println("[POLYGON SCALE] Scalatura: " + scaleX + "x, " + scaleY + "x rispetto a (" + centerX + ", " + centerY + ")");

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
     * CORREZIONE: Trasla (sposta) tutti i punti del poligono con validazione
     * migliorata.
     *
     * @param deltaX spostamento orizzontale
     * @param deltaY spostamento verticale
     */
    public void translate(double deltaX, double deltaY) {
        if (points.isEmpty()) {
            System.out.println("[POLYGON TRANSLATE] Nessun punto da traslare");
            return;
        }

        System.out.println("[POLYGON TRANSLATE] Spostamento: dx=" + deltaX + ", dy=" + deltaY);

        try {
            for (int i = 0; i < points.size(); i += 2) {
                double oldX = points.get(i);
                double oldY = points.get(i + 1);

                points.set(i, oldX + deltaX);        // X
                points.set(i + 1, oldY + deltaY);     // Y
            }

            updatePolygon();

            System.out.println("[POLYGON TRANSLATE] Nuova posizione: (" + getX() + ", " + getY() + ")");

        } catch (Exception e) {
            System.err.println("[POLYGON TRANSLATE ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * CORREZIONE: Sposta il poligono a una nuova posizione assoluta.
     *
     * @param newX nuova coordinata X (angolo sinistro del bounding box)
     * @param newY nuova coordinata Y (angolo superiore del bounding box)
     */
    public void moveTo(double newX, double newY) {
        double currentX = getX();
        double currentY = getY();
        double deltaX = newX - currentX;
        double deltaY = newY - currentY;

        System.out.println("[POLYGON MOVE TO] Da (" + currentX + ", " + currentY + ") a (" + newX + ", " + newY + ")");

        translate(deltaX, deltaY);
    }

    /**
     * Verifica se un punto è vicino al bordo del poligono usando GeometryUtils.
     */
    public boolean isNearBorder(double x, double y, double tolerance) {
        return GeometryUtils.isNearPolygonBorder(x, y, points, tolerance);
    }

    public void closePolygon() {
        System.out.println("[POLYGON] Poligono chiuso con " + getVertexCount() + " vertici");
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
        if (points.isEmpty()) {
            return 0;
        }
        double min = Double.MAX_VALUE;
        for (int i = 0; i < points.size(); i += 2) {
            min = Math.min(min, points.get(i));
        }
        return min;
    }

    @Override
    public double getY() {
        // Y minima tra i punti
        if (points.isEmpty()) {
            return 0;
        }
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

    @Override
    public double getWidth() {
        // larghezza = maxX - minX
        if (points.isEmpty()) {
            return 0;
        }
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
        if (points.isEmpty()) {
            return 0;
        }
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
     * CORREZIONE: Verifica migliorata se il poligono contiene un punto. Combina
     * più algoritmi per massima affidabilità.
     */
    @Override
    public boolean contains(double x, double y) {
        if (points.isEmpty()) {
            return false;
        }

        // Test 1: Prova il test nativo di JavaFX
        boolean nativeResult = polygon.contains(x, y);
        if (nativeResult) {
            System.out.println("[POLYGON CONTAINS] Hit nativo JavaFX");
            return true;
        }

        // Test 2: Ray casting algorithm con GeometryUtils
        boolean raycastResult = GeometryUtils.isPointInPolygon(x, y, points);
        if (raycastResult) {
            System.out.println("[POLYGON CONTAINS] Hit via ray casting");
            return true;
        }

        // Test 3: Verifica se è vicino ai bordi (tolleranza aumentata)
        boolean nearBorderResult = GeometryUtils.isNearPolygonBorder(x, y, points, 10.0);
        if (nearBorderResult) {
            System.out.println("[POLYGON CONTAINS] Hit vicino ai bordi");
            return true;
        }

        // Test 4: Bounding box come fallback
        double[] bbox = getBoundingBox();
        boolean inBoundingBox = (x >= bbox[0] && x <= bbox[2] && y >= bbox[1] && y <= bbox[3]);
        if (inBoundingBox) {
            System.out.println("[POLYGON CONTAINS] Hit via bounding box");
            return true;
        }

        System.out.println("[POLYGON CONTAINS] Nessun hit rilevato");
        return false;
    }

    /**
     * Clone migliorato con gestione completa delle proprietà.
     */
    @Override
    public FreeFormPolygonShape clone() {
        try {
            // Crea una lista completamente nuova con nuovi oggetti Double
            List<Double> clonedPoints = new ArrayList<>();
            for (Double point : this.points) {
                clonedPoints.add(new Double(point.doubleValue()));
            }

            if (clonedPoints.size() < 2) {
                System.err.println("[CLONE POLYGON] Punti insufficienti per il clone");
                return null;
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
            System.out.println("[CLONE POLYGON] Creata copia indipendente con "
                    + copia.getVertexCount() + " vertici @ (" + copia.getX() + ", " + copia.getY() + ")");

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

    /**
     * Metodi di utilità aggiuntivi per debug e gestione.
     */
    /**
     * Restituisce informazioni dettagliate sul poligono per debug.
     */
    public String getDebugInfo() {
        double[] bbox = getBoundingBox();
        double[] center = getCenter();

        return String.format(
                "FreeFormPolygonShape[vertices=%d, bbox=(%.1f,%.1f)-(%.1f,%.1f), center=(%.1f,%.1f), area=%.2f, perimeter=%.2f, convex=%s]",
                getVertexCount(),
                bbox[0], bbox[1], bbox[2], bbox[3],
                center[0], center[1],
                calculateArea(),
                calculatePerimeter(),
                isConvex()
        );
    }

    /**
     * Verifica l'integrità del poligono.
     */
    public boolean isValid() {
        return points.size() >= 4
                && // Almeno 2 vertici
                points.size() % 2 == 0
                && // Numero pari di coordinate
                polygon.getPoints().size() == points.size(); // Sincronizzazione con JavaFX
    }

    /**
     * Forza la sincronizzazione tra la lista interna e il nodo JavaFX.
     */
    public void forceSynchronization() {
        try {
            polygon.getPoints().setAll(points);
            System.out.println("[POLYGON] Sincronizzazione forzata completata");
        } catch (Exception e) {
            System.err.println("[POLYGON] Errore nella sincronizzazione: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return getDebugInfo();
    }
}
