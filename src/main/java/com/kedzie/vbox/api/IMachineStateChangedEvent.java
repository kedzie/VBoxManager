package com.kedzie.vbox.api;

import com.kedzie.vbox.KSOAP;
import com.kedzie.vbox.api.jaxb.MachineState;

public interface IMachineStateChangedEvent extends IMachineEvent {
	@KSOAP(cache=true, prefix="IMachineStateChangedEvent")
	public MachineState getState();
}
