package com.kedzie.vbox.api;

import com.kedzie.vbox.api.jaxb.FsObjType;
import com.kedzie.vbox.soap.KSOAP;

@KSOAP(prefix="IFsObjInfo")
public interface IFsObjInfo extends IManagedObjectRef {

	public Long getAccessTime();
	
	public Long getAllocatedSize();

	public Long getBirthTime();

	public Long getChangeTime();

	public Integer getDeviceNumber();

	public String getFileAttributes();

	public Integer getGenerationId();

	public Integer getGID();

	public String getGroupName();

	public Integer getHardLinks();

	public Long getModificationTime();

	public String getName();

	public Long getNodeId();

	public Integer getNodeIdDevice();

	public Long getObjectSize();

	public FsObjType getType();

	public Integer getUID();

	public Integer getUserFlags();

	public String getUserName();
}