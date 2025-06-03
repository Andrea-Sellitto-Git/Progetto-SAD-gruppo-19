package it.unisa.progettosadgruppo19.model.shapes;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TextShape extends AbstractShape {

    private final Text textNode;
    private Color fillColor = Color.BLACK;
    private Color strokeColor = Color.TRANSPARENT;

    public TextShape(String text, double x, double y) {
        super(new Text(x, y, text));
        this.textNode = (Text) super.node;
        this.textNode.setFill(fillColor);
        this.textNode.setStroke(strokeColor);
    }

    public TextShape(Text textNode) {
        super(textNode);
        this.textNode = textNode;
        this.fillColor = (Color) textNode.getFill();
        this.strokeColor = (Color) textNode.getStroke();
    }

    public void setColor(Color color) {
        this.fillColor = color;
        textNode.setFill(color);
    }

    public Color getColor() {
        this.fillColor = (Color) textNode.getFill();
        return this.fillColor;
    }

    public void setStrokeColor(Color color) {
        this.strokeColor = color;
        textNode.setStroke(color);
    }

    public Color getStrokeColor() {
        this.strokeColor = (Color) textNode.getStroke();
        return this.strokeColor;
    }

    public String getText() {
        return textNode.getText();
    }

    public void setText(String text) {
        textNode.setText(text);
    }

    public void setFontSize(double size) {
        Font oldFont = textNode.getFont();
        textNode.setFont(Font.font(oldFont.getFamily(), size));
    }

    public double getFontSize() {
        return textNode.getFont().getSize();
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
        try {
            // Log dei dati originali del nodo prima della copia
            System.out.println("[CLONE TEXT] Dati nodo originale:");
            System.out.println("- Fill: " + textNode.getFill());
            System.out.println("- Stroke: " + textNode.getStroke());
            System.out.println("- Font: " + textNode.getFont());
            System.out.println("- Text: " + textNode.getText());

            // Creazione del nuovo nodo con copia di tutte le proprietà
            Text newText = new Text(getX(), getY(), getText());
            newText.setFont(textNode.getFont());
            newText.setFill(textNode.getFill());
            newText.setStroke(textNode.getStroke());
            newText.setStrokeWidth(textNode.getStrokeWidth());
            newText.setRotate(textNode.getRotate());
            newText.setScaleX(textNode.getScaleX());
            newText.setScaleY(textNode.getScaleY());

            TextShape clone = new TextShape(newText);

            System.out.println("[CLONE TEXT] Copia creata: '" + newText.getText() + "' @ ("
                    + newText.getX() + ", " + newText.getY() + ")");

            return clone;

        } catch (Exception e) {
            System.err.println("[CLONE TEXT ERROR] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDrag(double x, double y) {
        setX(x);
        setY(y);
    }

    @Override
    public void onRelease() {
        // Nessuna azione necessaria al rilascio per ora
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
