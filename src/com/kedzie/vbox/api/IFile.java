package com.kedzie.vbox.api;

import com.kedzie.vbox.api.jaxb.FileSeekType;
import com.kedzie.vbox.soap.KSOAP;

@KSOAP(prefix="IFile")
public interface IFile extends IManagedObjectRef {

	public Integer getCreationMode();

	public Integer getDisposition();

	public String getFileName();

	public Long getInitialSize();
    
	public Integer getOpenMode();

	public Long getOffset();

	public void close();

	public String getQueryInfo();

	public String read(@KSOAP(type="unsignedInt", value="toRead") int toRead, @KSOAP(type="unsignedInt", value="timeoutMS") int timeoutMS);

	public String readAt(@KSOAP(type="long", value="offset") long offset, @KSOAP(type="unsignedInt", value="toRead") int toRead, @KSOAP(type="unsignedInt", value="timeoutMS") int timeoutMS);

	public void seek(@KSOAP(type="unsignedInt", value="offset") long offset, FileSeekType whence);
    
	public void setACL(String acl);
	
	public Integer write(String data, @KSOAP(type="unsignedInt", value="timeoutMS") int timeoutMS);
    
	public Integer writeAt(@KSOAP(type="unsignedInt", value="offset") long offset, String data, @KSOAP(type="unsignedInt", value="timeoutMS") int timeoutMS);
}
