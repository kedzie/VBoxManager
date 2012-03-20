package com.kedzie.vbox.api;

import com.kedzie.vbox.soap.Cacheable;

public interface ISystemProperties extends IManagedObjectRef {
	@Cacheable public Integer getMinGuestRAM();
	@Cacheable public Integer getMaxGuestRam();
	@Cacheable public Integer getMinGuestCPUCount();
	@Cacheable public Integer getMaxGuestCPUCount();
	@Cacheable public Integer getMinGuestMonitors();
	@Cacheable public Integer getMaxGuestMonitors();
}
