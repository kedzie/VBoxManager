package com.kedzie.vbox.api;

import com.kedzie.vbox.api.jaxb.MachineState;
import com.kedzie.vbox.soap.KSOAP;

public interface IMachineStateChangedEvent extends IMachineEvent {
	@KSOAP(cacheable=true, prefix="IMachineStateChangedEvent") 	public MachineState getState();
}
