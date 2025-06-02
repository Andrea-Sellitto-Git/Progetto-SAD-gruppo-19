package it.unisa.progettosadgruppo19.strategy;

import javafx.scene.input.MouseEvent;

/**
 *
 * @author mainuser
 */
public class MultiMouseInputsContext {

    private boolean executed;
    private MultiMouseInputsStrategy strategy;

    public MultiMouseInputsContext(MultiMouseInputsStrategy strategy) {
        this.strategy = strategy;
        this.executed = false;
    }

    public void onPressed(MouseEvent event) {
        if (executed) {
            return;
        }
        strategy.getInputs().onPressed(event);
        manageExec();
    }

    public void onReleased(MouseEvent event) {
        if (executed) {
            return;
        }
        strategy.getInputs().onReleased(event);
        manageExec();
    }

    public void onDragged(MouseEvent event) {
        if (executed) {
            return;
        }
        strategy.getInputs().onDragged(event);
        manageExec();
    }

    public void onMouseClick(MouseEvent event) {
        if (executed) {
            return;
        }
        strategy.getInputs().onMouseClick(event);
        manageExec();
    }

    public boolean isExecuted() {
        return executed;
    }

    private void manageExec() {
        if (strategy.getInputs().isExecutable()) {
            strategy.execute();
            executed = true;
        }
    }
}
