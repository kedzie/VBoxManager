package com.kedzie.vbox.task;

import java.util.concurrent.Callable;

import com.kedzie.vbox.api.IMachine;

/**
 * Callable with {@link IMachine} reference
 */
public abstract class MachineCallable<T> implements Callable<T> {

    protected IMachine m;
    
    public MachineCallable(IMachine machine) {
        m=machine;
    }
    
    protected IMachine getMachine() {
        return m;
    }
}
