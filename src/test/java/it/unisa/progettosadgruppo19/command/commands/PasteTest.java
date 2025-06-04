package it.unisa.progettosadgruppo19.command.commands;

import it.unisa.progettosadgruppo19.controller.MouseEventHandler;
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

class PasteTest {

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
    void testPasteAddsNewShapeToPaneAndList() {
        List<AbstractShape> shapes = new ArrayList<>();
        Pane pane = new Pane();
        MouseEventHandler handler = new MouseEventHandler(pane, shapes);
        ShapeManager manager = new ShapeManager(shapes, pane);

        RectangleShape original = new RectangleShape(30, 30, Color.BLUE);
        handler.setClipboard(original);

        Paste paste = new Paste(handler, manager, 100, 150);
        paste.execute();

        assertEquals(1, shapes.size());
        AbstractShape pasted = shapes.get(0);
        assertEquals(100, pasted.getX(), 0.1);
        assertEquals(150, pasted.getY(), 0.1);
        assertTrue(pane.getChildren().contains(pasted.getNode()));
    }
}
