package com.kedzie.vbox.api;

import org.virtualbox_4_0.MachineState;

public interface IMachineStateChangedEvent extends IMachineEvent {

	public IMachine getMachine();
	public MachineState getState();
	
}
