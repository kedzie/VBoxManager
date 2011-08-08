package com.kedzie.vbox.api;

import java.io.IOException;

import org.virtualbox_4_1.VBoxEventType;

import com.kedzie.vbox.KSOAP;

public interface IEventSource extends IRemoteObject {
	
	public IEventListener createListener() throws IOException;
	public void registerListener(@KSOAP("listener")IEventListener l, @KSOAP("interesting") VBoxEventType []events, @KSOAP("active") boolean active) throws IOException;
	public void unregisterListener(@KSOAP("listener") IEventListener l) throws IOException;
	public void eventProcessed(@KSOAP("listener")IEventListener l, @KSOAP("event")IEvent event) throws IOException;
	public IEvent getEvent(@KSOAP("listener")IEventListener l, @KSOAP(type="int", value="timeout") int timeout) throws IOException;
	
//	public IEventAggregator createAggregator(String[] subs) throws IOException;
}
