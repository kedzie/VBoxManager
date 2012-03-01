package com.kedzie.vbox.api;

import com.kedzie.vbox.api.jaxb.SessionState;
import com.kedzie.vbox.soap.Cacheable;

public interface ISessionStateChangedEvent extends IEvent {
	@Cacheable public SessionState getState();
}
