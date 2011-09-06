package com.kedzie.vbox.api;

import java.io.IOException;
import com.kedzie.vbox.KSOAP;
import com.kedzie.vbox.api.jaxb.SessionState;
import com.kedzie.vbox.api.jaxb.SessionType;

public interface ISession extends IRemoteObject {

	public void unlockMachine() throws IOException;;
	public IConsole getConsole() throws IOException;
	public SessionType getType() throws IOException;
	public SessionState getState() throws IOException;
}
