package it.unisa.progettosadgruppo19.adapter;

import it.unisa.progettosadgruppo19.model.serialization.DrawingData;
import it.unisa.progettosadgruppo19.model.serialization.ShapeData;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DrawingDataTest {

    @Test
    void testSerializationAndDeserialization() throws Exception {
        // Crea una ShapeData con stroke e fill
        ShapeData s = new ShapeData(
                "RectangleShape",
                5.0, 5.0, 15.0, 15.0, 10.0, 
                Color.BLACK,
                Color.YELLOW
        );

        DrawingData original = new DrawingData(List.of(s));

        // Serializza su byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(original);
        out.close();

        // Deserializza
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        DrawingData restored = (DrawingData) in.readObject();

        assertEquals(1, restored.getShapes().size());
        ShapeData restoredShape = restored.getShapes().get(0);

        assertEquals("RectangleShape", restoredShape.getType());
        assertEquals(5, restoredShape.getX());
        assertEquals(5, restoredShape.getY());
        assertEquals(15, restoredShape.getWidth());
        assertEquals(15, restoredShape.getHeight());
        assertEquals(Color.BLACK, restoredShape.getStroke());
        assertEquals(Color.YELLOW, restoredShape.getFill());
        assertEquals(10, restoredShape.getRotation());
    }
}
