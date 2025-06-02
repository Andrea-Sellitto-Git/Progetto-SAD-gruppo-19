package it.unisa.progettosadgruppo19.strategy;

import it.unisa.progettosadgruppo19.command.Command;
import it.unisa.progettosadgruppo19.command.MouseMultiInputs;

/**
 *
 * @author mainuser
 */
public class MultiMouseInputsCommandBase implements MultiMouseInputsStrategy {

    private final MouseMultiInputs command;

    public MultiMouseInputsCommandBase(MouseMultiInputs command) {
        this.command = command;
    }

    @Override
    public MouseMultiInputs getInputs() {
        return command;
    }

    @Override
    public void execute() {
        ((Command) command).execute();
    }

}
