package com.kedzie.vbox.api;

import com.kedzie.vbox.api.jaxb.SessionState;
import com.kedzie.vbox.soap.KSOAP;

public interface ISessionStateChangedEvent extends IEvent {
	@KSOAP(cacheable=true) public SessionState getState();
}
