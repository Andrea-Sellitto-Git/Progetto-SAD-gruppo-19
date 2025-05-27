/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.controller;


import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author fpagl
 */
public class GridManager {
    private final List<Line> gridLines = new ArrayList<>();
    private final Pane drawingPane;
    private boolean gridVisible = false;
    
    private double spacing = 20;
    
    /** Permette di cambiare la spaziatura della griglia */
    public void setSpacing(double spacing) {
        this.spacing = spacing;
    }

    public GridManager(Pane drawingPane) {
        this.drawingPane = drawingPane;
    }

    public void toggleGrid() {
        gridVisible = !gridVisible;

        if (gridVisible) {
            drawGrid();
        } else {
            removeGrid();
        }
    }

    private void drawGrid() {
        //final double spacing = 20;
        final double width = drawingPane.getWidth();
        final double height = drawingPane.getHeight();

        for (double x = 0; x < width; x += spacing) {
            Line line = new Line(x, 0, x, height);
            line.setStroke(Color.LIGHTGRAY);
            line.getStrokeDashArray().addAll(2.0, 4.0);
            line.setMouseTransparent(true);
            drawingPane.getChildren().add(0, line);
            gridLines.add(line);
        }

        for (double y = 0; y < height; y += spacing) {
            Line line = new Line(0, y, width, y);
            line.setStroke(Color.LIGHTGRAY);
            line.getStrokeDashArray().addAll(2.0, 4.0);
            line.setMouseTransparent(true);
            drawingPane.getChildren().add(0, line);
            gridLines.add(line);
        }
    }

    private void removeGrid() {
        drawingPane.getChildren().removeAll(gridLines);
        gridLines.clear();
    }

    public int getGridLayerCount() {
        return gridLines.size();
    }
    
}
