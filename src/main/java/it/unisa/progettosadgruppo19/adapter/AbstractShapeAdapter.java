package it.unisa.progettosadgruppo19.adapter;

import it.unisa.progettosadgruppo19.model.serialization.ShapeData;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Classe astratta base per tutti gli adapter di shape. Implementa l'interfaccia
 * {@link ShapeDataAdapter} fornendo un'implementazione standard di
 * {@link #getShapeData()} basata sui dati esposti da {@link AbstractShape}.
 */
public abstract class AbstractShapeAdapter implements ShapeDataAdapter {

    /**
     * La shape originale da adattare in {@link ShapeData}.
     */
    protected final AbstractShape shape;

    /**
     * Crea un nuovo adapter per la shape specificata.
     *
     * @param shape la shape da adattare; non può essere {@code null}.
     */
    public AbstractShapeAdapter(AbstractShape shape) {
        this.shape = shape;
    }

    /**
     * Converte lo stato corrente della {@code shape} in un oggetto
     * {@link ShapeData} serializzabile. Vengono estratti:
     * <ul>
     * <li>nome della classe semplice della shape</li>
     * <li>coordinate X e Y</li>
     * <li>larghezza e altezza</li>
     * <li>colore del bordo (stroke) e riempimento (fill)</li>
     * </ul>
     * Se il paint non è un'istanza di {@link Color}, viene usato
     * respectivamente {@link Color#BLACK} e {@link Color#TRANSPARENT}.
     *
     * @return un nuovo {@link ShapeData} popolato con i valori estratti dalla
     * shape originale.
     */
    @Override
    public ShapeData getShapeData() {
        javafx.scene.shape.Shape fxNode = (javafx.scene.shape.Shape) shape.getNode();

        Paint stroke = fxNode.getStroke();
        Paint fill = fxNode.getFill();

        Color strokeColor = (stroke instanceof Color c) ? c : Color.BLACK;
        Color fillColor = (fill instanceof Color c) ? c : Color.TRANSPARENT;

        return new ShapeData(
                shape.getClass().getSimpleName(),
                shape.getX(),
                shape.getY(),
                shape.getWidth(),
                shape.getHeight(),
                shape.getRotation(),
                strokeColor,
                fillColor
        );
    }
}
