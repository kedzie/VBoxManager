package com.kedzie.vbox.api;

import com.kedzie.vbox.soap.KSOAP;

@KSOAP(cacheable=true) 
public interface ISystemProperties extends IManagedObjectRef {
	public Integer getMinGuestRAM();
	public Integer getMaxGuestRam();
	public Integer getMinGuestCPUCount();
	public Integer getMaxGuestCPUCount();
	public Integer getMinGuestMonitors();
	public Integer getMaxGuestMonitors();
}
