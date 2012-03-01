package com.kedzie.vbox.api;

import com.kedzie.vbox.soap.Cacheable;
import com.kedzie.vbox.soap.KSOAP;

public interface IMachineEvent extends IEvent {
	@Cacheable @KSOAP(prefix="IMachineEvent")	public String getMachineId();
}
