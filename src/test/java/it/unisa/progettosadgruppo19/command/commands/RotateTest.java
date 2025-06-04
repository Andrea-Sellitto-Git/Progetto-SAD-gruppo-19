package it.unisa.progettosadgruppo19.command.commands;

import it.unisa.progettosadgruppo19.controller.ShapeManager;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.RectangleShape;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class RotateTest {

    private static boolean toolkitInitialized = false;

    @BeforeAll
    static void initJFX() throws InterruptedException {
        if (!toolkitInitialized) {
            CountDownLatch latch = new CountDownLatch(1);
            try {
                Platform.startup(() -> {
                    toolkitInitialized = true;
                    latch.countDown();
                });
                latch.await();
            } catch (IllegalStateException e) {
                toolkitInitialized = true;
            }
        }
    }

    @Test
    void testRotateChangesRotationCorrectly() {
        List<AbstractShape> shapes = new ArrayList<>();
        Pane pane = new Pane();
        ShapeManager manager = new ShapeManager(shapes, pane);

        RectangleShape shape = new RectangleShape(10, 10, Color.BLACK);
        shape.setRotation(0);

        Rotate rotate = new Rotate(manager, shape, 45);
        rotate.execute();

        assertEquals(45, shape.getRotation(), 0.001);
    }
}
