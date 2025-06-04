package it.unisa.progettosadgruppo19.command.commands;

import it.unisa.progettosadgruppo19.controller.MouseEventHandler;
import it.unisa.progettosadgruppo19.controller.ShapeManager;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.RectangleShape;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class CutTest {

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
                // Toolkit già avviato da un altro test: lo ignoriamo
                toolkitInitialized = true;
            }
        }
    }

    @Test
    public void testCutCommandRemovesShapeAndCopiesToClipboard() {

        List<AbstractShape> shapes = new ArrayList<>();
        Pane pane = new Pane();
        ShapeManager manager = new ShapeManager(shapes, pane);
        MouseEventHandler handler = new MouseEventHandler(pane, shapes);

        RectangleShape shape = new RectangleShape(10, 10, Color.BLACK);
        manager.addShape(shape);

        Cut cutCommand = new Cut(handler, manager, shape);

        cutCommand.execute();

        assertEquals(0, shapes.size());
        assertNotNull(handler.getClipboard());
        assertEquals(shape.getClass(), handler.getClipboard().getClass());
        assertNotSame(shape, handler.getClipboard());
    }
}
