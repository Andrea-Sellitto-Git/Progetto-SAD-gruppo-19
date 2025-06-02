package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import it.unisa.progettosadgruppo19.model.serialization.DrawingData;
import it.unisa.progettosadgruppo19.model.serialization.ShapeData;
import it.unisa.progettosadgruppo19.adapter.ShapeAdapter;
import it.unisa.progettosadgruppo19.decorator.FillDecorator;
import it.unisa.progettosadgruppo19.decorator.StrokeDecorator;
import it.unisa.progettosadgruppo19.factory.*;
import it.unisa.progettosadgruppo19.model.shapes.TextShape;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gestisce il salvataggio e il caricamento delle shape su/da file binari.
 */
public class ShapeFileManager {

    /**
     * Serializza la lista di shape e la salva sul file specificato.
     *
     * @param shapes lista di {@link AbstractShape} da salvare.
     * @param file file di destinazione; creato se inesistente.
     * @throws IOException in caso di errori di I/O.
     */
    public void saveToFile(List<AbstractShape> shapes, File file) throws IOException {
        List<ShapeData> dataList = shapes.stream()
                .map(shape -> new ShapeAdapter(shape).getShapeData())
                .collect(Collectors.toList());

        DrawingData drawingData = new DrawingData(dataList);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(drawingData);
        }
    }

    /**
     * Carica da file un {@link DrawingData} serializzato.
     *
     * @param file file sorgente contenente l'oggetto {@code DrawingData}.
     * @return l'istanza di {@link DrawingData} letta dal file.
     * @throws IOException in caso di errori di I/O.
     * @throws ClassNotFoundException se la classe serializzata non è trovata.
     */
    public DrawingData loadFromFile(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();
            if (obj instanceof DrawingData data) {
                return data;
            } else {
                throw new IOException("File does not contain valid DrawingData");
            }
        }
    }

    /**
     * Ricostruisce le {@link AbstractShape} da un {@link DrawingData}.
     *
     * @param drawingData dati serializzati del disegno.
     * @return lista di {@link AbstractShape} ricreate nel loro stato originale.
     */
    public List<AbstractShape> rebuildShapes(DrawingData drawingData) {
        List<AbstractShape> shapes = new ArrayList<>();

        for (ShapeData data : drawingData.getShapes()) {
            ShapeCreator creator = switch (data.getType()) {
                case "RectangleShape" ->
                    new RectangleShapeCreator();
                case "EllipseShape" ->
                    new EllipseShapeCreator();
                case "LineShape" ->
                    new LineShapeCreator();
                case "TextShape" ->
                    new TextShapeCreator();
                default ->
                    throw new IllegalArgumentException("Tipo non supportato: " + data.getType());
            };

            AbstractShape baseShape;

            if ("TextShape".equals(data.getType())) {
                // Per il testo, usa createShape con i parametri testo e fontSize
                baseShape = (AbstractShape) creator.createShape(
                        data.getText(),
                        data.getX(),
                        data.getY(),
                        data.getStroke(),
                        data.getFontSize()
                );
            } else {
                // Per le altre shape usa createShape classico
                baseShape = (AbstractShape) creator.createShape(data.getX(), data.getY(), data.getStroke());

                // Applica dimensioni (solo per non-text)
                baseShape.onDrag(data.getX() + data.getWidth(), data.getY() + data.getHeight());
                baseShape.onRelease();
            }

            // Imposta rotazione
            baseShape.setRotation(data.getRotation());

            // Applica decoratori
            Shape decorated = new StrokeDecorator(baseShape, data.getStroke());
            decorated = new FillDecorator(decorated, data.getFill());
            decorated.getNode().setUserData(decorated);

            shapes.add(baseShape);
        }

        return shapes;
    }

}
