package it.unisa.progettosadgruppo19.command;

import javafx.scene.input.MouseEvent;

/**
 *
 * @author mainuser
 */
public interface MouseMultiInputs {

    void onPressed(MouseEvent event);

    void onReleased(MouseEvent event);

    void onDragged(MouseEvent event);

    void onMouseClick(MouseEvent event);

    boolean isExecutable();
}
