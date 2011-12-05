package com.kedzie.vbox.api;

import com.kedzie.vbox.Cacheable;

public interface IGuest extends IManagedObjectRef {
	@Cacheable  public Integer getMemoryBalloonSize();
}
