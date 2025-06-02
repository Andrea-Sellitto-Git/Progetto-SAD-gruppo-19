package it.unisa.progettosadgruppo19.command;

import java.util.Stack;

/**
 * Invoker per comandi undoable che utilizza uno stack per memorizzare i comandi
 * eseguiti e supportare l'operazione di undo.
 */
public class StackUndoInvoker {

    /**
     * Stack dei comandi eseguiti che supportano l'undo.
     */
    private Stack<UndoableCommand> stack = new Stack<>();

    /**
     * Esegue il comando specificato: lo invoca e, se implementa
     * {@link UndoableCommand}, lo aggiunge allo stack.
     *
     * @param command il comando da eseguire; non può essere {@code null}
     */
    public void execute(Command command) {
        command.execute();
        if (command instanceof UndoableCommand uc) {
            stack.push(uc);
            System.out.println("[STACK] Comando aggiunto: " + uc.getClass().getSimpleName() + " (size=" + stack.size() + ")");
        } else {
            System.out.println("[STACK] Comando NON undoable: " + command.getClass().getSimpleName());
        }
    }

    /**
     * Annulla l'ultimo comando eseguito, se presente.
     */
    public void undo() {
        if (!stack.isEmpty()) {
            UndoableCommand cmd = stack.pop();
            System.out.println("[UNDO] Eseguo undo: " + cmd.getClass().getSimpleName() + " (rimasti=" + stack.size() + ")");
            cmd.undo();
        } else {
            System.out.println("[UNDO] Stack vuoto");
        }
    }
}
