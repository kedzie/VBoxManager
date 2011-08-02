package com.kedzie.vbox.api;

public interface IHost extends IRemoteObject {
	public Integer getMemorySize();
	public Integer getMemoryAvailable();
	public Integer getProcessorCount();
	public Integer getProcessorCoreCount();
	public Integer getProcessorOnlineCount();
	public Integer getProcessorSpeed();
	
}
