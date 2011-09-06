package com.kedzie.vbox.api;

import com.kedzie.vbox.Cacheable;

public interface IGuest extends IRemoteObject {
	@Cacheable  public Integer getMemoryBalloonSize();
}
