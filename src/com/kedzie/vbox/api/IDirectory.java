package com.kedzie.vbox.api;

import com.kedzie.vbox.soap.KSOAP;

@KSOAP(prefix="IDirectory")
public interface IDirectory extends IManagedObjectRef {

	public String getDirectoryName();
	
	public String getFilter();
	
	public void close();

	public String read();
}