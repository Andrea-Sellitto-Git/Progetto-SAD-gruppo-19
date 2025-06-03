package it.unisa.progettosadgruppo19.decorator;

import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import java.io.Serializable;
import javafx.scene.paint.Color;

/**
 * Decorator che imposta il colore del bordo di una shape.
 */
public class StrokeDecorator extends ShapeDecorator implements Serializable {

    private final Color stroke;

    /**
     * Applica il colore di stroke alla shape decorata.
     *
     * @param decorated shape originale
     * @param stroke colore del contorno
     */
    public StrokeDecorator(Shape decorated, Color stroke) {
        super(decorated);
        this.stroke = stroke;
        ((javafx.scene.shape.Shape) decorated.getNode()).setStroke(stroke);
    }
    
    @Override
    public Shape clone() {
    try {
        // Clona la shape decorata sottostante
        Shape clonedDecorated = decorated.clone();
        
        if (clonedDecorated == null) {
            System.err.println("[STROKE DECORATOR] Impossibile clonare la shape sottostante");
            return null;
        }
        
        // Ottieni il colore di stroke corrente dal nodo
        javafx.scene.paint.Paint currentStroke = null;
        try {
            javafx.scene.Node node = decorated.getNode();
            if (node instanceof javafx.scene.shape.Shape fxShape) {
                currentStroke = fxShape.getStroke();
            }
        } catch (Exception e) {
            System.err.println("[STROKE DECORATOR] Errore nel recupero del stroke: " + e.getMessage());
            currentStroke = stroke; // Fallback al colore originale
        }
        
        // Crea un nuovo decorator con il colore appropriato
        javafx.scene.paint.Color strokeColor = (currentStroke instanceof javafx.scene.paint.Color c) ? c : stroke;
        
        StrokeDecorator clonedDecorator = new StrokeDecorator(clonedDecorated, strokeColor);
        
        System.out.println("[STROKE DECORATOR] Clone creato con stroke: " + strokeColor);
        
        return clonedDecorator;
        
    } catch (Exception e) {
        System.err.println("[STROKE DECORATOR ERROR] " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}
    
}
