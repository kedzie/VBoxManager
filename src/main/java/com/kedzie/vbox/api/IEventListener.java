package com.kedzie.vbox.api;

import java.io.IOException;

import com.kedzie.vbox.event.IEvent;

public interface IEventListener extends IRemoteObject {

	public void handleEvent(@KSOAP("event") IEvent event) throws IOException;
	
}
