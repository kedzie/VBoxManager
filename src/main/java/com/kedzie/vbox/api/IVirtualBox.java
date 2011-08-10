package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.List;

import com.kedzie.vbox.KSOAP;


public interface IVirtualBox extends IRemoteObject {

	public List<IMachine> getMachines() throws IOException;
	public IMachine findMachine(@KSOAP("nameOrId") String nameOrId) throws IOException;
	public String getVersion() throws IOException;
	public IEventSource getEventSource() throws IOException;
	public IPerformanceCollector getPerformanceCollector() throws IOException;
	public IHost getHost() ;
	@KSOAP(prefix="IWebsessionManager", thisReference="refIVirtualBox")
	public void logoff() throws IOException;
	@KSOAP(prefix="IWebsessionManager", thisReference="refIVirtualBox")
	public ISession getSessionObject() throws IOException;
	
}
