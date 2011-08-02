package com.kedzie.vbox.event;

import org.virtualbox_4_1.VBoxEventType;

import com.kedzie.vbox.api.IRemoteObject;
import com.kedzie.vbox.api.KSOAP;

public interface IEvent extends IRemoteObject {

	@KSOAP(prefix="IEvent")
	public VBoxEventType getType();
	@KSOAP(prefix="IEvent")
	public void setProcessed();
	@KSOAP(prefix="IEvent")
	public Boolean waitProcessed(@KSOAP(type="unsignedInt", value="timeout") int timeout);
}
