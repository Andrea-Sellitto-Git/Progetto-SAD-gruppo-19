package it.unisa.progettosadgruppo19.command.commands;

import it.unisa.progettosadgruppo19.command.UndoableCommand;
import it.unisa.progettosadgruppo19.command.receivers.ShapeManagerReceiver;
import it.unisa.progettosadgruppo19.command.receivers.ClipboardReceiver;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

/**
 * Comando undoable per incollare una {@link Shape} salvata nel clipboard
 * in una posizione definita, creando una copia completamente indipendente.
 */
public class Paste implements UndoableCommand {

    private final ClipboardReceiver clipboard;
    private final ShapeManagerReceiver shapeManager;
    private Shape pastedShape;
    private final double x, y;    

    /**
    * Costruisce un comando Paste che incolla la shape alle coordinate specificate.
    *
    * @param clipboard il receiver del clipboard da cui recuperare la shape; non può essere {@code null}
    * @param shapeManager il receiver responsabile della gestione delle shape; non può essere {@code null}
    * @param x coordinata X in cui incollare la shape
    * @param y coordinata Y in cui incollare la shape
    */
    public Paste(ClipboardReceiver clipboard, ShapeManagerReceiver shapeManager, double x, double y) {
        this.clipboard = clipboard;
        this.shapeManager = shapeManager;
        this.x = x;
        this.y = y;
    }
    
    /**
     * Esegue l'incollaggio: preleva la shape dal clipboard, crea una copia completamente
     * indipendente, la posiziona alle coordinate specificate e la aggiunge al manager.
     */
    @Override
    public void execute() {
        Shape originalShape = clipboard.getClipboard();
        if (originalShape != null) {
            try {
                // Crea una copia completamente indipendente della shape
                pastedShape = createIndependentCopy(originalShape);
                
                if (pastedShape != null) {
                    // Calcola l'offset per posizionare la shape alle coordinate specificate
                    double currentX = pastedShape.getX();
                    double currentY = pastedShape.getY();
                    double deltaX = x - currentX;
                    double deltaY = y - currentY;
                    
                    // Applica l'offset
                    pastedShape.setX(x);
                    pastedShape.setY(y);
                    
                    // Aggiungi al manager
                    shapeManager.addShape(pastedShape);
                    
                    // Imposta UserData per la selezione
                    pastedShape.getNode().setUserData(pastedShape);
                    
                    System.out.println("[PASTE] Figura incollata: " + pastedShape.getClass().getSimpleName() 
                            + " @ (" + x + ", " + y + ")");
                    System.out.println("[PASTE] Offset applicato: (" + deltaX + ", " + deltaY + ")");
                } else {
                    System.err.println("[PASTE ERROR] Impossibile creare una copia della shape");
                }
                
            } catch (Exception e) {
                System.err.println("[PASTE ERROR] Errore durante l'incollaggio: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("[PASTE] Buffer vuoto.");
        }
    }

    /**
     * Crea una copia completamente indipendente della shape originale.
     * Questa copia non condivide alcun riferimento con l'originale.
     */
    private Shape createIndependentCopy(Shape originalShape) {
        try {
            // Ottieni la shape base (non decorata)
            AbstractShape baseShape = AbstractShape.unwrapToAbstract(originalShape);
            
            // Crea una copia profonda della shape base
            AbstractShape clonedBase = baseShape.clone();
            
            // Se la shape originale aveva dei decorator, li applica anche alla copia
            Shape decoratedCopy = applyDecorators(clonedBase, originalShape);
            
            return decoratedCopy;
            
        } catch (Exception e) {
            System.err.println("[PASTE] Errore nella creazione della copia: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Applica i decorator della shape originale alla copia.
     */
    private Shape applyDecorators(AbstractShape baseShape, Shape originalShape) {
        try {
            // Ottieni i colori dalla shape originale
            javafx.scene.shape.Shape originalNode = (javafx.scene.shape.Shape) originalShape.getNode();
            javafx.scene.paint.Paint originalStroke = originalNode.getStroke();
            javafx.scene.paint.Paint originalFill = originalNode.getFill();
            
            // Applica i decorator
            Shape decorated = baseShape;
            
            if (originalStroke instanceof javafx.scene.paint.Color strokeColor) {
                decorated = new it.unisa.progettosadgruppo19.decorator.StrokeDecorator(decorated, strokeColor);
            }
            
            if (originalFill instanceof javafx.scene.paint.Color fillColor) {
                decorated = new it.unisa.progettosadgruppo19.decorator.FillDecorator(decorated, fillColor);
            }
            
            // Copia altre proprietà
            decorated.setRotation(originalShape.getRotation());
            
            return decorated;
            
        } catch (Exception e) {
            System.err.println("[PASTE] Errore nell'applicazione dei decorator: " + e.getMessage());
            // Fallback: restituisce la shape base
            return baseShape;
        }
    }

    /**
     * Annulla l'incollaggio rimuovendo la shape precedentemente incollata, se presente.
     */
    @Override
    public void undo() {
        if (pastedShape != null) {
            shapeManager.removeShape(pastedShape);
            System.out.println("[UNDO PASTE] Shape rimossa: " + pastedShape.getClass().getSimpleName());
        }
    }
}