package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.List;
import com.kedzie.vbox.KSOAP;

public interface ISnapshot extends IRemoteObject {

	@KSOAP(cache=true) public String getName();
	public String getDescription();
	public Long getTimestamp();
	public Boolean getOnline();
	
	public ISnapshot getParent();
	public List<ISnapshot> getChildren() throws IOException;
}
