package com.kedzie.vbox.api;

import java.io.IOException;

import org.virtualbox_4_0.SessionState;
import org.virtualbox_4_0.SessionType;

public interface ISession extends IRemoteObject {

	public void unlockMachine() throws IOException;;
	public IConsole getConsole() throws IOException;
	public SessionType getType() throws IOException;
	public SessionState getState() throws IOException;
}
