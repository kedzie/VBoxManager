package com.kedzie.vbox.api;

import com.kedzie.vbox.KSOAP;

public interface IGuest extends IRemoteObject {
	@KSOAP(cache=true) public Integer getMemoryBalloonSize();
}
