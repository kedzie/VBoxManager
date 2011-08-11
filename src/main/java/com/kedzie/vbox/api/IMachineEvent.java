package com.kedzie.vbox.api;

import com.kedzie.vbox.KSOAP;

public interface IMachineEvent extends IEvent {
	@KSOAP(cache=true, prefix="IMachineEvent")
	public String getMachineId();
}
