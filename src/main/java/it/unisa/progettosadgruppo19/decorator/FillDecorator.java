package it.unisa.progettosadgruppo19.decorator;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.Serializable;

/**
 * Decorator che imposta il colore di riempimento di una shape in modo sicuro.
 * Gestisce correttamente tutti i tipi di shape inclusi i poligoni.
 */
public class FillDecorator extends ShapeDecorator implements Serializable {

    private final Color fill;

    /**
     * Applica il colore di fill alla shape decorata in modo sicuro.
     *
     * @param decorated shape originale
     * @param fill colore di riempimento
     */
    public FillDecorator(Shape decorated, Color fill) {
        super(decorated);
        this.fill = fill != null ? fill : Color.TRANSPARENT;
        
        // Applica il fill in modo sicuro
        applyFillSafely();
    }
    
    /**
     * Applica il fill in modo sicuro controllando il tipo di nodo.
     */
    private void applyFillSafely() {
        try {
            javafx.scene.Node node = decorated.getNode();
            
            // Verifica che sia effettivamente una Shape JavaFX
            if (node instanceof javafx.scene.shape.Shape fxShape) {
                fxShape.setFill(fill);
                System.out.println("[FILL] Applicato fill " + fill + " a " + 
                    fxShape.getClass().getSimpleName());
            } else {
                System.err.println("[FILL] Nodo non è una Shape JavaFX: " + 
                    node.getClass().getSimpleName());
            }
        } catch (Exception e) {
            System.err.println("[FILL ERROR] Errore nell'applicazione del fill: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Restituisce il colore di fill applicato.
     */
    public Color getFillColor() {
        return fill;
    }
    
    @Override
    public Shape clone() {
    try {
        // Clona la shape decorata sottostante
        Shape clonedDecorated = decorated.clone();
        
        if (clonedDecorated == null) {
            System.err.println("[FILL DECORATOR] Impossibile clonare la shape sottostante");
            return null;
        }
        
        // Ottieni il colore di fill corrente dal nodo
        javafx.scene.paint.Paint currentFill = null;
        try {
            javafx.scene.Node node = decorated.getNode();
            if (node instanceof javafx.scene.shape.Shape fxShape) {
                currentFill = fxShape.getFill();
            }
        } catch (Exception e) {
            System.err.println("[FILL DECORATOR] Errore nel recupero del fill: " + e.getMessage());
            currentFill = fill; // Fallback al colore originale
        }
        
        // Crea un nuovo decorator con il colore appropriato
        javafx.scene.paint.Color fillColor = (currentFill instanceof javafx.scene.paint.Color c) ? c : fill;
        
        FillDecorator clonedDecorator = new FillDecorator(clonedDecorated, fillColor);
        
        System.out.println("[FILL DECORATOR] Clone creato con fill: " + fillColor);
        
        return clonedDecorator;
        
    } catch (Exception e) {
        System.err.println("[FILL DECORATOR ERROR] " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}
    
    /**
     * Verifica se il fill è stato applicato correttamente.
     */
    public boolean isFillApplied() {
        try {
            javafx.scene.Node node = decorated.getNode();
            if (node instanceof javafx.scene.shape.Shape fxShape) {
                Paint appliedFill = fxShape.getFill();
                return appliedFill != null && appliedFill.equals(fill);
            }
        } catch (Exception e) {
            System.err.println("[FILL CHECK ERROR] " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Riapplica il fill se necessario.
     */
    public void refreshFill() {
        applyFillSafely();
    }
}