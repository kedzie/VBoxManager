package com.kedzie.vbox.api;

import com.kedzie.vbox.Cacheable;
import com.kedzie.vbox.KSOAP;
import com.kedzie.vbox.api.jaxb.MachineState;

public interface IMachineStateChangedEvent extends IMachineEvent {
	@Cacheable @KSOAP(prefix="IMachineStateChangedEvent") 	public MachineState getState();
}
