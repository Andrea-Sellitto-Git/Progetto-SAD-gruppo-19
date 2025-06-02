/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.factory;

import it.unisa.progettosadgruppo19.model.shapes.Shape;
import it.unisa.progettosadgruppo19.model.shapes.TextShape;
import javafx.scene.paint.Color;

public class TextShapeCreator extends ShapeCreator {

    public Shape createShape(String text, double x, double y, Color stroke, double fontSize) {
        TextShape shape = new TextShape(text, x, y);
        shape.setColor(stroke);  
        shape.setFontSize(fontSize);
        return shape;
    }


    @Override
    public Shape createShape(double x, double y, Color stroke) {
        throw new UnsupportedOperationException("Use createShape(String, double, double, Color) for TextShape.");
    }
}

