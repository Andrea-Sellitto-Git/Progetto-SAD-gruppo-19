package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.model.shapes.FreeFormPolygonShape;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class per supportare l'editing avanzato dei poligoni. Fornisce metodi
 * per rilevare vertici, gestire handle di editing e validazioni.
 */
public class PolygonEditMode {

    private static final double VERTEX_HANDLE_RADIUS = 5.0;
    private static final double VERTEX_DETECTION_TOLERANCE = 8.0;

    /**
     * Verifica se un punto è vicino a un vertice del poligono.
     *
     * @param polygon poligono da controllare
     * @param x coordinata X del punto
     * @param y coordinata Y del punto
     * @return true se il punto è vicino a un vertice
     */
    public static boolean isNearVertex(FreeFormPolygonShape polygon, double x, double y) {
        return getVertexIndex(polygon, x, y) >= 0;
    }

    /**
     * Restituisce l'indice del vertice più vicino al punto specificato.
     *
     * @param polygon poligono da controllare
     * @param x coordinata X del punto
     * @param y coordinata Y del punto
     * @return indice del vertice (0-based) o -1 se nessun vertice è abbastanza
     * vicino
     */
    public static int getVertexIndex(FreeFormPolygonShape polygon, double x, double y) {
        List<Double> points = polygon.getPoints();
        for (int i = 0; i < points.size(); i += 2) {
            double vx = points.get(i);
            double vy = points.get(i + 1);
            if (Math.hypot(x - vx, y - vy) <= VERTEX_DETECTION_TOLERANCE) {
                return i / 2; // Restituisce l'indice del vertice
            }
        }
        return -1;
    }

    /**
     * Crea handle visuali per tutti i vertici del poligono.
     *
     * @param polygon poligono per cui creare gli handle
     * @param drawingPane pane su cui disegnare gli handle
     * @return lista degli handle creati
     */
    public static List<Circle> createVertexHandles(FreeFormPolygonShape polygon, Pane drawingPane) {
        List<Circle> handles = new ArrayList<>();
        List<Double> points = polygon.getPoints();

        for (int i = 0; i < points.size(); i += 2) {
            double x = points.get(i);
            double y = points.get(i + 1);

            Circle handle = new Circle(x, y, VERTEX_HANDLE_RADIUS);
            handle.setFill(Color.WHITE);
            handle.setStroke(Color.BLUE);
            handle.setStrokeWidth(2.0);
            handle.setMouseTransparent(false);

            // Memorizza l'indice del vertice nell'handle
            handle.setUserData(i / 2);

            handles.add(handle);
            drawingPane.getChildren().add(handle);
        }

        return handles;
    }

    /**
     * Rimuove tutti gli handle visuali dal pane.
     *
     * @param handles lista degli handle da rimuovere
     * @param drawingPane pane da cui rimuovere gli handle
     */
    public static void removeVertexHandles(List<Circle> handles, Pane drawingPane) {
        if (handles != null) {
            drawingPane.getChildren().removeAll(handles);
            handles.clear();
        }
    }

    /**
     * Aggiorna la posizione degli handle dopo una modifica del poligono.
     *
     * @param polygon poligono modificato
     * @param handles lista degli handle da aggiornare
     */
    public static void updateVertexHandles(FreeFormPolygonShape polygon, List<Circle> handles) {
        List<Double> points = polygon.getPoints();

        // Assicurati che il numero di handle corrisponda al numero di vertici
        if (handles.size() * 2 != points.size()) {
            System.err.println("Mismatch tra numero di handle e vertici del poligono");
            return;
        }

        for (int i = 0; i < handles.size(); i++) {
            Circle handle = handles.get(i);
            handle.setCenterX(points.get(i * 2));
            handle.setCenterY(points.get(i * 2 + 1));
        }
    }

    /**
     * Muove un vertice specifico del poligono.
     *
     * @param polygon poligono da modificare
     * @param vertexIndex indice del vertice da spostare
     * @param newX nuova coordinata X
     * @param newY nuova coordinata Y
     * @return true se il vertice è stato spostato con successo
     */
    public static boolean moveVertex(FreeFormPolygonShape polygon, int vertexIndex, double newX, double newY) {
        List<Double> points = polygon.getPoints();
        int pointIndex = vertexIndex * 2;

        if (pointIndex < 0 || pointIndex + 1 >= points.size()) {
            return false;
        }

        // Aggiorna le coordinate del vertice
        points.set(pointIndex, newX);
        points.set(pointIndex + 1, newY);

        // Riapplica i punti al poligono JavaFX
        ((javafx.scene.shape.Polygon) polygon.getNode()).getPoints().setAll(points);

        return true;
    }

    /**
     * Inserisce un nuovo vertice tra due vertici esistenti.
     *
     * @param polygon poligono da modificare
     * @param afterVertexIndex indice del vertice dopo il quale inserire il
     * nuovo vertice
     * @param x coordinata X del nuovo vertice
     * @param y coordinata Y del nuovo vertice
     * @return true se il vertice è stato inserito con successo
     */
    public static boolean insertVertex(FreeFormPolygonShape polygon, int afterVertexIndex, double x, double y) {
        List<Double> points = new ArrayList<>(polygon.getPoints());
        int insertIndex = (afterVertexIndex + 1) * 2;

        if (insertIndex < 0 || insertIndex > points.size()) {
            return false;
        }

        points.add(insertIndex, x);
        points.add(insertIndex + 1, y);

        // Riapplica i punti al poligono JavaFX
        ((javafx.scene.shape.Polygon) polygon.getNode()).getPoints().setAll(points);

        return true;
    }

    /**
     * Rimuove un vertice dal poligono.
     *
     * @param polygon poligono da modificare
     * @param vertexIndex indice del vertice da rimuovere
     * @return true se il vertice è stato rimosso con successo
     */
    public static boolean removeVertex(FreeFormPolygonShape polygon, int vertexIndex) {
        List<Double> points = new ArrayList<>(polygon.getPoints());

        // Non permettere di scendere sotto i 3 vertici
        if (points.size() <= 6) { // 3 vertici = 6 coordinate
            return false;
        }

        int pointIndex = vertexIndex * 2;
        if (pointIndex < 0 || pointIndex + 1 >= points.size()) {
            return false;
        }

        points.remove(pointIndex + 1); // Rimuovi Y
        points.remove(pointIndex);     // Rimuovi X

        // Riapplica i punti al poligono JavaFX
        ((javafx.scene.shape.Polygon) polygon.getNode()).getPoints().setAll(points);

        return true;
    }

    /**
     * Valida la geometria di un poligono.
     *
     * @param polygon poligono da validare
     * @return messaggio di validazione o stringa vuota se valido
     */
    public static String validatePolygon(FreeFormPolygonShape polygon) {
        List<Double> points = polygon.getPoints();

        if (points.size() < 6) {
            return "Il poligono deve avere almeno 3 vertici";
        }

        // Controlla per vertici duplicati
        for (int i = 0; i < points.size(); i += 2) {
            for (int j = i + 2; j < points.size(); j += 2) {
                if (Math.abs(points.get(i) - points.get(j)) < 0.1
                        && Math.abs(points.get(i + 1) - points.get(j + 1)) < 0.1) {
                    return "Vertici duplicati rilevati";
                }
            }
        }

        return ""; // Poligono valido
    }

    /**
     * Calcola il punto medio di un lato del poligono.
     *
     * @param polygon poligono
     * @param edgeIndex indice del lato (0-based)
     * @return array con [x, y] del punto medio, o null se l'indice non è valido
     */
    public static double[] getEdgeMidpoint(FreeFormPolygonShape polygon, int edgeIndex) {
        List<Double> points = polygon.getPoints();
        int vertexCount = points.size() / 2;

        if (edgeIndex < 0 || edgeIndex >= vertexCount) {
            return null;
        }

        int nextIndex = (edgeIndex + 1) % vertexCount;

        double x1 = points.get(edgeIndex * 2);
        double y1 = points.get(edgeIndex * 2 + 1);
        double x2 = points.get(nextIndex * 2);
        double y2 = points.get(nextIndex * 2 + 1);

        return new double[]{(x1 + x2) / 2, (y1 + y2) / 2};
    }
}
