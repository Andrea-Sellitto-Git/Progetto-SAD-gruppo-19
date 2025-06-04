/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.unisa.progettosadgruppo19.controller;

/**
 * Gestisce i livelli di zoom disponibili e fornisce metodi per zoom in/out.
 */
public class ZoomManager {

    private final double[] levels = {0.5, 0.75, 1.0, 1.5, 2.0};
    private int index = 2; // parte da 1.0x

    /**
     * Aumenta il fattore di zoom al livello successivo, se possibile.
     *
     * @return il nuovo coefficiente di scala.
     */
    public double zoomIn() {
        if (index < levels.length - 1) {
            index++;
        }
        return levels[index];
    }

    /**
     * Diminuisce il fattore di zoom al livello precedente, se possibile.
     *
     * @return il nuovo coefficiente di scala.
     */
    public double zoomOut() {
        if (index > 0) {
            index--;
        }
        return levels[index];
    }

    /**
     * Restituisce il coefficiente di scala corrente.
     *
     * @return coefficiente di zoom attuale.
     */
    public double getScale() {
        return levels[index];
    }
}
