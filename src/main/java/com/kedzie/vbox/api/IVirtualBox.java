package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.List;

import com.kedzie.vbox.soap.Cacheable;
import com.kedzie.vbox.soap.KSOAP;


public interface IVirtualBox extends IManagedObjectRef {
	
	@Cacheable public String getVersion();
	@Cacheable public IEventSource getEventSource() ;
	@Cacheable public IPerformanceCollector getPerformanceCollector();
	@Cacheable public IHost getHost() ;
	@Cacheable  ISystemProperties getSystemProperties();
	
	@KSOAP(prefix="IWebsessionManager", thisReference="")
	public IVirtualBox logon(@KSOAP("username") String username, @KSOAP("password") String password) throws IOException;
	@KSOAP(prefix="IWebsessionManager", thisReference="refIVirtualBox")
	public void logoff() throws IOException;
	@Cacheable @KSOAP(prefix="IWebsessionManager", thisReference="refIVirtualBox")
	public ISession getSessionObject() throws IOException;
	
	public List<IMachine> getMachines() throws IOException;
	public IMachine findMachine(@KSOAP("nameOrId") String nameOrId) throws IOException;
}
