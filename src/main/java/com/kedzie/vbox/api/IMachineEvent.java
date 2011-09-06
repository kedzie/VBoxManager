package com.kedzie.vbox.api;

import com.kedzie.vbox.Cacheable;
import com.kedzie.vbox.KSOAP;

public interface IMachineEvent extends IEvent {
	@Cacheable @KSOAP(prefix="IMachineEvent")	public String getMachineId();
}
