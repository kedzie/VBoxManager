package com.kedzie.vbox.api;

import java.util.List;

public interface IVirtualBox extends IRemoteObject {

	public List<IMachine> getMachines();
	
	public String getVersion();
	
}
