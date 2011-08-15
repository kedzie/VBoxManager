package com.kedzie.vbox.api;

import java.io.IOException;
import com.kedzie.vbox.KSOAP;
import com.kedzie.vbox.api.jaxb.SessionState;
import com.kedzie.vbox.api.jaxb.SessionType;

public interface ISession extends IRemoteObject {

	public void unlockMachine() throws IOException;;
	@KSOAP(cache=false) public IConsole getConsole() throws IOException;
	@KSOAP(cache=false) public SessionType getType() throws IOException;
	@KSOAP(cache=false) public SessionState getState() throws IOException;
}
