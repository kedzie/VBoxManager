package com.kedzie.vbox.api;

import java.io.IOException;

public interface IEventListener extends IRemoteObject {

	public void handleEvent(@KSOAP("event") IEvent event) throws IOException;
	
}
