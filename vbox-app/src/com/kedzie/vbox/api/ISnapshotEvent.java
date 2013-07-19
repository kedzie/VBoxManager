package com.kedzie.vbox.api;

import com.kedzie.vbox.soap.KSOAP;

@KSOAP
public interface ISnapshotEvent extends IEvent {
	@KSOAP(cacheable=true, prefix="ISnapshotEvent") public String getSnapshotId();
}
