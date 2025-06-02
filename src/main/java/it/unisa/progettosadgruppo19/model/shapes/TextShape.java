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

    @Override
    public AbstractShape clone() {
        TextShape cloned = new TextShape(textNode.getText(), textNode.getX(), textNode.getY());
        cloned.setFontSize(this.getFontSize());     // Copia dimensione font
        cloned.setColor((Color) textNode.getFill()); // Copia colore
        return cloned;
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

}
