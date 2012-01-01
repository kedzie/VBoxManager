package com.kedzie.vbox.api;

import com.kedzie.vbox.Cacheable;

public interface IHost extends IManagedObjectRef {
	@Cacheable public Integer getMemorySize();
	@Cacheable public Integer getMemoryAvailable();
	@Cacheable public Integer getProcessorCount();
	@Cacheable public Integer getProcessorCoreCount();
	@Cacheable public Integer getProcessorOnlineCount();
	@Cacheable public Integer getProcessorSpeed();
}
