package com.kedzie.vbox.api;

import com.kedzie.vbox.soap.KSOAP;

public interface ISystemProperties extends IManagedObjectRef {
	@KSOAP(cacheable=true) public Integer getMinGuestRAM();
	@KSOAP(cacheable=true) public Integer getMaxGuestRam();
	@KSOAP(cacheable=true) public Integer getMinGuestCPUCount();
	@KSOAP(cacheable=true) public Integer getMaxGuestCPUCount();
	@KSOAP(cacheable=true) public Integer getMinGuestMonitors();
	@KSOAP(cacheable=true) public Integer getMaxGuestMonitors();
}
