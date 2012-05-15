package com.kedzie.vbox.api;

import java.util.List;

import com.kedzie.vbox.soap.KSOAP;

public interface ISnapshot extends IManagedObjectRef {

	@KSOAP(cacheable=true) public String getName();
	@KSOAP(cacheable=true) public String getDescription();
	@KSOAP(cacheable=true) public Long getTimestamp();
	@KSOAP(cacheable=true) public Boolean getOnline();
	@KSOAP(cacheable=true) public ISnapshot getParent();
	@KSOAP(cacheable=true) public List<ISnapshot> getChildren();
	@KSOAP(cacheable=true) public IMachine getMachine();
}
