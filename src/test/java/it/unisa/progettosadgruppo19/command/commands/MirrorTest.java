package it.unisa.progettosadgruppo19.command.commands;

import it.unisa.progettosadgruppo19.controller.ShapeManager;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.RectangleShape;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class MirrorTest {

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
    public void testMirrorAppliesHorizontalFlip() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                Pane pane = new Pane();
                List<AbstractShape> shapes = new ArrayList<>();
                ShapeManager manager = new ShapeManager(shapes, pane);

                AbstractShape shape = new RectangleShape(100, 100, Color.RED);
                shapes.add(shape);
                pane.getChildren().add(shape.getNode());

                Node node = shape.getNode();
                assertEquals(1.0, node.getScaleX(), 0.001); // prima dello specchio

                Mirror mirror = new Mirror(manager, shape, true); // specchio orizzontale
                mirror.execute();

                assertEquals(-1.0, node.getScaleX(), 0.001); // specchiato orizzontalmente

                mirror.undo();
                assertEquals(1.0, node.getScaleX(), 0.001); // tornato allo stato iniziale

                latch.countDown();
            } catch (Exception e) {
                fail("Mirror test failed: " + e.getMessage());
            }
        });

        latch.await();
    }
}
