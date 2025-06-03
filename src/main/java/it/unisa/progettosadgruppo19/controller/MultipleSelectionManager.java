package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Gestisce la selezione multipla delle shape.
 * Supporta l'aggiunta/rimozione di shape dalla selezione e l'applicazione
 * di effetti visivi per indicare lo stato di selezione.
 */
public class MultipleSelectionManager {
    
    private final Set<Shape> selectedShapes;
    private boolean multipleSelectionMode;
    private final DropShadow selectionEffect;
    
    public MultipleSelectionManager() {
        this.selectedShapes = new HashSet<>();
        this.multipleSelectionMode = false;
        
        // Configura l'effetto visivo per le shape selezionate
        this.selectionEffect = new DropShadow();
        this.selectionEffect.setRadius(8);
        this.selectionEffect.setColor(Color.BLUE);
        this.selectionEffect.setSpread(0.3);
    }
    
    /**
     * Attiva/disattiva la modalità di selezione multipla.
     */
    public void setMultipleSelectionMode(boolean enabled) {
        this.multipleSelectionMode = enabled;
        System.out.println("[MULTI-SELECT] Modalità selezione multipla: " + (enabled ? "ATTIVA" : "DISATTIVA"));
        
        // Se disattivo la modalità, deseleziono tutto
        if (!enabled) {
            clearSelection();
        }
    }
    
    /**
     * Verifica se la modalità di selezione multipla è attiva.
     */
    public boolean isMultipleSelectionMode() {
        return multipleSelectionMode;
    }
    
    /**
     * Aggiunge una shape alla selezione.
     */
    public void addToSelection(Shape shape) {
        if (shape == null) return;
        
        boolean added = selectedShapes.add(shape);
        if (added) {
            applySelectionEffect(shape);
            System.out.println("[MULTI-SELECT] Aggiunta alla selezione: " + shape.getClass().getSimpleName() + 
                             " (totale: " + selectedShapes.size() + ")");
        }
    }
    
    /**
     * Rimuove una shape dalla selezione.
     */
    public void removeFromSelection(Shape shape) {
        if (shape == null) return;
        
        boolean removed = selectedShapes.remove(shape);
        if (removed) {
            removeSelectionEffect(shape);
            System.out.println("[MULTI-SELECT] Rimossa dalla selezione: " + shape.getClass().getSimpleName() + 
                             " (totale: " + selectedShapes.size() + ")");
        }
    }
    
    /**
     * Alterna lo stato di selezione di una shape.
     */
    public void toggleSelection(Shape shape) {
        if (shape == null) return;
        
        if (isSelected(shape)) {
            removeFromSelection(shape);
        } else {
            addToSelection(shape);
        }
    }
    
    /**
     * Verifica se una shape è selezionata.
     */
    public boolean isSelected(Shape shape) {
        return selectedShapes.contains(shape);
    }
    
    /**
     * Restituisce una copia della lista delle shape selezionate.
     */
    public List<Shape> getSelectedShapes() {
        return new ArrayList<>(selectedShapes);
    }
    
    /**
     * Restituisce il numero di shape selezionate.
     */
    public int getSelectionCount() {
        return selectedShapes.size();
    }
    
    /**
     * Verifica se ci sono shape selezionate.
     */
    public boolean hasSelection() {
        return !selectedShapes.isEmpty();
    }
    
    /**
     * Pulisce la selezione rimuovendo tutte le shape.
     */
    public void clearSelection() {
        for (Shape shape : new ArrayList<>(selectedShapes)) {
            removeSelectionEffect(shape);
        }
        selectedShapes.clear();
        System.out.println("[MULTI-SELECT] Selezione pulita");
    }
    
    /**
     * Seleziona tutte le shape fornite.
     */
    public void selectAll(List<? extends Shape> shapes) {
        clearSelection();
        for (Shape shape : shapes) {
            addToSelection(shape);
        }
        System.out.println("[MULTI-SELECT] Selezionate tutte le shape: " + selectedShapes.size());
    }
    
    /**
     * Applica l'effetto visivo di selezione a una shape.
     */
    private void applySelectionEffect(Shape shape) {
        try {
            Node node = shape.getNode();
            if (node instanceof javafx.scene.shape.Shape fxShape) {
                // Crea un nuovo effetto per ogni shape per evitare condivisioni
                DropShadow effect = new DropShadow();
                effect.setRadius(selectionEffect.getRadius());
                effect.setColor(selectionEffect.getColor());
                effect.setSpread(selectionEffect.getSpread());
                
                fxShape.setEffect(effect);
            }
        } catch (Exception e) {
            System.err.println("[MULTI-SELECT] Errore nell'applicazione dell'effetto: " + e.getMessage());
        }
    }
    
    /**
     * Rimuove l'effetto visivo di selezione da una shape.
     */
    private void removeSelectionEffect(Shape shape) {
        try {
            Node node = shape.getNode();
            if (node instanceof javafx.scene.shape.Shape fxShape) {
                fxShape.setEffect(null);
            }
        } catch (Exception e) {
            System.err.println("[MULTI-SELECT] Errore nella rimozione dell'effetto: " + e.getMessage());
        }
    }
    
    /**
     * Restituisce informazioni di debug sulla selezione corrente.
     */
    public String getSelectionInfo() {
        if (selectedShapes.isEmpty()) {
            return "Nessuna shape selezionata";
        }
        
        StringBuilder info = new StringBuilder();
        info.append("Shape selezionate (").append(selectedShapes.size()).append("): ");
        
        for (Shape shape : selectedShapes) {
            info.append(shape.getClass().getSimpleName()).append(" ");
        }
        
        return info.toString().trim();
    }
    
    /**
     * Verifica se una specifica shape può essere aggiunta alla selezione.
     * Può essere esteso in futuro per aggiungere logiche di validazione.
     */
    public boolean canSelect(Shape shape) {
        return shape != null && multipleSelectionMode;
    }
    
    /**
     * Calcola il bounding box che racchiude tutte le shape selezionate.
     * Utile per operazioni di gruppo come movimento o ridimensionamento.
     */
    public double[] getSelectionBoundingBox() {
        if (selectedShapes.isEmpty()) {
            return new double[]{0, 0, 0, 0}; // minX, minY, maxX, maxY
        }
        
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        
        for (Shape shape : selectedShapes) {
            double x = shape.getX();
            double y = shape.getY();
            double width = shape.getWidth();
            double height = shape.getHeight();
            
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x + width);
            maxY = Math.max(maxY, y + height);
        }
        
        return new double[]{minX, minY, maxX, maxY};
    }
    
    /**
     * Calcola il centro della selezione.
     */
    public double[] getSelectionCenter() {
        double[] bbox = getSelectionBoundingBox();
        return new double[]{
            (bbox[0] + bbox[2]) / 2,  // centerX
            (bbox[1] + bbox[3]) / 2   // centerY
        };
    }
}