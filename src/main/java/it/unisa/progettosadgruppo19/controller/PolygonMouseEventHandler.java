package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.command.CreateFreeFormPolygon;
import it.unisa.progettosadgruppo19.command.receiver.ShapeManagerReceiver;
import it.unisa.progettosadgruppo19.model.shapes.FreeFormPolygonShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import it.unisa.progettosadgruppo19.decorator.FillDecorator;
import it.unisa.progettosadgruppo19.decorator.StrokeDecorator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestore degli eventi mouse per disegnare un poligono a forma libera con feedback visivo migliorato:
 *  – all'utente basta fare click per inserire vertici
 *  – un cerchietto (indicator) segnala il primo punto
 *  – piccoli indicatori blu mostrano tutti i vertici aggiunti
 *  – cliccando nuovamente sul primo vertice (entro tolleranza), il poligono si chiude e viene "committato"
 *  – validazione per assicurare almeno 3 vertici prima della chiusura
 *  – subito dopo aver chiuso il poligono, l'handler si "spegne" (detach) lasciando il disegno sul Pane
 *  – per iniziarne un altro, è necessario richiamare di nuovo il tool "Poligono" dal Controller
 */
public class PolygonMouseEventHandler {

    private final Pane drawingPane;
    private final List<FreeFormPolygonShape> polygonList;   // elenco di FreeFormPolygonShape (model-list)
    private final ShapeManagerReceiver shapeManager;        // receiver per aggiungere/rimuovere shape
    private final Color strokeColor, fillColor;             // colori correnti (passati da Controller)
    private final double tol = 6.0;                         // tolleranza in pixel per chiudere il poligono

    /**
     * Callback da invocare subito dopo aver chiuso e commit­tato un poligono:
     * di solito richiama Controller.setNeutralTool() per disattivare il tool.
     */
    private final Runnable onComplete;

    private FreeFormPolygonShape basePolygon = null; // modello "puro" in costruzione
    private Shape decoratedPolygon = null;           // shape decorata (Stroke/Fill)
    private Circle startIndicator = null;            // cerchietto attorno al primo vertice
    private List<Circle> vertexIndicators = new ArrayList<>(); // indicatori per tutti i vertici
    private double firstX, firstY;
    private boolean isActive = true; // Flag per controllare se l'handler è attivo

    /**
     * @param drawingPane   il Pane su cui disegnare
     * @param polygonList   la lista di FreeFormPolygonShape (model-list)
     * @param shapeManager  il receiver per aggiungere/rimuovere shape
     * @param strokeColor   colore del bordo
     * @param fillColor     colore di riempimento
     * @param onComplete    callback invocato subito dopo che il poligono è stato chiuso e committato
     */
    public PolygonMouseEventHandler(Pane drawingPane,
                                    List<FreeFormPolygonShape> polygonList,
                                    ShapeManagerReceiver shapeManager,
                                    Color strokeColor,
                                    Color fillColor,
                                    Runnable onComplete) {
        this.drawingPane = drawingPane;
        this.polygonList = polygonList;
        this.shapeManager = shapeManager;
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;
        this.onComplete = onComplete;

        drawingPane.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClick);
    }

    /**
     * Aggiunge un indicatore visivo per un vertice.
     */
    private void addVertexIndicator(double x, double y) {
        Circle indicator = new Circle(x, y, 3, Color.LIGHTBLUE);
        indicator.setStroke(Color.DARKBLUE);
        indicator.setStrokeWidth(1.0);
        indicator.setMouseTransparent(true); // Non interferisce con i click
        drawingPane.getChildren().add(indicator);
        vertexIndicators.add(indicator);
    }

    /**
     * Rimuove tutti gli indicatori dei vertici.
     */
    private void removeAllVertexIndicators() {
        drawingPane.getChildren().removeAll(vertexIndicators);
        vertexIndicators.clear();
    }

    /**
     * Aggiorna l'indicatore del primo vertice per mostrare se il poligono può essere chiuso.
     */
    private void updateStartIndicator() {
        if (startIndicator != null && basePolygon != null) {
            if (basePolygon.canClose()) {
                startIndicator.setStroke(Color.GREEN);
                startIndicator.setStrokeWidth(2.0);
            } else {
                startIndicator.setStroke(Color.GRAY);
                startIndicator.setStrokeWidth(1.0);
            }
        }
    }

    private void onMouseClick(MouseEvent e) {
    // Se l'handler non è attivo, ignora tutti i click
    if (!isActive) {
        return;
    }

    double x = e.getX();
    double y = e.getY();

    // 1) Se non ho un poligono in costruzione, creo il primo vertice
    if (basePolygon == null) {
        basePolygon = new FreeFormPolygonShape(x, y, strokeColor);
        polygonList.add(basePolygon);

        // Creo la versione decorata con stroke e fill
        decoratedPolygon = new StrokeDecorator(basePolygon, strokeColor);
        decoratedPolygon = new FillDecorator(decoratedPolygon, fillColor);

        // Imposta UserData SUBITO dopo la creazione
        decoratedPolygon.getNode().setUserData(decoratedPolygon);
        
        // Aggiungo il nodo decorato al Pane
        drawingPane.getChildren().add(decoratedPolygon.getNode());

        // Disegno l'indicatore attorno al primo vertice
        firstX = x;
        firstY = y;
        startIndicator = new Circle(firstX, firstY, tol, Color.TRANSPARENT);
        startIndicator.setStroke(Color.GRAY);
        startIndicator.setStrokeWidth(1.0);
        startIndicator.setMouseTransparent(true);
        drawingPane.getChildren().add(startIndicator);

        // Aggiungo l'indicatore del primo vertice
        addVertexIndicator(x, y);
        
        System.out.println("[POLYGON] Primo vertice aggiunto. Clicca per aggiungere altri vertici o chiudi cliccando sul primo.");
        return;
    }

    // 2) Se ho già un poligono in costruzione, verifico se il click è vicino al primo vertice
    if (basePolygon.isNearStart(x, y, tol)) {
        // MODIFICATO: Ora si può chiudere anche con meno di 3 vertici
        if (basePolygon.canClose()) {
            // a) Chiudo il poligono
            basePolygon.closePolygon();

            // b) Rimuovo tutti gli indicatori
            drawingPane.getChildren().remove(startIndicator);
            startIndicator = null;
            removeAllVertexIndicators();

            // c) Verifica che UserData sia ancora impostato
            if (decoratedPolygon.getNode().getUserData() == null) {
                System.out.println("[POLYGON] ERRORE: UserData perso! Reimpostando...");
                decoratedPolygon.getNode().setUserData(decoratedPolygon);
            }

            // d) Eseguo il comando undoable per "committare" il poligono finale (decorato)
            CreateFreeFormPolygon cmd = new CreateFreeFormPolygon(shapeManager, decoratedPolygon);
            cmd.execute();

            System.out.println("[POLYGON] Poligono chiuso con " + basePolygon.getVertexCount() + " vertici.");
            if (basePolygon.getVertexCount() >= 3) {
                System.out.println("[POLYGON] Area: " + String.format("%.2f", basePolygon.calculateArea()));
                System.out.println("[POLYGON] Perimetro: " + String.format("%.2f", basePolygon.calculatePerimeter()));
                System.out.println("[POLYGON] Convesso: " + basePolygon.isConvex());
            } else {
                System.out.println("[POLYGON] Poligono con meno di 3 vertici - area e convessità non calcolabili.");
            }
            
            // e) Reset delle variabili prima di disattivare
            FreeFormPolygonShape completedPolygon = basePolygon;
            Shape completedDecorated = decoratedPolygon;
            basePolygon = null;
            decoratedPolygon = null;
            
            // f) Disattiva l'handler PRIMA di fare detach e callback
            isActive = false;
            
            // g) Detach e callback per tornare in modalità neutra
            detach();
            
            // h) Invoca il callback ALLA FINE per assicurarsi che tutto sia pulito
            if (onComplete != null) {
                onComplete.run();
            }
            
            System.out.println("[POLYGON] Strumento poligono disattivato automaticamente.");
        } else {
            System.out.println("[POLYGON] Serve almeno 1 vertice aggiuntivo per chiudere il poligono. Attuali: " + basePolygon.getVertexCount());
        }
        return;
    }

    // 3) Se arrivo qui, non sto chiudendo: aggiungo un vertice
    if (basePolygon != null) {
        basePolygon.addPoint(x, y);
        addVertexIndicator(x, y);
        updateStartIndicator();
        
        // Aggiorna UserData dopo ogni modifica
        if (decoratedPolygon != null && decoratedPolygon.getNode().getUserData() == null) {
            decoratedPolygon.getNode().setUserData(decoratedPolygon);
            System.out.println("[POLYGON] UserData reimpostato dopo aggiunta vertice");
        }
        
        System.out.println("[POLYGON] Vertice " + basePolygon.getVertexCount() + " aggiunto.");
        if (basePolygon.canClose()) {
            System.out.println("[POLYGON] Il poligono può ora essere chiuso cliccando sul primo vertice.");
        }
    }
}

    /**
     * Chiama questo metodo quando l'utente cambia strumento (Controller):
     *  – rimuove il listener dei click
     *  – se c'è un poligono incompleto (basePolygon != null), lo cancella dal Pane e dalla lista
     *  – rimuove tutti gli indicatori visivi
     */
    public void detach() {
        // 1) Rimuovo l'handler dei click
        drawingPane.removeEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClick);

        // 2) Se era presente un indicatore del primo vertice, lo tolgo
        if (startIndicator != null) {
            drawingPane.getChildren().remove(startIndicator);
            startIndicator = null;
        }

        // 3) Rimuovo tutti gli indicatori dei vertici
        removeAllVertexIndicators();

        // 4) Se c'è basePolygon che non è stato mai chiuso (incompleto), rimuovo tutto
        if (decoratedPolygon != null && basePolygon != null) {
            // Rimuovo il nodo decorato dal Pane
            drawingPane.getChildren().remove(decoratedPolygon.getNode());
            // Rimuovo il modello dalla lista
            polygonList.remove(basePolygon);
            decoratedPolygon = null;
            basePolygon = null;
            System.out.println("[POLYGON] Poligono incompleto rimosso.");
        }
        
        // 5) Disattiva l'handler
        isActive = false;
        
        // Se basePolygon == null significa che il poligono era già stato chiuso,
        // quindi non tocco il nodo (lo lascia il command), e non tocco la lista.
    }

    /**
     * Restituisce il numero di vertici del poligono corrente.
     */
    public int getCurrentVertexCount() {
        return basePolygon != null ? basePolygon.getVertexCount() : 0;
    }

    /**
     * Verifica se c'è un poligono in costruzione.
     */
    public boolean isPolygonInProgress() {
        return basePolygon != null && isActive;
    }

    /**
     * Verifica se il poligono corrente può essere chiuso.
     */
    public boolean canCloseCurrentPolygon() {
        return basePolygon != null && basePolygon.canClose() && isActive;
    }
    
    /**
     * Verifica se l'handler è attivo.
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Riattiva l'handler (utile se si vuole riutilizzare lo stesso handler).
     */
    public void reactivate() {
        isActive = true;
        System.out.println("[POLYGON] Handler riattivato.");
    }
}