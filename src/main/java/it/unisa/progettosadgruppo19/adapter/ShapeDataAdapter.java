package it.unisa.progettosadgruppo19.adapter;

import it.unisa.progettosadgruppo19.model.serialization.ShapeData;

/**
 * Interfaccia che definisce il contratto per gli adapter in grado di
 * trasformare una shape in un oggetto serializzabile {@link ShapeData}.
 */
public interface ShapeDataAdapter {

    /**
     * Restituisce i dati serializzabili della shape associata all'adapter.
     *
     * @return un'istanza di {@link ShapeData} contenente tutte le informazioni
     * necessarie per la persistenza della shape.
     */
    ShapeData getShapeData();
}
