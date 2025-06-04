package it.unisa.progettosadgruppo19.model.serialization;

import java.io.Serializable;
import javafx.scene.paint.Color;

/**
 * DTO serializzabile che rappresenta una shape con tipo, posizione, dimensione
 * e colori di stroke/fill. Supporto esteso per poligoni e testo.
 */
public class ShapeData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;
    private double x, y, width, height;
    private double strokeR, strokeG, strokeB, strokeA;
    private double fillR, fillG, fillB, fillA;
    private double rotation;
    private String text; // Per TextShape e coordinate poligono
    private double fontSize;

    /**
     * Costruttore base per shape senza testo.
     */
    public ShapeData(String type, double x, double y, double width, double height, double rotation, Color stroke, Color fill) {
        this(type, x, y, width, height, rotation, stroke, fill, null, 0);
    }

    /**
     * Costruisce un DTO a partire dai parametri forniti.
     *
     * @param type tipo della shape
     * @param x coordinata X
     * @param y coordinata Y
     * @param width larghezza
     * @param height altezza
     * @param rotation rotazione in gradi
     * @param stroke colore del bordo
     * @param fill colore di riempimento
     * @param text testo (per TextShape) o coordinate (per poligoni)
     * @param fontSize dimensione font (per TextShape)
     */
    public ShapeData(String type, double x, double y, double width, double height, double rotation, Color stroke, Color fill, String text, double fontSize) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = rotation;

        // Gestione sicura dei colori
        if (stroke != null) {
            this.strokeR = stroke.getRed();
            this.strokeG = stroke.getGreen();
            this.strokeB = stroke.getBlue();
            this.strokeA = stroke.getOpacity();
        } else {
            // Colore nero di default
            this.strokeR = 0.0;
            this.strokeG = 0.0;
            this.strokeB = 0.0;
            this.strokeA = 1.0;
        }

        if (fill != null) {
            this.fillR = fill.getRed();
            this.fillG = fill.getGreen();
            this.fillB = fill.getBlue();
            this.fillA = fill.getOpacity();
        } else {
            // Trasparente di default
            this.fillR = 0.0;
            this.fillG = 0.0;
            this.fillB = 0.0;
            this.fillA = 0.0;
        }

        this.text = text;
        this.fontSize = fontSize;
    }

    /**
     * Restituisce il tipo della shape.
     */
    public String getType() {
        return type;
    }

    /**
     * Imposta il tipo della shape.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Restituisce la coordinata X.
     */
    public double getX() {
        return x;
    }

    /**
     * Imposta la coordinata X.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Restituisce la coordinata Y.
     */
    public double getY() {
        return y;
    }

    /**
     * Imposta la coordinata Y.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Restituisce la larghezza.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Imposta la larghezza.
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * Restituisce l'altezza.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Imposta l'altezza.
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Restituisce la rotazione in gradi.
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * Imposta la rotazione in gradi.
     */
    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    /**
     * Restituisce il colore di stroke ricostruito.
     */
    public Color getStroke() {
        return new Color(strokeR, strokeG, strokeB, strokeA);
    }

    /**
     * Imposta il colore di stroke.
     */
    public void setStroke(Color stroke) {
        if (stroke != null) {
            this.strokeR = stroke.getRed();
            this.strokeG = stroke.getGreen();
            this.strokeB = stroke.getBlue();
            this.strokeA = stroke.getOpacity();
        }
    }

    /**
     * Restituisce il colore di fill ricostruito.
     */
    public Color getFill() {
        return new Color(fillR, fillG, fillB, fillA);
    }

    /**
     * Imposta il colore di fill.
     */
    public void setFill(Color fill) {
        if (fill != null) {
            this.fillR = fill.getRed();
            this.fillG = fill.getGreen();
            this.fillB = fill.getBlue();
            this.fillA = fill.getOpacity();
        }
    }

    /**
     * Restituisce il testo (per TextShape) o le coordinate (per poligoni).
     */
    public String getText() {
        return text;
    }

    /**
     * Imposta il testo o le coordinate.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Restituisce la dimensione del font.
     */
    public double getFontSize() {
        return fontSize;
    }

    /**
     * Imposta la dimensione del font.
     */
    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * Verifica se questa shape è un poligono.
     */
    public boolean isPolygon() {
        return "FreeFormPolygonShape".equals(type);
    }

    /**
     * Verifica se questa shape è un testo.
     */
    public boolean isText() {
        return "TextShape".equals(type);
    }

    /**
     * Restituisce le coordinate del poligono come array di double. Valido solo
     * se isPolygon() restituisce true.
     */
    public double[] getPolygonCoordinates() {
        if (!isPolygon() || text == null || text.trim().isEmpty()) {
            return new double[0];
        }

        String[] coords = text.split(",");
        double[] result = new double[coords.length];

        try {
            for (int i = 0; i < coords.length; i++) {
                result[i] = Double.parseDouble(coords[i].trim());
            }
        } catch (NumberFormatException e) {
            System.err.println("Errore nel parsing delle coordinate del poligono: " + e.getMessage());
            return new double[0];
        }

        return result;
    }

    /**
     * Imposta le coordinate del poligono da un array di double.
     */
    public void setPolygonCoordinates(double[] coordinates) {
        if (coordinates == null || coordinates.length == 0) {
            this.text = "";
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coordinates.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(coordinates[i]);
        }
        this.text = sb.toString();
    }

    /**
     * Crea una copia profonda di questo ShapeData.
     */
    public ShapeData clone() {
        return new ShapeData(type, x, y, width, height, rotation, getStroke(), getFill(), text, fontSize);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(" at (").append(x).append(", ").append(y).append(")");
        sb.append(" size ").append(width).append("x").append(height);

        if (rotation != 0) {
            sb.append(" rotated ").append(rotation).append("°");
        }

        if (isText() && text != null) {
            sb.append(" text: '").append(text).append("' font: ").append(fontSize);
        } else if (isPolygon() && text != null) {
            int vertexCount = text.split(",").length / 2;
            sb.append(" vertices: ").append(vertexCount);
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ShapeData that = (ShapeData) obj;

        return Double.compare(that.x, x) == 0
                && Double.compare(that.y, y) == 0
                && Double.compare(that.width, width) == 0
                && Double.compare(that.height, height) == 0
                && Double.compare(that.rotation, rotation) == 0
                && Double.compare(that.fontSize, fontSize) == 0
                && type.equals(that.type)
                && java.util.Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(type, x, y, width, height, rotation, text, fontSize);
    }
}
