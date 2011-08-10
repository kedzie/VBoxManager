package com.kedzie.vbox.api;

import com.kedzie.vbox.KSOAP;

public interface IMachineEvent extends IEvent {
	@KSOAP(prefix="IMachineEvent")
	public String getMachineId();
}
