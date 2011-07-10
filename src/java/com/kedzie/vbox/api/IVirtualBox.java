package com.kedzie.vbox.api;

import java.util.List;

import org.ksoap2.serialization.SoapPrimitive;

public interface IVirtualBox extends IRemoteObject {

	public List<IMachine> getMachines();
	
	public String getVersion();
	
	public SoapPrimitive getGuestOSType();
	
}
