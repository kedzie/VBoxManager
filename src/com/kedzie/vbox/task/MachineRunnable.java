package com.kedzie.vbox.task;

import com.kedzie.vbox.api.IMachine;

/**
 * Runnable with {@link IMachine} reference
 */
public abstract class MachineRunnable implements Runnable {
     
    protected IMachine m;

    public MachineRunnable(IMachine machine) {
        m=machine;
    }

    protected IMachine getMachine() {
        return m;
    }
}
