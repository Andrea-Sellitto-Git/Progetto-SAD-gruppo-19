/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.command;

import it.unisa.progettosadgruppo19.command.receiver.ShapeManagerReceiver;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

/**
 *
 * @author mainuser
 */
public class Rotate implements UndoableCommand {

    private ShapeManagerReceiver shapeManager;
    private Shape toRotate;
    private double degreesToRotate;
    private double degreesOriginal;

    public Rotate(ShapeManagerReceiver shapeManager, Shape toRotate,double degrees){
        this.shapeManager = shapeManager;
        this.toRotate = toRotate;
        this.degreesToRotate = degrees;
    }
    
    @Override
    public void undo() {
        shapeManager.rotateShape(toRotate,degreesOriginal);
    }

    @Override
    public void execute() {
        this.degreesOriginal = toRotate.getRotation();
        shapeManager.rotateShape(toRotate,degreesToRotate);
    }
    
}
