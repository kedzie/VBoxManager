package com.kedzie.vbox.event;

import java.io.IOException;

public interface VBoxEventListener {

	public void handleEvent(IEvent event) throws IOException;
	
}
