package it.unisa.progettosadgruppo19.command.file;

import it.unisa.progettosadgruppo19.command.Command;
import it.unisa.progettosadgruppo19.controller.ShapeFileManager;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Comando per salvare su file binario (*.bin) il disegno corrente. Mostra un
 * {@link FileChooser} per selezionare il file di destinazione e delega la
 * scrittura al {@link ShapeFileManager}.
 */
public class Save implements Command {

    private final Stage stage;
    private final List<AbstractShape> shapes;
    private final ShapeFileManager fileManager;
    private File targetFile; // Aggiunto per i test

    /**
     * Costruisce il comando di salvataggio.
     *
     * @param stage finestra JavaFX per il {@link FileChooser}; non può essere
     * {@code null}
     * @param shapes lista di shape da serializzare; non può essere {@code null}
     * @param fileManager gestore per le operazioni di I/O; non può essere
     * {@code null}
     */
    public Save(Stage stage, List<AbstractShape> shapes, ShapeFileManager fileManager) {
        this.stage = stage;
        this.shapes = shapes;
        this.fileManager = fileManager;
    }

    public void setTargetFile(File file) {
        this.targetFile = file;
    }

    /**
     * Esegue il comando aprendo il dialog di salvataggio, selezionando il file
     * e scrivendo i dati tramite {@link ShapeFileManager}.
     */
    @Override
    public void execute() {
        File file = targetFile;
        if (file == null) {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Salva disegno");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Drawing files (*.bin)", "*.bin"));
            chooser.setInitialFileName("drawing.bin");

            file = chooser.showSaveDialog(stage);
        }

        if (file != null) {
            try {
                fileManager.saveToFile(shapes, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
