package it.unisa.progettosadgruppo19.command.multi;

import it.unisa.progettosadgruppo19.command.Command;
import it.unisa.progettosadgruppo19.command.receivers.ClipboardReceiver;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Comando per copiare multiple shape nel clipboard.
 * Supporta sia la copia di una singola shape che di un gruppo di shape.
 * 
 * VERSIONE COMPLETA con supporto per clipboard esteso.
 */
public class MultiCopyCommand implements Command {
    
    private final ClipboardReceiver clipboard;
    private final List<Shape> shapes;
    private final boolean isMultipleShapes;
    
    /**
     * Costruisce un comando MultiCopy per una lista di shape.
     *
     * @param clipboard il receiver del clipboard; non può essere {@code null}
     * @param shapes lista delle shape da copiare; non può essere {@code null}
     */
    public MultiCopyCommand(ClipboardReceiver clipboard, List<Shape> shapes) {
        this.clipboard = clipboard;
        this.shapes = new ArrayList<>(shapes);
        this.isMultipleShapes = shapes.size() > 1;
    }
    
    /**
     * Costruisce un comando MultiCopy per una singola shape.
     *
     * @param clipboard il receiver del clipboard; non può essere {@code null}
     * @param shape la shape da copiare; non può essere {@code null}
     */
    public MultiCopyCommand(ClipboardReceiver clipboard, Shape shape) {
        this.clipboard = clipboard;
        this.shapes = new ArrayList<>();
        this.shapes.add(shape);
        this.isMultipleShapes = false;
    }
    
    /**
     * Esegue la copia delle shape nel clipboard.
     * Se è presente una sola shape, la copia nel clipboard standard.
     * Se sono presenti multiple shape, gestisce la copia multipla.
     */
    @Override
    public void execute() {
        if (shapes.isEmpty()) {
            System.out.println("[MULTI-COPY] Nessuna shape da copiare");
            return;
        }
        
        if (shapes.size() == 1) {
            // Comportamento standard per singola shape
            copySingleShape(shapes.get(0));
        } else {
            // Gestione multiple shape
            copyMultipleShapes(shapes);
        }
        
        System.out.println("[MULTI-COPY] Copiate " + shapes.size() + " shape nel clipboard");
    }
    
    /**
     * Copia una singola shape nel clipboard usando il metodo standard.
     */
    private void copySingleShape(Shape shape) {
        try {
            Shape clonedShape = shape.clone();
            if (clonedShape != null) {
                clipboard.setClipboard(clonedShape);
                System.out.println("[MULTI-COPY] Shape singola copiata: " + shape.getClass().getSimpleName());
            } else {
                System.err.println("[MULTI-COPY] Impossibile clonare la shape: " + shape.getClass().getSimpleName());
            }
        } catch (Exception e) {
            System.err.println("[MULTI-COPY] Errore nella copia singola: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Gestisce la copia di multiple shape.
     * Per ora, copia la prima shape nel clipboard standard e mantiene 
     * un riferimento interno per le altre (per future implementazioni).
     */
    private void copyMultipleShapes(List<Shape> shapes) {
        try {
            // Lista per contenere tutte le copie
            List<Shape> clonedShapes = new ArrayList<>();
            
            for (Shape shape : shapes) {
                Shape clonedShape = shape.clone();
                if (clonedShape != null) {
                    clonedShapes.add(clonedShape);
                } else {
                    System.err.println("[MULTI-COPY] Impossibile clonare: " + shape.getClass().getSimpleName());
                }
            }
            
            if (!clonedShapes.isEmpty()) {
                // Per compatibilità con il clipboard esistente, 
                // impostiamo la prima shape come clipboard principale
                clipboard.setClipboard(clonedShapes.get(0));
                
                // Estendi il clipboard se supporta multiple shape
                if (clipboard instanceof ExtendedClipboardReceiver extendedClipboard) {
                    extendedClipboard.setMultipleClipboard(clonedShapes);
                } else {
                    // Fallback: usa un clipboard interno statico per ora
                    MultiClipboardManager.setClipboard(clonedShapes);
                }
                
                System.out.println("[MULTI-COPY] Multiple shape copiate: " + clonedShapes.size());
                
                // Log delle shape copiate
                for (int i = 0; i < clonedShapes.size(); i++) {
                    System.out.println("[MULTI-COPY]   " + (i + 1) + ". " + 
                                     clonedShapes.get(i).getClass().getSimpleName());
                }
            } else {
                System.err.println("[MULTI-COPY] Nessuna shape è stata copiata con successo");
            }
            
        } catch (Exception e) {
            System.err.println("[MULTI-COPY] Errore nella copia multipla: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Restituisce il numero di shape che verranno copiate.
     */
    public int getShapeCount() {
        return shapes.size();
    }
    
    /**
     * Verifica se questo comando copierà multiple shape.
     */
    public boolean isMultipleCopy() {
        return isMultipleShapes;
    }
    
    /**
     * Restituisce una copia della lista delle shape da copiare.
     */
    public List<Shape> getShapes() {
        return new ArrayList<>(shapes);
    }
    
    /**
     * Interfaccia estesa per clipboard che supporta multiple shape.
     * Può essere implementata in futuro per gestire nativamente multiple shape.
     */
    public interface ExtendedClipboardReceiver extends ClipboardReceiver {
        /**
         * Imposta multiple shape nel clipboard.
         */
        void setMultipleClipboard(List<Shape> shapes);
        
        /**
         * Restituisce le multiple shape dal clipboard.
         */
        List<Shape> getMultipleClipboard();
        
        /**
         * Verifica se il clipboard contiene multiple shape.
         */
        boolean hasMultipleShapes();
    }
    
    /**
     * Manager temporaneo per gestire clipboard multiplo.
     * Questa è una soluzione temporanea fino a quando il ClipboardReceiver
     * non viene esteso per supportare nativamente multiple shape.
     */
    public static class MultiClipboardManager {
        private static List<Shape> multiClipboard = new ArrayList<>();
        
        /**
         * Imposta le shape nel clipboard multiplo.
         */
        public static void setClipboard(List<Shape> shapes) {
            multiClipboard.clear();
            if (shapes != null) {
                multiClipboard.addAll(shapes);
            }
        }
        
        /**
         * Restituisce una copia delle shape nel clipboard multiplo.
         */
        public static List<Shape> getClipboard() {
            return new ArrayList<>(multiClipboard);
        }
        
        /**
         * Verifica se il clipboard multiplo contiene shape.
         */
        public static boolean hasShapes() {
            return !multiClipboard.isEmpty();
        }
        
        /**
         * Restituisce il numero di shape nel clipboard multiplo.
         */
        public static int getShapeCount() {
            return multiClipboard.size();
        }
        
        /**
         * Pulisce il clipboard multiplo.
         */
        public static void clear() {
            multiClipboard.clear();
        }
        
        /**
         * Restituisce informazioni di debug sul clipboard.
         */
        public static String getDebugInfo() {
            if (multiClipboard.isEmpty()) {
                return "Clipboard multiplo vuoto";
            }
            
            StringBuilder info = new StringBuilder();
            info.append("Clipboard multiplo contiene ").append(multiClipboard.size()).append(" shape:\n");
            
            for (int i = 0; i < multiClipboard.size(); i++) {
                Shape shape = multiClipboard.get(i);
                info.append("  ").append(i + 1).append(". ")
                    .append(shape.getClass().getSimpleName())
                    .append(" @ (").append(String.format("%.1f", shape.getX()))
                    .append(", ").append(String.format("%.1f", shape.getY())).append(")\n");
            }
            
            return info.toString().trim();
        }
    }
    
    @Override
    public String toString() {
        return "MultiCopyCommand{" +
               "shapeCount=" + shapes.size() +
               ", isMultiple=" + isMultipleShapes +
               '}';
    }
}