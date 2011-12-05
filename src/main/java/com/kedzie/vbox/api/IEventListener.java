package com.kedzie.vbox.api;

import java.io.IOException;

import com.kedzie.vbox.KSOAP;

public interface IEventListener extends IManagedObjectRef {

	public void handleEvent(@KSOAP("event") IEvent event) throws IOException;
	
}
