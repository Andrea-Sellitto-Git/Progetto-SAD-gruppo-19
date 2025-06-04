/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.command.commands;

import it.unisa.progettosadgruppo19.command.UndoableCommand;
import it.unisa.progettosadgruppo19.command.receivers.ShapeManagerReceiver;
import it.unisa.progettosadgruppo19.model.shapes.Shape;

/**
 *
 * @author mainuser
 */
public class ScaleShape implements UndoableCommand {

    private final double delta;
    private final ShapeManagerReceiver shapeManager;
    private final Shape toScale;
    
    public ScaleShape(ShapeManagerReceiver shapeManager,Shape toScale,double delta){
        this.shapeManager = shapeManager;
        this.delta = delta;
        this.toScale = toScale;
    }

    @Override
    public void undo() {
        shapeManager.scale(toScale,-delta);
    }

    @Override
    public void execute() {
        shapeManager.scale(toScale,delta);
    }
    
}
