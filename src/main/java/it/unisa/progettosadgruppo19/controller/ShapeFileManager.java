package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import it.unisa.progettosadgruppo19.model.shapes.FreeFormPolygonShape;
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
 * Supporta tutte le forme geometriche inclusi i poligoni a forma libera.
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
        
        System.out.println("[SAVE] Salvate " + shapes.size() + " forme nel file: " + file.getName());
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
                System.out.println("[LOAD] Caricato file: " + file.getName() + " con " + data.getShapes().size() + " forme");
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
            try {
                AbstractShape baseShape = createShapeFromData(data);
                if (baseShape != null) {
                    // Applica decoratori
                    Shape decorated = new StrokeDecorator(baseShape, data.getStroke());
                    decorated = new FillDecorator(decorated, data.getFill());
                    decorated.getNode().setUserData(decorated);

                    shapes.add(baseShape);
                    System.out.println("[REBUILD] Ricostruita: " + data.getType() + " @ (" + data.getX() + ", " + data.getY() + ")");
                }
            } catch (Exception e) {
                System.err.println("[REBUILD ERROR] Errore nella ricostruzione di " + data.getType() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        return shapes;
    }

    /**
     * Crea una shape specifica dai dati serializzati.
     *
     * @param data dati della shape da ricostruire
     * @return shape ricostruita o null in caso di errore
     */
    private AbstractShape createShapeFromData(ShapeData data) {
        ShapeCreator creator = getCreatorForType(data.getType());
        if (creator == null) {
            System.err.println("[REBUILD] Tipo non supportato: " + data.getType());
            return null;
        }

        AbstractShape baseShape;

        switch (data.getType()) {
            case "TextShape" -> {
                // Per il testo, usa createShape con i parametri testo e fontSize
                baseShape = (AbstractShape) creator.createShape(
                        data.getText() != null ? data.getText() : "Testo",
                        data.getX(),
                        data.getY(),
                        data.getStroke(),
                        data.getFontSize()
                );
            }
            case "FreeFormPolygonShape" -> {
                // Per i poligoni, crea la forma base e poi imposta i punti
                baseShape = (AbstractShape) creator.createShape(data.getX(), data.getY(), data.getStroke());
                
                // Ricostruisci i vertici del poligono
                if (baseShape instanceof FreeFormPolygonShape polygonShape && data.getText() != null) {
                    polygonShape.setPointsFromString(data.getText());
                }
            }
            default -> {
                // Per le altre shape usa createShape classico
                baseShape = (AbstractShape) creator.createShape(data.getX(), data.getY(), data.getStroke());

                // Applica dimensioni (solo per forme che supportano onDrag)
                if (!(baseShape instanceof FreeFormPolygonShape)) {
                    baseShape.onDrag(data.getX() + data.getWidth(), data.getY() + data.getHeight());
                    baseShape.onRelease();
                }
            }
        }

        // Imposta rotazione
        baseShape.setRotation(data.getRotation());

        return baseShape;
    }

    /**
     * Restituisce il creator appropriato per il tipo di shape.
     *
     * @param type nome del tipo di shape
     * @return creator corrispondente o null se non supportato
     */
    private ShapeCreator getCreatorForType(String type) {
        return switch (type) {
            case "RectangleShape" -> new RectangleShapeCreator();
            case "EllipseShape" -> new EllipseShapeCreator();
            case "LineShape" -> new LineShapeCreator();
            case "TextShape" -> new TextShapeCreator();
            case "FreeFormPolygonShape" -> new FreeFormPolygonShapeCreator();
            default -> null;
        };
    }

    /**
     * Verifica se un file contiene dati validi.
     *
     * @param file file da verificare
     * @return true se il file contiene DrawingData validi
     */
    public boolean isValidDrawingFile(File file) {
        if (file == null || !file.exists() || !file.canRead()) {
            return false;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();
            return obj instanceof DrawingData;
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Restituisce informazioni di base su un file di disegno.
     *
     * @param file file da analizzare
     * @return stringa con informazioni o messaggio di errore
     */
    public String getFileInfo(File file) {
        try {
            DrawingData data = loadFromFile(file);
            List<ShapeData> shapes = data.getShapes();
            
            long rectangles = shapes.stream().filter(s -> "RectangleShape".equals(s.getType())).count();
            long ellipses = shapes.stream().filter(s -> "EllipseShape".equals(s.getType())).count();
            long lines = shapes.stream().filter(s -> "LineShape".equals(s.getType())).count();
            long texts = shapes.stream().filter(s -> "TextShape".equals(s.getType())).count();
            long polygons = shapes.stream().filter(s -> "FreeFormPolygonShape".equals(s.getType())).count();
            
            return String.format("File: %s\nTotale forme: %d\nRettangoli: %d, Ellissi: %d, Linee: %d, Testi: %d, Poligoni: %d",
                    file.getName(), shapes.size(), rectangles, ellipses, lines, texts, polygons);
        } catch (Exception e) {
            return "Errore nella lettura del file: " + e.getMessage();
        }
    }
}