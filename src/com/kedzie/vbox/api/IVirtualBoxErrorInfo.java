package com.kedzie.vbox.api;

import java.io.IOException;

import com.kedzie.vbox.soap.KSOAP;

public interface IVirtualBoxErrorInfo extends IManagedObjectRef {

	@KSOAP(cacheable=true) public Integer getResultCode() throws IOException;
	@KSOAP(cacheable=true) public String getText() throws IOException;
	
}
