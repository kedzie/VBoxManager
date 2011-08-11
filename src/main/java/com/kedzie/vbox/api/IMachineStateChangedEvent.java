package com.kedzie.vbox.api;

import org.virtualbox_4_1.MachineState;

import com.kedzie.vbox.KSOAP;

public interface IMachineStateChangedEvent extends IMachineEvent {
	@KSOAP(cache=true, prefix="IMachineStateChangedEvent")
	public MachineState getState();
}
