package com.kedzie.vbox.api;

import com.kedzie.vbox.api.jaxb.MachineState;
import com.kedzie.vbox.soap.Cacheable;
import com.kedzie.vbox.soap.KSOAP;

public interface IMachineStateChangedEvent extends IMachineEvent {
	@Cacheable @KSOAP(prefix="IMachineStateChangedEvent") 	public MachineState getState();
}
