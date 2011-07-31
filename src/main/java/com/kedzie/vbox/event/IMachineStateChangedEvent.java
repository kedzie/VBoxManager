package com.kedzie.vbox.event;

import org.virtualbox_4_1.MachineState;

import com.kedzie.vbox.api.IMachine;

public interface IMachineStateChangedEvent extends IMachineEvent {

	public IMachine getMachine();
	public MachineState getState();
	
}
