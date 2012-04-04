package com.kedzie.vbox.api;

import com.kedzie.vbox.soap.Cacheable;

public interface IGuest extends IManagedObjectRef {
	@Cacheable  public Integer getMemoryBalloonSize();
}
