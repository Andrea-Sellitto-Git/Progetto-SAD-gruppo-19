package it.unisa.progettosadgruppo19.factory;

/**
 * Factory concreta che restituisce lo ShapeCreator corrispondente al tipo
 * testuale fornito.
 */
public class ConcreteShapeCreator {

    /**
     * Restituisce un ShapeCreator in base al nome del tipo.
     *
     * @param tipo "Linea", "Rettangolo" o "Ellisse"
     * @return riferimento al costruttore appropriato
     * @throws IllegalArgumentException se il tipo non è supportato
     */
    public static ShapeCreator getCreator(String tipo) {
        return switch (tipo) {
            case "Linea" ->
                new LineShapeCreator();
            case "Rettangolo" ->
                new RectangleShapeCreator();
            case "Ellisse" ->
                new EllipseShapeCreator();
            case "Testo" ->
                new TextShapeCreator();
            default ->
                throw new IllegalArgumentException("Tipo non supportato: " + tipo);
        };
    }
}
