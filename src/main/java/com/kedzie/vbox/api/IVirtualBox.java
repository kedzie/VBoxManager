package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.List;

import com.kedzie.vbox.KSOAP;


public interface IVirtualBox extends IRemoteObject {
	
	@KSOAP(cache=true) public String getVersion();
	@KSOAP(cache=true) public IEventSource getEventSource() ;
	@KSOAP(cache=true) public IPerformanceCollector getPerformanceCollector();
	@KSOAP(cache=true) public IHost getHost() ;
	@KSOAP(cache=true)  ISystemProperties getSystemProperties();
	
	@KSOAP(prefix="IWebsessionManager", thisReference="")
	public IVirtualBox logon(@KSOAP("username") String username, @KSOAP("password") String password) throws IOException;
	@KSOAP(prefix="IWebsessionManager", thisReference="refIVirtualBox")
	public void logoff() throws IOException;
	@KSOAP(cache=true, prefix="IWebsessionManager", thisReference="refIVirtualBox")
	public ISession getSessionObject() throws IOException;
	
	public List<IMachine> getMachines() throws IOException;
	public IMachine findMachine(@KSOAP("nameOrId") String nameOrId) throws IOException;
}
