package com.kedzie.vbox.api;

import com.kedzie.vbox.api.jaxb.SessionState;

public interface ISessionStateChangedEvent extends IEvent {
	public SessionState getState();
}
