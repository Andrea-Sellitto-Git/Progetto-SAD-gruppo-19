package it.unisa.progettosadgruppo19.command.commands;

import it.unisa.progettosadgruppo19.controller.MouseEventHandler;
import it.unisa.progettosadgruppo19.model.shapes.RectangleShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CopyTest {

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
    public void testCopyCommandCopiesShapeToClipboard() {
        RectangleShape shape = new RectangleShape(10, 10, Color.BLACK);
        MouseEventHandler handler = new MouseEventHandler(null, null);
        Copy copyCommand = new Copy(handler, shape);

        copyCommand.execute();

        Shape clipboardShape = handler.getClipboard();
        assertNotNull(clipboardShape);
        assertNotSame(shape, clipboardShape);
        assertEquals(shape.getClass(), clipboardShape.getClass());
        assertEquals(shape.getX(), clipboardShape.getX(), 0.001);
        assertEquals(shape.getY(), clipboardShape.getY(), 0.001);
    }
}
