package com.kedzie.vbox.api;

import org.virtualbox_4_1.VBoxEventType;

import com.kedzie.vbox.KSOAP;

public interface IEvent extends IRemoteObject {

	@KSOAP(cache=true, prefix="IEvent") public VBoxEventType getType();
	@KSOAP(prefix="IEvent") public void setProcessed();
	@KSOAP(prefix="IEvent")	public Boolean waitProcessed(@KSOAP(type="unsignedInt", value="timeout") int timeout);
}
