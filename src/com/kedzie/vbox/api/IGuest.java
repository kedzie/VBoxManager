package com.kedzie.vbox.api;

import com.kedzie.vbox.soap.KSOAP;

public interface IGuest extends IManagedObjectRef {
	@KSOAP(cacheable=true)  public Integer getMemoryBalloonSize();
}
