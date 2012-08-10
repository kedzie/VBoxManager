package com.kedzie.vbox.api;

import java.io.IOException;

import com.kedzie.vbox.soap.KSOAP;

@KSOAP(cacheable=true) 
public interface IVirtualBoxErrorInfo extends IManagedObjectRef {
	public Integer getResultCode() throws IOException;
	public String getText() throws IOException;
}
