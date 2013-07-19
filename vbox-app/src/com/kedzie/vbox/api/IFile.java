package com.kedzie.vbox.api;

import com.kedzie.vbox.api.jaxb.FileSeekType;
import com.kedzie.vbox.soap.KSOAP;

@KSOAP(prefix="IFile")
public interface IFile extends IManagedObjectRef {

	@KSOAP(cacheable=true) public Integer getCreationMode();

	@KSOAP(cacheable=true) public Integer getDisposition();

	@KSOAP(cacheable=true) public String getFileName();

	@KSOAP(cacheable=true) public Long getInitialSize();
    
	@KSOAP(cacheable=true) public Integer getOpenMode();

	@KSOAP(cacheable=true) public Long getOffset();

	public void close();

	@KSOAP(cacheable=true) public IFsObjInfo getQueryInfo();

	public String read(@KSOAP(type="unsignedInt", value="toRead") int toRead, @KSOAP(type="unsignedInt", value="timeoutMS") int timeoutMS);

	public String readAt(@KSOAP(type="long", value="offset") long offset, @KSOAP(type="unsignedInt", value="toRead") int toRead, @KSOAP(type="unsignedInt", value="timeoutMS") int timeoutMS);

	public void seek(@KSOAP(type="unsignedInt", value="offset") long offset, FileSeekType whence);
    
	public void setACL(String acl);
	
	public Integer write(String data, @KSOAP(type="unsignedInt", value="timeoutMS") int timeoutMS);
    
	public Integer writeAt(@KSOAP(type="unsignedInt", value="offset") long offset, String data, @KSOAP(type="unsignedInt", value="timeoutMS") int timeoutMS);
}
