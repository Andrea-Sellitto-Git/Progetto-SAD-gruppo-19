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

class DeleteTest {

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
    void testDeleteRemovesShapeFromPaneAndList() {
        List<AbstractShape> shapeList = new ArrayList<>();
        Pane pane = new Pane();
        ShapeManager manager = new ShapeManager(shapeList, pane);

        RectangleShape shape = new RectangleShape(10, 10, Color.BLACK);
        shapeList.add(shape);
        pane.getChildren().add(shape.getNode());

        Delete command = new Delete(manager, shape);
        command.execute();

        assertFalse(shapeList.contains(shape));
        assertFalse(pane.getChildren().contains(shape.getNode()));
    }
}
