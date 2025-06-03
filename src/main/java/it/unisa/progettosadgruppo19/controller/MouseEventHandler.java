package it.unisa.progettosadgruppo19.controller;

import it.unisa.progettosadgruppo19.decorator.FillDecorator;
import it.unisa.progettosadgruppo19.decorator.StrokeDecorator;
import it.unisa.progettosadgruppo19.factory.ConcreteShapeCreator;
import it.unisa.progettosadgruppo19.factory.ShapeCreator;
import it.unisa.progettosadgruppo19.model.shapes.AbstractShape;
import it.unisa.progettosadgruppo19.model.shapes.Shape;
import it.unisa.progettosadgruppo19.model.shapes.FreeFormPolygonShape;
import it.unisa.progettosadgruppo19.command.*;
import it.unisa.progettosadgruppo19.command.receiver.ClipboardReceiver;
import it.unisa.progettosadgruppo19.model.shapes.TextShape;
import it.unisa.progettosadgruppo19.strategy.*;
import it.unisa.progettosadgruppo19.util.GeometryUtils;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;

import java.util.List;
import java.util.ArrayList;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * Gestisce gli eventi mouse sul canvas per creare, selezionare, spostare,
 * ridimensionare, decorare e incollare le Shape. Include supporto completo
 * per i poligoni con movimento e ridimensionamento.
 * Utilizza GeometryUtils per operazioni geometriche.
 * 
 * VERSIONE AGGIORNATA con supporto per selezione multipla.
 */
public class MouseEventHandler implements ClipboardReceiver {

    private final Pane drawingPane;
    private final List<AbstractShape> currentShapes;

    private String text;
    private double toolbarHeight = 50;
    private boolean toolbarHeightInitialized = false;
    private Shape selectedShapeInstance;
    private String selectedShape;
    private Color strokeColor;
    private Color fillColor;
    private Shape tempShape;

    private boolean toolActive = false;
    private ResizeMode currentResizeMode = ResizeMode.NONE;

    private double moveAnchorX, moveAnchorY;
    private double origX1, origY1, origX2, origY2;
    private double origX, origY;
    private double origCenterX, origCenterY;
    private double origRadiusX, origRadiusY;
    private double resizeAnchorX, resizeAnchorY;
    private double startMouseX, startMouseY;
    private double lastMouseX, lastMouseY;
    private double fontSize = 12;

    // Variabili specifiche per i poligoni
    private double origPolygonX, origPolygonY, origPolygonWidth, origPolygonHeight;
    private List<Double> origPolygonPoints;

    private static final double HANDLE_RADIUS = 6.0;
    private static final double ELLIPSE_BORDER_TOLERANCE = 6.0;
    private static final double POLYGON_BORDER_TOLERANCE = 8.0;

    private Shape shapeToPaste;
    private Shape clipboardBuffer;

    private boolean isDragging = false;
    private double pressX, pressY;

    private StackUndoInvoker invoker;
    
    // NUOVI CAMPI PER SELEZIONE MULTIPLA
    private MultipleSelectionManager multipleSelectionManager;
    private boolean isDraggingSelection = false;
    private double selectionDragStartX, selectionDragStartY;
    
    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }

    public enum ResizeMode {
        NONE,
        LINE_START, LINE_END,
        RECT_LEFT, RECT_RIGHT, RECT_TOP, RECT_BOTTOM,
        RECT_TOP_LEFT, RECT_TOP_RIGHT, RECT_BOTTOM_LEFT, RECT_BOTTOM_RIGHT,
        ELLIPSE_BORDER,
        POLYGON_BORDER  // Nuovo: ridimensionamento del poligono
    }

    /**
     * Costruisce un handler per il Pane e la lista di shape correnti.
     */
    public MouseEventHandler(Pane drawingPane, List<AbstractShape> currentShapes) {
        this.drawingPane = drawingPane;
        this.currentShapes = currentShapes;
        this.shapeToPaste = null;
    }

    // NUOVO SETTER PER SELEZIONE MULTIPLA
    public void setMultipleSelectionManager(MultipleSelectionManager manager) {
        this.multipleSelectionManager = manager;
    }

    public void setToolbarHeight(double toolbarHeight) {
        this.toolbarHeight = toolbarHeight;
        this.toolbarHeightInitialized = true;
    }

    public void setSelectedShape(String selectedShape) {
        this.selectedShape = selectedShape;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public void setSelectedShapeInstance(Shape shape) {
        this.selectedShapeInstance = shape;
    }

    @Override
    public void setClipboard(Shape shape) {
        this.clipboardBuffer = shape;
    }

    @Override
    public Shape getClipboard() {
        return clipboardBuffer;
    }

    public Shape getSelectedShapeInstance() {
        return selectedShapeInstance;
    }

    public void setToolActive(boolean active) {
        this.toolActive = active;
    }

    public void setInvoker(StackUndoInvoker invoker) {
        this.invoker = invoker;
    }

    /**
     * Verifica se un punto è vicino a una linea usando GeometryUtils.
     */
    private boolean isNearLine(double px, double py, double x1, double y1, double x2, double y2, double tolerance) {
        return GeometryUtils.isNearLine(px, py, x1, y1, x2, y2, tolerance);
    }

    /**
     * Trova la shape più in alto (ultimo nell'ordine Z) al punto specificato.
     */
    private Shape findShapeAtPoint(double x, double y) {
        // Cerca dall'ultimo al primo (ordine Z inverso) per trovare la shape più in alto
        for (int i = drawingPane.getChildren().size() - 1; i >= 0; i--) {
            Node node = drawingPane.getChildren().get(i);
            
            if (!(node instanceof javafx.scene.shape.Shape fxShape)) {
                continue;
            }

            boolean isHit = false;
            String nodeType = fxShape.getClass().getSimpleName();

            if (fxShape instanceof Line line) {
                double sx = line.getStartX(), sy = line.getStartY();
                double ex = line.getEndX(), ey = line.getEndY();
                isHit = GeometryUtils.isNearLine(x, y, sx, sy, ex, ey, HANDLE_RADIUS);
            } else if (fxShape instanceof Polygon polygon) {
                // Test migliorato per poligoni
                isHit = polygon.contains(x, y);
                if (!isHit) {
                    isHit = GeometryUtils.isPointInPolygon(x, y, polygon.getPoints());
                }
                if (!isHit) {
                    isHit = GeometryUtils.isNearPolygonBorder(x, y, polygon.getPoints(), 10.0);
                }
            } else {
                isHit = fxShape.contains(x, y);
            }

            if (isHit) {
                Object userData = fxShape.getUserData();
                if (userData instanceof Shape shape) {
                    System.out.println("[FIND SHAPE] Trovata: " + shape.getClass().getSimpleName());
                    return shape;
                }
            }
        }
        
        System.out.println("[FIND SHAPE] Nessuna shape trovata al punto (" + x + ", " + y + ")");
        return null;
    }

    /**
     * Rimuove tutti gli effetti visivi dalle shape.
     */
    private void clearAllVisualEffects() {
        for (Node node : drawingPane.getChildren()) {
            if (node instanceof javafx.scene.shape.Shape fxShape) {
                fxShape.setStrokeWidth(1);
                fxShape.setEffect(null);
            }
        }
    }

    /**
     * Applica l'effetto visivo di selezione a una shape.
     */
    private void applySelectionEffect(Shape shape) {
        try {
            Node node = shape.getNode();
            if (node instanceof javafx.scene.shape.Shape fxShape) {
                DropShadow ds = new DropShadow();
                ds.setRadius(10);
                ds.setColor(Color.BLACK);
                fxShape.setEffect(ds);
            }
        } catch (Exception e) {
            System.err.println("[SELECTION EFFECT] Errore: " + e.getMessage());
        }
    }
    
    private void startTextEditing(TextShape textShape) {
        TextField textField = new TextField(textShape.getText());

        // Posiziona il TextField sopra il testo (aggiusta coordinate se serve)
        double x = textShape.getX();
        double y = textShape.getY() - textShape.getHeight();
        textField.setLayoutX(x);
        textField.setLayoutY(y);
        textField.setPrefColumnCount(10);

        drawingPane.getChildren().add(textField);
        textField.requestFocus();

        // Quando si conferma o perde focus, aggiorna il testo e rimuovi il TextField
        textField.setOnAction(e -> finalizeTextEdit(textShape, textField));
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                finalizeTextEdit(textShape, textField);
            }
        });
    }

    private void finalizeTextEdit(TextShape textShape, TextField textField) {
        String newText = textField.getText();
        if (newText != null && !newText.trim().isEmpty()) {
            ((Text) textShape.getNode()).setText(newText);
        }
        drawingPane.getChildren().remove(textField);
    }

    /**
     * METODO MODIFICATO - Evento mouse clicked con supporto selezione multipla.
     */
    public void onMouseClick(MouseEvent e) {
        System.out.println("[CLICK] Click su (" + e.getX() + ", " + e.getY() + ")");
        if (e.getClickCount() == 2 && selectedShapeInstance instanceof TextShape textShape) {
            startTextEditing(textShape);
            return;  // Esci dal metodo per evitare altre azioni
        }
        
        
        // Gestione logica incolla (rimane invariata)
        if (shapeToPaste != null) {
            handlePaste(e.getX(), e.getY());
            shapeToPaste = null;
            return;
        }

        // Trova la shape cliccata
        Shape clickedShape = findShapeAtPoint(e.getX(), e.getY());
        
        // Gestione della selezione multipla
        if (multipleSelectionManager != null && multipleSelectionManager.isMultipleSelectionMode()) {
            if (clickedShape != null) {
                // Toggle della selezione per la shape cliccata
                multipleSelectionManager.toggleSelection(clickedShape);
                System.out.println("[MULTI-SELECT] Toggle shape: " + clickedShape.getClass().getSimpleName());
            } else {
                // Click su area vuota: pulisce la selezione se non si tiene CTRL
                if (!e.isControlDown()) {
                    multipleSelectionManager.clearSelection();
                    System.out.println("[MULTI-SELECT] Selezione pulita (click su area vuota)");
                }
            }
            
            // Non impostare selectedShapeInstance in modalità multi-selezione
            selectedShapeInstance = null;
            return;
        }
        
        // Modalità di selezione singola (comportamento originale)
        // Reset degli effetti visivi
        clearAllVisualEffects();
        
        selectedShapeInstance = clickedShape;
        
        if (selectedShapeInstance != null) {
            // Applica effetto visivo di selezione
            applySelectionEffect(selectedShapeInstance);
            System.out.println("[CLICK] ✅ SELEZIONATO: " + selectedShapeInstance.getClass().getSimpleName());
        } else {
            System.out.println("[CLICK] ❌ Nessuna shape selezionata");
            if (toolActive) {
                return;
            }
            toolActive = false;
        }
    }

    /**
     * METODO MODIFICATO - Evento mouse pressed con supporto selezione multipla.
     */
    public void onPressed(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        isDragging = false;
        pressX = x;
        pressY = y;

        // NUOVO: Gestione trascinamento selezione multipla
        if (multipleSelectionManager != null && multipleSelectionManager.hasSelection()) {
            // Verifica se il click è su una delle shape selezionate
            Shape clickedShape = findShapeAtPoint(x, y);
            if (clickedShape != null && multipleSelectionManager.isSelected(clickedShape)) {
                // Inizia il trascinamento di gruppo
                isDraggingSelection = true;
                selectionDragStartX = x;
                selectionDragStartY = y;
                System.out.println("[MULTI-DRAG] Inizio trascinamento gruppo da (" + x + ", " + y + ")");
                return;
            } else {
                // Click su shape non selezionata o area vuota
                isDraggingSelection = false;
            }
        }

        System.out.println("[PRESSED] Click su (" + x + ", " + y + ")");
        
        if (selectedShapeInstance != null) {
            Node node = selectedShapeInstance.getNode();
            System.out.println("[PRESSED] Shape selezionata: " + selectedShapeInstance.getClass().getSimpleName());

            if (node instanceof Line line) {
                double sx = line.getStartX(), sy = line.getStartY();
                double ex = line.getEndX(), ey = line.getEndY();

                if (Math.hypot(x - sx, y - sy) < HANDLE_RADIUS) {
                    currentResizeMode = ResizeMode.LINE_START;
                    System.out.println("[PRESSED] Modalità resize LINE_START");
                    return;
                }
                if (Math.hypot(x - ex, y - ey) < HANDLE_RADIUS) {
                    currentResizeMode = ResizeMode.LINE_END;
                    System.out.println("[PRESSED] Modalità resize LINE_END");
                    return;
                }
            } else if (node instanceof Rectangle rect) {
                double rx = rect.getX(), ry = rect.getY();
                double w = rect.getWidth(), h = rect.getHeight();
                boolean left = Math.abs(x - rx) < HANDLE_RADIUS;
                boolean right = Math.abs(x - (rx + w)) < HANDLE_RADIUS;
                boolean top = Math.abs(y - ry) < HANDLE_RADIUS;
                boolean bottom = Math.abs(y - (ry + h)) < HANDLE_RADIUS;

                if (left && top) {
                    currentResizeMode = ResizeMode.RECT_TOP_LEFT;
                } else if (right && top) {
                    currentResizeMode = ResizeMode.RECT_TOP_RIGHT;
                } else if (left && bottom) {
                    currentResizeMode = ResizeMode.RECT_BOTTOM_LEFT;
                } else if (right && bottom) {
                    currentResizeMode = ResizeMode.RECT_BOTTOM_RIGHT;
                } else if (left) {
                    currentResizeMode = ResizeMode.RECT_LEFT;
                } else if (right) {
                    currentResizeMode = ResizeMode.RECT_RIGHT;
                } else if (top) {
                    currentResizeMode = ResizeMode.RECT_TOP;
                } else if (bottom) {
                    currentResizeMode = ResizeMode.RECT_BOTTOM;
                }

                if (currentResizeMode != ResizeMode.NONE) {
                    resizeAnchorX = switch (currentResizeMode) {
                        case RECT_TOP_LEFT, RECT_BOTTOM_LEFT, RECT_LEFT ->
                            rect.getX() + rect.getWidth();
                        case RECT_TOP_RIGHT, RECT_BOTTOM_RIGHT, RECT_RIGHT ->
                            rect.getX();
                        default -> x;
                    };
                    resizeAnchorY = switch (currentResizeMode) {
                        case RECT_TOP_LEFT, RECT_TOP_RIGHT, RECT_TOP ->
                            rect.getY() + rect.getHeight();
                        case RECT_BOTTOM_LEFT, RECT_BOTTOM_RIGHT, RECT_BOTTOM ->
                            rect.getY();
                        default -> y;
                    };
                    System.out.println("[PRESSED] Modalità resize RECT: " + currentResizeMode);
                    return;
                }
            } else if (node instanceof Ellipse ell) {
                double cx = ell.getCenterX(), cy = ell.getCenterY();
                double rx = ell.getRadiusX(), ry = ell.getRadiusY();
                double dx = (x - cx) / rx;
                double dy = (y - cy) / ry;
                double d = Math.hypot(dx, dy);
                if (Math.abs(d - 1) < ELLIPSE_BORDER_TOLERANCE / Math.max(rx, ry)) {
                    currentResizeMode = ResizeMode.ELLIPSE_BORDER;
                    System.out.println("[PRESSED] Modalità resize ELLIPSE_BORDER");
                    return;
                }
            } else if (node instanceof Polygon && selectedShapeInstance instanceof FreeFormPolygonShape polygonShape) {
                // CORREZIONE: Migliore rilevamento per i poligoni
                System.out.println("[PRESSED] Controllo poligono per resize/move");
                
                if (polygonShape.isNearBorder(x, y, POLYGON_BORDER_TOLERANCE)) {
                    currentResizeMode = ResizeMode.POLYGON_BORDER;
                    // Salva lo stato originale del poligono
                    origPolygonX = polygonShape.getX();
                    origPolygonY = polygonShape.getY();
                    origPolygonWidth = polygonShape.getWidth();
                    origPolygonHeight = polygonShape.getHeight();
                    origPolygonPoints = new ArrayList<>(polygonShape.getPoints());
                    
                    // Imposta il punto di ancoraggio per il resize
                    double[] center = polygonShape.getCenter();
                    resizeAnchorX = center[0];
                    resizeAnchorY = center[1];
                    
                    System.out.println("[PRESSED] Modalità resize POLYGON_BORDER");
                    return;
                } else {
                    System.out.println("[PRESSED] Click interno al poligono - modalità movimento");
                }
            } else if (node instanceof javafx.scene.text.Text textNode) {
                origX = textNode.getX();
                origY = textNode.getY();
            }
        }

        if (currentResizeMode != ResizeMode.NONE) {
            System.out.println("[PRESSED] Resize mode attivo: " + currentResizeMode);
            return;
        }

        if (selectedShapeInstance != null) {
            moveAnchorX = x;
            moveAnchorY = y;

            Node node = selectedShapeInstance.getNode();
            if (node instanceof Line line) {
                origX1 = line.getStartX();
                origY1 = line.getStartY();
                origX2 = line.getEndX();
                origY2 = line.getEndY();
            } else if (node instanceof Rectangle rect) {
                origX = rect.getX();
                origY = rect.getY();
            } else if (node instanceof Ellipse ell) {
                origCenterX = ell.getCenterX();
                origCenterY = ell.getCenterY();
                origRadiusX = ell.getRadiusX();
                origRadiusY = ell.getRadiusY();
            } else if (node instanceof Polygon && selectedShapeInstance instanceof FreeFormPolygonShape polygonShape) {
                // CORREZIONE: Salva la posizione originale del poligono per il movimento
                origX = polygonShape.getX();
                origY = polygonShape.getY();
                System.out.println("[PRESSED] Preparato per movimento poligono da (" + origX + ", " + origY + ")");
            }
            
            System.out.println("[PRESSED] Preparato per movimento/resize");
            return;
        }

        if (!toolActive || selectedShape == null) {
            System.out.println("[PRESSED] Tool non attivo o shape non selezionata");
            return;
        }

        ShapeCreator creator = ConcreteShapeCreator.getCreator(selectedShape);
        AbstractShape baseShape = null;

        if ("Testo".equals(selectedShape)) {
            if (text == null || text.isEmpty()) {
                System.out.println("Testo vuoto, shape testo non creata.");
                return;
            }
            double fontSize = 12;
            
            baseShape = (AbstractShape) creator.createShape(text, x, y, strokeColor, fontSize);
        } else {
            baseShape = (AbstractShape) creator.createShape(x, y, strokeColor);
        }

        if (baseShape == null) {
            System.out.println("Errore nella creazione della shape");
            return;
        }

        currentShapes.add(baseShape);

        tempShape = new StrokeDecorator(baseShape, strokeColor);
        tempShape = new FillDecorator(tempShape, fillColor);
        tempShape.getNode().setUserData(tempShape);
        drawingPane.getChildren().add(tempShape.getNode());
        
        System.out.println("[PRESSED] Creata nuova shape: " + tempShape.getClass().getSimpleName());
    }

    /**
     * METODO MODIFICATO - Evento mouse dragged con supporto selezione multipla.
     */
    public void onDragged(MouseEvent e) {
        double x = Math.min(Math.max(0, e.getX()), drawingPane.getWidth());
        double y = Math.min(Math.max(toolbarHeight, e.getY()), drawingPane.getHeight());

        lastMouseX = x;
        lastMouseY = y;

        if (!isDragging) {
            isDragging = true;
            System.out.println("[DRAGGED] Iniziato trascinamento");
        }

        // NUOVO: Gestione trascinamento selezione multipla
        if (isDraggingSelection && multipleSelectionManager != null && multipleSelectionManager.hasSelection()) {
            double deltaX = x - selectionDragStartX;
            double deltaY = y - selectionDragStartY;
            
            // Sposta tutte le shape selezionate
            for (Shape shape : multipleSelectionManager.getSelectedShapes()) {
                Node node = shape.getNode();
                
                if (node instanceof Line line) {
                    line.setStartX(line.getStartX() + deltaX);
                    line.setStartY(line.getStartY() + deltaY);
                    line.setEndX(line.getEndX() + deltaX);
                    line.setEndY(line.getEndY() + deltaY);
                } else if (node instanceof Rectangle rect) {
                    rect.setX(rect.getX() + deltaX);
                    rect.setY(rect.getY() + deltaY);
                } else if (node instanceof Ellipse ell) {
                    ell.setCenterX(ell.getCenterX() + deltaX);
                    ell.setCenterY(ell.getCenterY() + deltaY);
                } else if (node instanceof javafx.scene.text.Text textNode) {
                    textNode.setX(textNode.getX() + deltaX);
                    textNode.setY(textNode.getY() + deltaY);
                } else if (node instanceof Polygon && shape instanceof FreeFormPolygonShape polygonShape) {
                    polygonShape.translate(deltaX, deltaY);
                }
            }
            
            // Aggiorna la posizione di riferimento
            selectionDragStartX = x;
            selectionDragStartY = y;
            
            System.out.println("[MULTI-DRAG] Spostamento gruppo: (" + deltaX + ", " + deltaY + ")");
            return;
        }

        // Resize attivo
        if (selectedShapeInstance != null && currentResizeMode != ResizeMode.NONE) {
            Node node = selectedShapeInstance.getNode();
            
            switch (currentResizeMode) {
                case LINE_START -> {
                    Line l = (Line) node;
                    l.setStartX(x);
                    l.setStartY(y);
                }
                case LINE_END -> {
                    Line l = (Line) node;
                    l.setEndX(x);
                    l.setEndY(y);
                }
                case RECT_TOP_LEFT, RECT_TOP_RIGHT, RECT_BOTTOM_LEFT, RECT_BOTTOM_RIGHT -> {
                    Rectangle r = (Rectangle) node;
                    double newX = Math.min(x, resizeAnchorX);
                    double newY = Math.min(y, resizeAnchorY);
                    double newW = Math.max(1, Math.abs(resizeAnchorX - x));
                    double newH = Math.max(1, Math.abs(resizeAnchorY - y));
                    r.setX(newX);
                    r.setY(newY);
                    r.setWidth(newW);
                    r.setHeight(newH);
                }
                case RECT_LEFT, RECT_RIGHT -> {
                    Rectangle r = (Rectangle) node;
                    double newX = Math.min(x, resizeAnchorX);
                    double newW = Math.max(1, Math.abs(resizeAnchorX - x));
                    r.setX(newX);
                    r.setWidth(newW);
                }
                case RECT_TOP, RECT_BOTTOM -> {
                    Rectangle r = (Rectangle) node;
                    double newY = Math.min(y, resizeAnchorY);
                    double newH = Math.max(1, Math.abs(resizeAnchorY - y));
                    r.setY(newY);
                    r.setHeight(newH);
                }
                case ELLIPSE_BORDER -> {
                    Ellipse ell = (Ellipse) node;
                    double cx = (x + resizeAnchorX) / 2;
                    double cy = (y + resizeAnchorY) / 2;
                    double radiusX = Math.max(1, Math.abs(x - resizeAnchorX) / 2);
                    double radiusY = Math.max(1, Math.abs(y - resizeAnchorY) / 2);
                    ell.setCenterX(cx);
                    ell.setCenterY(cy);
                    ell.setRadiusX(radiusX);
                    ell.setRadiusY(radiusY);
                }
                case POLYGON_BORDER -> {
                    // CORREZIONE: Ridimensionamento migliorato del poligono
                    if (selectedShapeInstance instanceof FreeFormPolygonShape polygonShape) {
                        System.out.println("[DRAGGED] Ridimensionamento poligono");
                        resizePolygonImproved(polygonShape, x, y);
                    }
                }
                default -> {}
            }
            return;
        }

        // Spostamento
        if (selectedShapeInstance != null) {
            double dx = x - moveAnchorX;
            double dy = y - moveAnchorY;
            Node node = selectedShapeInstance.getNode();

            if (node instanceof Line line) {
                line.setStartX(line.getStartX() + dx);
                line.setStartY(line.getStartY() + dy);
                line.setEndX(line.getEndX() + dx);
                line.setEndY(line.getEndY() + dy);
            } else if (node instanceof Rectangle rect) {
                rect.setX(rect.getX() + dx);
                rect.setY(rect.getY() + dy);
            } else if (node instanceof Ellipse ell) {
                ell.setCenterX(ell.getCenterX() + dx);
                ell.setCenterY(ell.getCenterY() + dy);
            } else if (node instanceof javafx.scene.text.Text textNode) {
                textNode.setX(textNode.getX() + dx);
                textNode.setY(textNode.getY() + dy);
                moveAnchorX = x;
                moveAnchorY = y;
                return;
            } else if (node instanceof Polygon && selectedShapeInstance instanceof FreeFormPolygonShape polygonShape) {
                // CORREZIONE: Spostamento migliorato del poligono
                System.out.println("[DRAGGED] Spostamento poligono dx=" + dx + ", dy=" + dy);
                polygonShape.translate(dx, dy);
            }
            
            moveAnchorX = x;
            moveAnchorY = y;
        }

        // Disegno figura nuova
        if (toolActive && selectedShape != null) {
            if (!isDragging) {
                isDragging = true;
                ShapeCreator creator = ConcreteShapeCreator.getCreator(selectedShape);
                AbstractShape baseShape = (AbstractShape) creator.createShape(pressX, pressY, strokeColor);
                currentShapes.add(baseShape);
                tempShape = new StrokeDecorator(baseShape, strokeColor);
                tempShape = new FillDecorator(tempShape, fillColor);
                tempShape.getNode().setUserData(tempShape);
                drawingPane.getChildren().add(tempShape.getNode());
            }

            if (tempShape != null) {
                tempShape.onDrag(x, y);
            }
        }
    }

    /**
     * Ridimensiona un poligono migliorato con gestione più precisa.
     */
    private void resizePolygonImproved(FreeFormPolygonShape polygonShape, double mouseX, double mouseY) {
        if (origPolygonPoints == null || origPolygonPoints.isEmpty()) {
            System.out.println("[RESIZE] Punti originali non disponibili");
            return;
        }
        
        try {
            // Calcola il fattore di scala basato sulla distanza dal centro
            double centerX = resizeAnchorX;
            double centerY = resizeAnchorY;
            
            // Distanza iniziale dal centro al punto di click
            double originalDistance = Math.hypot(pressX - centerX, pressY - centerY);
            // Distanza corrente dal centro al mouse
            double currentDistance = Math.hypot(mouseX - centerX, mouseY - centerY);
            
            // Calcola il fattore di scala (minimo 0.1 per evitare dimensioni troppo piccole)
            double scaleFactor = Math.max(0.1, currentDistance / Math.max(originalDistance, 1.0));
            
            System.out.println("[RESIZE] ScaleFactor: " + scaleFactor + " (dist orig: " + originalDistance + ", curr: " + currentDistance + ")");
            
            List<Double> newPoints = new ArrayList<>();
            
            for (int i = 0; i < origPolygonPoints.size(); i += 2) {
                double origX = origPolygonPoints.get(i);
                double origY = origPolygonPoints.get(i + 1);
                
                // Scala rispetto al centro
                double newX = centerX + (origX - centerX) * scaleFactor;
                double newY = centerY + (origY - centerY) * scaleFactor;
                
                newPoints.add(newX);
                newPoints.add(newY);
            }
            
            // Aggiorna i punti nel poligono
            polygonShape.setAllPoints(newPoints);
            
        } catch (Exception e) {
            System.err.println("[RESIZE POLYGON ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * METODO MODIFICATO - Evento mouse released con supporto selezione multipla.
     */
    public void onReleased(MouseEvent e) {
        // NUOVO: Gestione fine trascinamento selezione multipla
        if (isDraggingSelection && multipleSelectionManager != null && multipleSelectionManager.hasSelection()) {
            double totalDeltaX = e.getX() - pressX;
            double totalDeltaY = e.getY() - pressY;
            
            if (Math.abs(totalDeltaX) > 1 || Math.abs(totalDeltaY) > 1) {
                // Crea comando undo per il movimento di gruppo
                List<Shape> movedShapes = multipleSelectionManager.getSelectedShapes();
                MultiMoveCommand moveCommand = new MultiMoveCommand(movedShapes, totalDeltaX, totalDeltaY);
                
                if (invoker != null) {
                    invoker.execute(moveCommand);
                }
                
                System.out.println("[MULTI-DRAG END] Movimento gruppo completato: (" + totalDeltaX + ", " + totalDeltaY + ")");
            }
            
            isDraggingSelection = false;
            isDragging = false;
            return;
        }

        if (currentResizeMode != ResizeMode.NONE && selectedShapeInstance != null) {
            javafx.scene.shape.Shape fx = (javafx.scene.shape.Shape) selectedShapeInstance.getNode();

            if (fx instanceof Rectangle rect) {
                double oldX = origX;
                double oldY = origY;
                double oldW = rect.getWidth();
                double oldH = rect.getHeight();

                double newX = rect.getX();
                double newY = rect.getY();
                double newW = rect.getWidth();
                double newH = rect.getHeight();

                applyUndoableStrategy(new Resize(selectedShapeInstance,
                        oldX, oldY, oldW, oldH, newX, newY, newW, newH));

            } else if (fx instanceof Ellipse ell) {
                double oldCX = origCenterX;
                double oldCY = origCenterY;
                double oldRX = ell.getRadiusX();
                double oldRY = ell.getRadiusY();

                double newCX = ell.getCenterX();
                double newCY = ell.getCenterY();
                double newRX = ell.getRadiusX();
                double newRY = ell.getRadiusY();

                applyUndoableStrategy(new Resize(selectedShapeInstance,
                        oldCX, oldCY, oldRX, oldRY, newCX, newCY, newRX, newRY));

            } else if (fx instanceof Line line) {
                double oldStartX = origX1;
                double oldStartY = origY1;
                double oldEndX = origX2;
                double oldEndY = origY2;

                double newStartX = line.getStartX();
                double newStartY = line.getStartY();
                double newEndX = line.getEndX();
                double newEndY = line.getEndY();

                applyUndoableStrategy(new Resize(selectedShapeInstance,
                        oldStartX, oldStartY, oldEndX, oldEndY,
                        newStartX, newStartY, newEndX, newEndY));
            } else if (fx instanceof Polygon && selectedShapeInstance instanceof FreeFormPolygonShape polygonShape) {
                // CORREZIONE: Usa il comando appropriato per il poligono
                if (origPolygonPoints != null && !origPolygonPoints.equals(polygonShape.getPoints())) {
                    System.out.println("[POLYGON RESIZE] Creando comando ResizePolygon");
                    // Crea un comando personalizzato inline
                    UndoableCommand resizeCommand = new UndoableCommand() {
                        private final List<Double> oldPoints = new ArrayList<>(origPolygonPoints);
                        private final List<Double> newPoints = new ArrayList<>(polygonShape.getPoints());
                        
                        @Override
                        public void execute() {
                            polygonShape.setAllPoints(newPoints);
                            System.out.println("[POLYGON RESIZE] Applicato resize");
                        }
                        
                        @Override
                        public void undo() {
                            polygonShape.setAllPoints(oldPoints);
                            System.out.println("[POLYGON RESIZE UNDO] Ripristinato stato originale");
                        }
                    };
                    
                    if (invoker != null) {
                        invoker.execute(resizeCommand);
                    }
                }
            }

            currentResizeMode = ResizeMode.NONE;
            origPolygonPoints = null; // Reset
            return;
        }

        if (selectedShapeInstance != null) {
            Node node = selectedShapeInstance.getNode();

            if (node instanceof Rectangle rect) {
                double newX = rect.getX();
                double newY = rect.getY();
                if (origX != newX || origY != newY) {
                    applyUndoableStrategy(new Move(selectedShapeInstance, origX, origY, newX, newY));
                }
            } else if (node instanceof Ellipse ell) {
                double newCX = ell.getCenterX();
                double newCY = ell.getCenterY();
                if (origCenterX != newCX || origCenterY != newCY) {
                    applyUndoableStrategy(new Move(selectedShapeInstance, origCenterX, origCenterY, newCX, newCY));
                }
            } else if (node instanceof Line line) {
                double newStartX = line.getStartX();
                double newStartY = line.getStartY();
                double newEndX = line.getEndX();
                double newEndY = line.getEndY();
                if (origX1 != newStartX || origY1 != newStartY || origX2 != newEndX || origY2 != newEndY) {
                    applyUndoableStrategy(new Move(selectedShapeInstance,
                            origX1, origY1, origX2, origY2,
                            newStartX, newStartY, newEndX, newEndY));
                }
            } else if (node instanceof javafx.scene.text.Text textNode) {
                double newX = textNode.getX();
                double newY = textNode.getY();
                if (origX != newX || origY != newY) {
                    applyUndoableStrategy(new Move(selectedShapeInstance, origX, origY, newX, newY));
                }
            } else if (node instanceof Polygon && selectedShapeInstance instanceof FreeFormPolygonShape polygonShape) {
                // CORREZIONE: Gestione corretta del movimento del poligono
                double newX = polygonShape.getX();
                double newY = polygonShape.getY();
                if (Math.abs(origX - newX) > 0.1 || Math.abs(origY - newY) > 0.1) {
                    System.out.println("[POLYGON MOVE] Da (" + origX + ", " + origY + ") a (" + newX + ", " + newY + ")");
                    
                    // Crea comando move personalizzato per poligono
                    UndoableCommand moveCommand = new UndoableCommand() {
                        private final double oldX = origX;
                        private final double oldY = origY;
                        private final double newX_final = newX;
                        private final double newY_final = newY;
                        
                        @Override
                        public void execute() {
                            polygonShape.moveTo(newX_final, newY_final);
                            System.out.println("[POLYGON MOVE] Spostato a (" + newX_final + ", " + newY_final + ")");
                        }
                        
                        @Override
                        public void undo() {
                            polygonShape.moveTo(oldX, oldY);
                            System.out.println("[POLYGON MOVE UNDO] Ripristinato a (" + oldX + ", " + oldY + ")");
                        }
                    };
                    
                    if (invoker != null) {
                        invoker.execute(moveCommand);
                    }
                }
            }

            return;
        }

        if (!toolActive) {
            return;
        }

        // Click senza trascinamento = annulla creazione
        if (!isDragging && tempShape != null) {
            drawingPane.getChildren().remove(tempShape.getNode());
            currentShapes.remove(AbstractShape.unwrapToAbstract(tempShape));
            tempShape = null;
            return;
        }

        // Rilascio dopo vero trascinamento: crea figura
        if (tempShape != null) {
            tempShape.onRelease();
            applyUndoableStrategy(new Create(new ShapeManager(currentShapes, drawingPane), tempShape));
            tempShape = null;
            toolActive = false;
        }

        isDragging = false;
    }

    public void setShapeToPaste(Shape shapeToPaste) {
        this.shapeToPaste = shapeToPaste;
    }

    /**
     * Gestisce l'incollaggio di una shape clonata in una posizione specifica.
     * Crea una copia completamente indipendente della shape nel clipboard.
     */
    private void handlePaste(double x, double y) {
        try {
            if (shapeToPaste != null) {
                Shape independentCopy = createDeepCopy(shapeToPaste);
                
                if (independentCopy != null) {
                    independentCopy.setX(x);
                    independentCopy.setY(y);
                    independentCopy.getNode().setUserData(independentCopy);
                    drawingPane.getChildren().add(independentCopy.getNode());
                    AbstractShape baseShape = AbstractShape.unwrapToAbstract(independentCopy);
                    currentShapes.add(baseShape);
                    setSelectedShapeInstance(independentCopy);

                    System.out.println("[PASTE] Figura incollata: " + independentCopy.getClass().getSimpleName() 
                                     + " @ (" + x + ", " + y + ")");
                } else {
                    System.err.println("[PASTE] Impossibile creare una copia della shape");
                }
            } else {
                System.out.println("[PASTE] Nessuna shape da incollare");
            }
            
        } catch (Exception e) {
            System.err.println("[PASTE ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crea una copia profonda e completamente indipendente di una shape.
     */
    private Shape createDeepCopy(Shape originalShape) {
        try {
            if (originalShape == null) {
                return null;
            }
            
            Shape clonedShape = originalShape.clone();
            
            if (clonedShape == null) {
                System.err.println("[DEEP COPY] Il metodo clone ha restituito null");
                return null;
            }
            
            if (clonedShape.getNode() == originalShape.getNode()) {
                System.err.println("[DEEP COPY] ATTENZIONE: La copia condivide il nodo con l'originale!");
            }
            
            System.out.println("[DEEP COPY] Copia creata: " + clonedShape.getClass().getSimpleName());
            
            return clonedShape;
            
        } catch (Exception e) {
            System.err.println("[DEEP COPY ERROR] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * METODO MODIFICATO - Deseleziona con supporto selezione multipla.
     */
    public void unselectShape() {
        if (multipleSelectionManager != null && multipleSelectionManager.isMultipleSelectionMode()) {
            multipleSelectionManager.clearSelection();
        } else {
            // Comportamento originale
            if (selectedShapeInstance != null) {
                javafx.scene.shape.Shape fx = (javafx.scene.shape.Shape) selectedShapeInstance.getNode();
                fx.setStrokeWidth(1);
                fx.setEffect(null);
                selectedShapeInstance = null;
            }
        }
    }

    public void applyUndoableStrategy(MouseMultiInputs command) {
        MultiMouseInputsStrategy strategy = new MultiMouseInputsCommandStackInvoker(command, invoker);
        MultiMouseInputsContext context = new MultiMouseInputsContext(strategy);
        context.onReleased(null);
    }

    public void setText(String text) {
        this.text = text;
    }
}