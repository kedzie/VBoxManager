package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.List;

public interface IVirtualBox extends IRemoteObject {

	public List<IMachine> getMachines() throws IOException;
	public String getVersion() throws IOException;
	public IEventSource getEventSource() throws IOException;
}
