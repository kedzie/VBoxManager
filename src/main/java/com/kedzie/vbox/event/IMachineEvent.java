package com.kedzie.vbox.event;

import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.KSOAP;

public interface IMachineEvent extends IEvent {
	@KSOAP(prefix="IMachineEvent")
	public IMachine getMachine();
}
