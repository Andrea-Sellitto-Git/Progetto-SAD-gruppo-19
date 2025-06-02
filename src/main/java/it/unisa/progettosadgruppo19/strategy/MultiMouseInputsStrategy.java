package it.unisa.progettosadgruppo19.strategy;

import it.unisa.progettosadgruppo19.command.MouseMultiInputs;

/**
 *
 * @author mainuser
 */
public interface MultiMouseInputsStrategy {

    MouseMultiInputs getInputs();

    void execute();
}
