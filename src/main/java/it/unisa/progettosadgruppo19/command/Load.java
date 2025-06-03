package it.unisa.progettosadgruppo19.command;

import it.unisa.progettosadgruppo19.controller.ShapeFileManager;
import it.unisa.progettosadgruppo19.model.serialization.DrawingData;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.serialization.ShapeData;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Comando per caricare un disegno da file binario (*.bin).
 * VERSIONE CORRETTA che funziona con il nuovo sistema di salvataggio senza duplicati.
 */
public class Load implements Command {

    private final Stage stage;
    private final List<AbstractShape> currentShapes;
    private final Pane drawingPane;
    private final ShapeFileManager fileManager;

    /**
     * Costruisce un comando Load.
     *
     * @param stage finestra JavaFX per il FileChooser; non può essere {@code null}
     * @param currentShapes lista di shape correnti da sostituire; non può essere {@code null}
     * @param drawingPane pane in cui inserire i nodi delle shape; non può essere {@code null}
     * @param fileManager gestore per il caricamento e ricostruzione delle shape; non può essere {@code null}
     */
    public Load(Stage stage, List<AbstractShape> currentShapes, Pane drawingPane, ShapeFileManager fileManager) {
        this.stage = stage;
        this.currentShapes = currentShapes;
        this.drawingPane = drawingPane;
        this.fileManager = fileManager;
    }

    /**
     * Esegue il comando aprendo il FileChooser, caricando il file selezionato,
     * pulendo la lista e il pane, ricostruendo le shape e aggiungendole
     * nuovamente all'interfaccia. VERSIONE CORRETTA senza duplicazioni.
     */
    @Override
    public void execute() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Carica disegno");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Drawing files (*.bin)", "*.bin"));

        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            try {
                System.out.println("\n=== LOAD START ===");
                
                // Carica i dati dal file
                DrawingData data = fileManager.loadFromFile(file);
                
                // Ricostruisci le shape (ora senza duplicati grazie al salvataggio corretto)
                List<AbstractShape> shapes = fileManager.rebuildShapes(data);
                
                // Pulisci il canvas corrente
                System.out.println("[LOAD] Pulizia canvas...");
                drawingPane.getChildren().clear();
                currentShapes.clear();
                
                // Aggiungi le shape caricate
                System.out.println("[LOAD] Aggiunta " + shapes.size() + " shape al canvas...");
                for (AbstractShape s : shapes) {
                    currentShapes.add(s);
                    drawingPane.getChildren().add(s.getNode());
                }

                System.out.println("[LOAD] Caricamento completato: " + shapes.size() + " shape caricate");
                
                // Log delle shape caricate
                for (ShapeData s : data.getShapes()) {
                    System.out.println("  " + s.getType() + " @ " + s.getX() + "," + s.getY());
                }
                
                System.out.println("=== LOAD END ===\n");

            } catch (IOException | ClassNotFoundException e) {
                System.err.println("[LOAD ERROR] Errore durante il caricamento: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}