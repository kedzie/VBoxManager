package com.kedzie.vbox.api;

import java.io.IOException;

import com.kedzie.vbox.soap.KSOAP;

@KSOAP
public interface IEventListener extends IManagedObjectRef {

	public void handleEvent(@KSOAP("event") IEvent event) throws IOException;
	
}
