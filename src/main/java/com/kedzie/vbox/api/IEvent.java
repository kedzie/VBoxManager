package com.kedzie.vbox.api;

import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.soap.Cacheable;
import com.kedzie.vbox.soap.KSOAP;

public interface IEvent extends IManagedObjectRef {

	@Cacheable @KSOAP(prefix="IEvent") public VBoxEventType getType();
	@KSOAP(prefix="IEvent") public void setProcessed();
	@KSOAP(prefix="IEvent")	public Boolean waitProcessed(@KSOAP(type="unsignedInt", value="timeout") int timeout);
}
