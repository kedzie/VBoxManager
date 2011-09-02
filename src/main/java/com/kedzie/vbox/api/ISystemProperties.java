package com.kedzie.vbox.api;

import com.kedzie.vbox.Cacheable;

public interface ISystemProperties extends IRemoteObject {
	@Cacheable public Integer getMinGuestRAM();
	@Cacheable public Integer getMaxGuestRam();
	@Cacheable public Integer getMinGuestCPUCount();
	@Cacheable public Integer getMaxGuestCPUCount();
	@Cacheable public Integer getMinGuestMonitors();
	@Cacheable public Integer getMaxGuestMonitors();
}
