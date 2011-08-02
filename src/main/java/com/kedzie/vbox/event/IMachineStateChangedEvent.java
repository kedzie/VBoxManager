package com.kedzie.vbox.event;

import org.virtualbox_4_1.MachineState;

import com.kedzie.vbox.api.KSOAP;

public interface IMachineStateChangedEvent extends IMachineEvent {
	@KSOAP(prefix="IMachineStateChangedEvent")
	public MachineState getState();
}
