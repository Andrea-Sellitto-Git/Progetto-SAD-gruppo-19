package it.unisa.progettosadgruppo19.adapter;

import it.unisa.progettosadgruppo19.model.serialization.ShapeData;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.TextShape;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.Serializable;
import javafx.scene.text.Text;

/**
 * Adapter che converte da AbstractShape a ShapeData per la persistenza.
 */
public class ShapeAdapter implements Serializable {

    private transient AbstractShape originalShape;
    private ShapeData shapeData;

    /**
     * Costruisce un adapter estraendo i dati dalla shape originale.
     *
     * @param shape shape da convertire
     */
    public ShapeAdapter(AbstractShape shape) {
        this.originalShape = shape;
        this.shapeData = convertToShapeData(shape);
    }

    /**
     * Estrae e popola un ShapeData dai parametri geometrici e di stile della
     * shape.
     *
     * @param shape shape da cui estrarre i dati
     * @return DTO corrispondente
     */
    private ShapeData convertToShapeData(AbstractShape shape) {
        String type = shape.getClass().getSimpleName();
        javafx.scene.shape.Shape fxNode = (javafx.scene.shape.Shape) shape.getNode();

        // Convert Paint → Color in modo sicuro
        Paint stroke = fxNode.getStroke();
        Paint fill = fxNode.getFill();

        Color strokeColor = (stroke instanceof Color c) ? c : Color.BLACK;
        Color fillColor = (fill instanceof Color c) ? c : Color.TRANSPARENT;

        // Aggiunta gestione per TextShape
        if (shape instanceof TextShape textShape) {
            Text textNode = (Text) textShape.getNode();
            return new ShapeData(
                    type,
                    shape.getX(),
                    shape.getY(),
                    shape.getWidth(),
                    shape.getHeight(),
                    shape.getRotation(),
                    strokeColor,
                    fillColor,
                    textShape.getText(),
                    textShape.getFontSize()
            );
        }

        return new ShapeData(
                type,
                shape.getX(),
                shape.getY(),
                shape.getWidth(),
                shape.getHeight(),
                shape.getRotation(),
                strokeColor,
                fillColor
        );
    }

    /**
     * Restituisce il DTO ShapeData generato.
     *
     * @return dati serializzabili della shape
     */
    public ShapeData getShapeData() {
        return shapeData;
    }
}
