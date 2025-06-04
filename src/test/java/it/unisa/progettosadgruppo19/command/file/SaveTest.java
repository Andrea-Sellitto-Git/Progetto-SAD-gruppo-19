package it.unisa.progettosadgruppo19.command.file;

import it.unisa.progettosadgruppo19.controller.ShapeFileManager;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.RectangleShape;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class SaveTest {

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
    public void testSaveToFile() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                AbstractShape shape = new RectangleShape(50, 50, Color.BLACK);
                List<AbstractShape> shapes = List.of(shape);
                ShapeFileManager fileManager = new ShapeFileManager();

                File tempFile = File.createTempFile("test_save", ".bin");
                tempFile.deleteOnExit();

                Save save = new Save(new Stage(), shapes, fileManager);
                save.setTargetFile(tempFile); // test mode
                save.execute();

                assertTrue(tempFile.exists() && tempFile.length() > 0);
                latch.countDown();
            } catch (Exception e) {
                fail("Save test failed: " + e.getMessage());
            }
        });

        latch.await();
    }
}
