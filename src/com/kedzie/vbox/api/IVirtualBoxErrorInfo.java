package com.kedzie.vbox.api;

import java.io.IOException;

import com.kedzie.vbox.soap.Cacheable;

public interface IVirtualBoxErrorInfo extends IManagedObjectRef {

	@Cacheable public Integer getResultCode() throws IOException;
	@Cacheable public String getText() throws IOException;
	
}
