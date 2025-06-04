package it.unisa.progettosadgruppo19.command.multi;

import it.unisa.progettosadgruppo19.command.UndoableCommand;
import it.unisa.progettosadgruppo19.command.receivers.ShapeManagerReceiver;
import it.unisa.progettosadgruppo19.command.receivers.ClipboardReceiver;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Comando undoable per incollare shape (singole o multiple) dal clipboard.
 * Supporta sia il clipboard standard che quello multiplo gestito da MultiClipboardManager.
 * 
 * VERSIONE MIGLIORATA che si integra con MultiCopyCommand.
 */
public class MultiPasteCommand implements UndoableCommand {

    private final ClipboardReceiver clipboard;
    private final ShapeManagerReceiver shapeManager;
    private final double x, y;
    private final List<Shape> pastedShapes;
    private final boolean useMultiClipboard;

    /**
     * Costruisce un comando MultiPaste che incolla alle coordinate specificate.
     *
     * @param clipboard il receiver del clipboard; non può essere {@code null}
     * @param shapeManager il receiver responsabile della gestione delle shape; non può essere {@code null}
     * @param x coordinata X in cui incollare
     * @param y coordinata Y in cui incollare
     * @param useMultiClipboard se true, tenta di incollare da clipboard multiplo
     */
    public MultiPasteCommand(ClipboardReceiver clipboard, ShapeManagerReceiver shapeManager, 
                            double x, double y, boolean useMultiClipboard) {
        this.clipboard = clipboard;
        this.shapeManager = shapeManager;
        this.x = x;
        this.y = y;
        this.pastedShapes = new ArrayList<>();
        this.useMultiClipboard = useMultiClipboard;
    }

    /**
     * Costruttore semplificato che usa automaticamente clipboard multiplo se disponibile.
     */
    public MultiPasteCommand(ClipboardReceiver clipboard, ShapeManagerReceiver shapeManager, double x, double y) {
        this(clipboard, shapeManager, x, y, true);
    }

    /**
     * Esegue l'incollaggio delle shape dal clipboard.
     */
    @Override
    public void execute() {
        pastedShapes.clear();
        
        // Prova prima il clipboard multiplo se richiesto
        if (useMultiClipboard && MultiCopyCommand.MultiClipboardManager.hasShapes()) {
            executeMultiplePaste();
        } else {
            // Fallback al clipboard standard
            executeSinglePaste();
        }
        
        if (pastedShapes.isEmpty()) {
            System.out.println("[MULTI-PASTE] Nessuna shape da incollare");
        } else {
            System.out.println("[MULTI-PASTE] Incollate " + pastedShapes.size() + " shape");
        }
    }

    /**
     * Incolla multiple shape dal clipboard multiplo.
     */
    private void executeMultiplePaste() {
        try {
            List<Shape> clipboardShapes = MultiCopyCommand.MultiClipboardManager.getClipboard();
            
            if (clipboardShapes.isEmpty()) {
                System.out.println("[MULTI-PASTE] Clipboard multiplo vuoto");
                return;
            }
            
            // Calcola il centro del gruppo di shape nel clipboard
            double[] centerOffset = calculateGroupCenter(clipboardShapes);
            double centerX = centerOffset[0];
            double centerY = centerOffset[1];
            
            // Calcola l'offset per posizionare il centro del gruppo al punto di click
            double deltaX = x - centerX;
            double deltaY = y - centerY;
            
            System.out.println("[MULTI-PASTE] Incollaggio gruppo di " + clipboardShapes.size() + 
                             " shape con offset (" + deltaX + ", " + deltaY + ")");
            
            for (Shape originalShape : clipboardShapes) {
                Shape independentCopy = createIndependentCopy(originalShape);
                
                if (independentCopy != null) {
                    // Applica l'offset
                    double newX = independentCopy.getX() + deltaX;
                    double newY = independentCopy.getY() + deltaY;
                    independentCopy.setX(newX);
                    independentCopy.setY(newY);
                    
                    // Aggiungi al manager e alla lista delle shape incollate
                    shapeManager.addShape(independentCopy);
                    independentCopy.getNode().setUserData(independentCopy);
                    pastedShapes.add(independentCopy);
                    
                    System.out.println("[MULTI-PASTE]   Incollata: " + 
                                     independentCopy.getClass().getSimpleName() + 
                                     " @ (" + newX + ", " + newY + ")");
                } else {
                    System.err.println("[MULTI-PASTE] Impossibile creare copia di: " + 
                                     originalShape.getClass().getSimpleName());
                }
            }
            
        } catch (Exception e) {
            System.err.println("[MULTI-PASTE] Errore nell'incollaggio multiplo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Incolla una singola shape dal clipboard standard.
     */
    private void executeSinglePaste() {
        Shape originalShape = clipboard.getClipboard();
        
        if (originalShape == null) {
            System.out.println("[MULTI-PASTE] Clipboard standard vuoto");
            return;
        }
        
        try {
            Shape independentCopy = createIndependentCopy(originalShape);
            
            if (independentCopy != null) {
                // Posiziona alle coordinate specificate
                independentCopy.setX(x);
                independentCopy.setY(y);
                
                // Aggiungi al manager
                shapeManager.addShape(independentCopy);
                independentCopy.getNode().setUserData(independentCopy);
                pastedShapes.add(independentCopy);
                
                System.out.println("[MULTI-PASTE] Shape incollata: " + 
                                 independentCopy.getClass().getSimpleName() + 
                                 " @ (" + x + ", " + y + ")");
            } else {
                System.err.println("[MULTI-PASTE] Impossibile creare una copia della shape");
            }
            
        } catch (Exception e) {
            System.err.println("[MULTI-PASTE] Errore nell'incollaggio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Calcola il centro di un gruppo di shape.
     */
    private double[] calculateGroupCenter(List<Shape> shapes) {
        if (shapes.isEmpty()) {
            return new double[]{0, 0};
        }
        
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        
        // Calcola il bounding box del gruppo
        for (Shape shape : shapes) {
            double shapeX = shape.getX();
            double shapeY = shape.getY();
            double shapeW = shape.getWidth();
            double shapeH = shape.getHeight();
            
            minX = Math.min(minX, shapeX);
            minY = Math.min(minY, shapeY);
            maxX = Math.max(maxX, shapeX + shapeW);
            maxY = Math.max(maxY, shapeY + shapeH);
        }
        
        // Restituisce il centro del bounding box
        return new double[]{(minX + maxX) / 2, (minY + maxY) / 2};
    }

    /**
     * Crea una copia completamente indipendente di una shape.
     */
    private Shape createIndependentCopy(Shape originalShape) {
        try {
            if (originalShape == null) {
                return null;
            }
            
            Shape clonedShape = originalShape.clone();
            
            if (clonedShape == null) {
                System.err.println("[MULTI-PASTE] Il metodo clone ha restituito null");
                return null;
            }
            
            // Verifica che la copia sia effettivamente indipendente
            if (clonedShape.getNode() == originalShape.getNode()) {
                System.err.println("[MULTI-PASTE] ATTENZIONE: La copia condivide il nodo con l'originale!");
            }
            
            return clonedShape;
            
        } catch (Exception e) {
            System.err.println("[MULTI-PASTE] Errore nella creazione della copia: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Annulla l'incollaggio rimuovendo tutte le shape precedentemente incollate.
     */
    @Override
    public void undo() {
        for (Shape shape : pastedShapes) {
            try {
                shapeManager.removeShape(shape);
                System.out.println("[UNDO MULTI-PASTE] Shape rimossa: " + shape.getClass().getSimpleName());
            } catch (Exception e) {
                System.err.println("[UNDO MULTI-PASTE] Errore nella rimozione: " + e.getMessage());
            }
        }
        
        if (!pastedShapes.isEmpty()) {
            System.out.println("[UNDO MULTI-PASTE] Rimosse " + pastedShapes.size() + " shape");
        }
    }

    /**
     * Restituisce il numero di shape incollate.
     */
    public int getPastedShapeCount() {
        return pastedShapes.size();
    }

    /**
     * Restituisce una copia della lista delle shape incollate.
     */
    public List<Shape> getPastedShapes() {
        return new ArrayList<>(pastedShapes);
    }

    /**
     * Verifica se questo comando ha incollato multiple shape.
     */
    public boolean isMultiplePaste() {
        return pastedShapes.size() > 1;
    }

    @Override
    public String toString() {
        return "MultiPasteCommand{" +
               "position=(" + x + ", " + y + "), " +
               "pastedCount=" + pastedShapes.size() +
               ", useMultiClipboard=" + useMultiClipboard +
               '}';
    }
}