package com.kedzie.vbox.api;

import com.kedzie.vbox.soap.KSOAP;

public interface IMachineEvent extends IEvent {
	@KSOAP(cacheable=true, prefix="IMachineEvent")	public String getMachineId();
}
