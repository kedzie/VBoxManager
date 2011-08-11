package com.kedzie.vbox.api;

import java.io.IOException;
import org.virtualbox_4_1.SessionState;
import org.virtualbox_4_1.SessionType;
import com.kedzie.vbox.KSOAP;

public interface ISession extends IRemoteObject {

	public void unlockMachine() throws IOException;;
	@KSOAP(cache=false) public IConsole getConsole() throws IOException;
	@KSOAP(cache=false) public SessionType getType() throws IOException;
	@KSOAP(cache=false) public SessionState getState() throws IOException;
}
