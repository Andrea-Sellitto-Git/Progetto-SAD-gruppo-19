package it.unisa.progettosadgruppo19.strategy;

import it.unisa.progettosadgruppo19.command.MouseMultiInputs;
import it.unisa.progettosadgruppo19.command.StackUndoInvoker;
import it.unisa.progettosadgruppo19.command.UndoableCommand;

/**
 *
 * @author mainuser
 */
public class MultiMouseInputsCommandStackInvoker implements MultiMouseInputsStrategy {

    private MouseMultiInputs command;
    private StackUndoInvoker invoker;

    public MultiMouseInputsCommandStackInvoker(MouseMultiInputs command, StackUndoInvoker invoker) {
        this.command = command;
        this.invoker = invoker;
    }

    @Override
    public MouseMultiInputs getInputs() {
        return command;
    }

    @Override
    public void execute() {
        invoker.execute((UndoableCommand) command);
    }

}
