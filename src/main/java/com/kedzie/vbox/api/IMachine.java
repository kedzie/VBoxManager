package com.kedzie.vbox.api;

public interface IMachine extends IRemoteObject {

	public String getName();
	public String getState();
	public String getDescription();
	public String getOSTypeId();
	
}
