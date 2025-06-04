package it.unisa.progettosadgruppo19.command.commands;

import it.unisa.progettosadgruppo19.command.UndoableCommand;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import it.unisa.progettosadgruppo19.controller.ShapeManager;

public class Mirror implements UndoableCommand {

    private final ShapeManager shapeManager;
    private final Shape shape;
    private final boolean horizontal; // true = orizzontale, false = verticale

    // Stato precedente per undo
    private double prevScaleX, prevScaleY;
    private double prevTranslateX, prevTranslateY;
    private double prevX, prevY;

    public Mirror(ShapeManager shapeManager, Shape shape, boolean horizontal) {
        this.shapeManager = shapeManager;
        this.shape = shape;
        this.horizontal = horizontal;
    }

    @Override
    public void execute() {
        // Salvo stato precedente
        prevScaleX = shape.getNode().getScaleX();
        prevScaleY = shape.getNode().getScaleY();
        prevTranslateX = shape.getNode().getTranslateX();
        prevTranslateY = shape.getNode().getTranslateY();
        prevX = shape.getX();
        prevY = shape.getY();

        double centerX = shape.getX() + shape.getWidth() / 2;
        double centerY = shape.getY() + shape.getHeight() / 2;

        if (horizontal) {
            // Inverti la scala orizzontale
            shape.getNode().setScaleX(shape.getNode().getScaleX() * -1);

            // Aggiorna posizione x per riflettere correttamente attorno al centro
            double dx = 2 * (shape.getX() + shape.getWidth() / 2 - centerX);
            shape.setX(shape.getX() - dx);
        } else {
            // Inverti la scala verticale
            shape.getNode().setScaleY(shape.getNode().getScaleY() * -1);

            // Aggiorna posizione y per riflettere correttamente attorno al centro
            double dy = 2 * (shape.getY() + shape.getHeight() / 2 - centerY);
            shape.setY(shape.getY() - dy);
        }
    }

    @Override
    public void undo() {
        // Ripristino scale e translate
        shape.getNode().setScaleX(prevScaleX);
        shape.getNode().setScaleY(prevScaleY);
        shape.getNode().setTranslateX(prevTranslateX);
        shape.getNode().setTranslateY(prevTranslateY);

        // Ripristino posizione
        shape.setX(prevX);
        shape.setY(prevY);
    }
}
