package it.unisa.progettosadgruppo19.command.file;

import it.unisa.progettosadgruppo19.controller.ShapeFileManager;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.RectangleShape;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class LoadTest {

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
    public void testLoadFromFile() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // salva una shape
                AbstractShape shape = new RectangleShape(50, 50, Color.BLACK);
                List<AbstractShape> toSave = List.of(shape);

                ShapeFileManager fileManager = new ShapeFileManager();
                File tempFile = File.createTempFile("test_load", ".bin");
                tempFile.deleteOnExit();
                fileManager.saveToFile(toSave, tempFile);

                // carica la shape
                List<AbstractShape> loaded = new ArrayList<>();
                Pane pane = new Pane();

                Load load = new Load(new Stage(), loaded, pane, fileManager);
                load.setSourceFile(tempFile); // test mode
                load.execute();

                assertEquals(1, loaded.size());
                latch.countDown();
            } catch (Exception e) {
                fail("Load test failed: " + e.getMessage());
            }
        });

        latch.await();
    }
}
