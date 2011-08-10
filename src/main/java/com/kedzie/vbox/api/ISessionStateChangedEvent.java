package com.kedzie.vbox.api;

import org.virtualbox_4_1.SessionState;

public interface ISessionStateChangedEvent extends IEvent {
	public SessionState getState();
}
