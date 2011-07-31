package com.kedzie.vbox.event;

import java.io.IOException;

import com.kedzie.vbox.api.IRemoteObject;
import com.kedzie.vbox.api.KSOAP;

public interface IEventListener extends IRemoteObject {

	public void handleEvent(@KSOAP("event") IEvent event) throws IOException;
	
}
