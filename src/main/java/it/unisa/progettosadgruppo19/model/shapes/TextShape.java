/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.model.shapes;

import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class TextShape extends AbstractShape {

    private final Text textNode;

    public TextShape(String text, double x, double y) {
        super(new Text(x, y, text));
        this.textNode = (Text) super.node;
        this.textNode.setFill(Color.BLACK);
    }

    public void setColor(Color color) {
        textNode.setFill(color);
    }

    @Override
    public double getX() {
        return textNode.getX();
    }

    @Override
    public double getY() {
        return textNode.getY();
    }

    @Override
    public void setX(double x) {
        textNode.setX(x);
    }

    @Override
    public void setY(double y) {
        textNode.setY(y);
    }

    @Override
    public double getWidth() {
        return textNode.getLayoutBounds().getWidth();
    }

    @Override
    public double getHeight() {
        return textNode.getLayoutBounds().getHeight();
    }

    public AbstractShape clone() {
    try {
        javafx.scene.text.Text originalText = (javafx.scene.text.Text) this.node;
        
        // Crea un nuovo nodo Text completamente indipendente
        javafx.scene.text.Text newText = new javafx.scene.text.Text(
            originalText.getX(),
            originalText.getY(),
            originalText.getText()
        );
        
        // Copia tutte le proprietà
        newText.setFont(originalText.getFont());
        newText.setFill(originalText.getFill());
        newText.setStroke(originalText.getStroke());
        newText.setStrokeWidth(originalText.getStrokeWidth());
        newText.setRotate(originalText.getRotate());
        newText.setScaleX(originalText.getScaleX());
        newText.setScaleY(originalText.getScaleY());
        
        // Crea la nuova shape wrapper
        TextShape clone = new TextShape(
            originalText.getText(),
            originalText.getX(),
            originalText.getY()
        );
        
        // Sostituisce il nodo
        try {
            java.lang.reflect.Field nodeField = AbstractShape.class.getDeclaredField("node");
            nodeField.setAccessible(true);
            nodeField.set(clone, newText);
        } catch (Exception reflectionEx) {
            System.err.println("[CLONE TEXT] Impossibile accedere al campo node: " + reflectionEx.getMessage());
        }
        
        // Applica le proprietà
        clone.setFontSize(getFontSize());
        clone.setColor((javafx.scene.paint.Color) originalText.getFill());
        clone.setRotation(getRotation());
        
        System.out.println("[CLONE TEXT] Creata copia indipendente: '" + 
                          newText.getText() + "' @ (" + newText.getX() + ", " + newText.getY() + ")");
        
        return clone;
        
    } catch (Exception e) {
        System.err.println("[CLONE TEXT ERROR] " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}

    public String getText() {
        return textNode.getText();
    }

    @Override
    public void onDrag(double x, double y) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onRelease() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public void setFontSize(double size) {
        Font oldFont = textNode.getFont();
        textNode.setFont(Font.font(oldFont.getFamily(), size));
    }

    public double getFontSize() {
        return textNode.getFont().getSize();
    }

    @Override
    public double getRotation() {
        return getNode().getRotate();
    }
    
    @Override
    public void setRotation(double degrees) {
        getNode().setRotate(degrees);
    }
}
