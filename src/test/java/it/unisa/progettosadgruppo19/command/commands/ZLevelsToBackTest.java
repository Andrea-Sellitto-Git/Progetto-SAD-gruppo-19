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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZLevelsToBackTest {

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
    public void testZLevelToBackMovesShapeToBottom() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // Setup iniziale
                List<AbstractShape> shapes = new ArrayList<>();
                Pane pane = new Pane();

                RectangleShape shape1 = new RectangleShape(10, 10, Color.RED);
                RectangleShape shape2 = new RectangleShape(20, 20, Color.BLUE);

                shapes.add(shape1);
                shapes.add(shape2);

                pane.getChildren().addAll(shape1.getNode(), shape2.getNode());

                ShapeManager manager = new ShapeManager(shapes, pane);

                // Verifica ordine iniziale: shape1 sotto shape2 (indice 0 e 1)
                assertEquals(shape1.getNode(), pane.getChildren().get(0));
                assertEquals(shape2.getNode(), pane.getChildren().get(1));

                // Comando ZLevelsToBack per spostare shape2 sotto tutto
                ZLevelsToBack sendToBack = new ZLevelsToBack(manager, shape2, 0);
                sendToBack.execute();

                // Dopo l'esecuzione shape2 deve essere il primo nodo (indice 0)
                assertEquals(shape2.getNode(), pane.getChildren().get(0));
                assertEquals(shape1.getNode(), pane.getChildren().get(1));

                latch.countDown();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        latch.await();
    }
}
