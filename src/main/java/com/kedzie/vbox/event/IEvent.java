package com.kedzie.vbox.event;

import org.virtualbox_4_0.VBoxEventType;

import com.kedzie.vbox.api.IRemoteObject;
import com.kedzie.vbox.api.KSOAP;

public interface IEvent extends IRemoteObject {

	public VBoxEventType getType();
	public void setProcessed();
	public Boolean waitProcessed(@KSOAP("timeout") int timeout);
}
