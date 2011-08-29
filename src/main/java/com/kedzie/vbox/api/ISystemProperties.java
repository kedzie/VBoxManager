package com.kedzie.vbox.api;

import com.kedzie.vbox.KSOAP;

public interface ISystemProperties extends IRemoteObject {
	@KSOAP(cache=true) public Integer getMinGuestRAM();
	@KSOAP(cache=true) public Integer getMaxGuestRam();
	@KSOAP(cache=true) public Integer getMinGuestCPUCount();
	@KSOAP(cache=true) public Integer getMaxGuestCPUCount();
	@KSOAP(cache=true) public Integer getMinGuestMonitors();
	@KSOAP(cache=true) public Integer getMaxGuestMonitors();
	
}
