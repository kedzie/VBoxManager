package com.kedzie.vbox.api;

import java.util.List;
import com.kedzie.vbox.Cacheable;

public interface ISnapshot extends IRemoteObject {

	@Cacheable public String getName();
	@Cacheable public String getDescription();
	@Cacheable public Long getTimestamp();
	@Cacheable public Boolean getOnline();
	@Cacheable public ISnapshot getParent();
	@Cacheable public List<ISnapshot> getChildren();
}
