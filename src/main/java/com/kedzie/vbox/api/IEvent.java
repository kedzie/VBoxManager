package com.kedzie.vbox.api;

import org.virtualbox_4_0.VBoxEventType;

public interface IEvent extends IRemoteObject {

	public VBoxEventType getType();
	public void setProcessed();
	public Boolean waitProcessed(@KSOAP("timeout") int timeout);
}
