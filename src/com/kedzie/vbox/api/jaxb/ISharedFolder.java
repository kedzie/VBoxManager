package com.kedzie.vbox.api.jaxb;

public class ISharedFolder {

	protected String name;

	protected String hostPath;
	protected boolean accessible;
	protected boolean writable;
	protected boolean autoMount;

	protected String lastAccessError;

	public String getName() {
		return name;
	}

	public void setName(String value) {
		this.name = value;
	}

	public String getHostPath() {
		return hostPath;
	}

	public void setHostPath(String value) {
		this.hostPath = value;
	}

	public boolean isAccessible() {
		return accessible;
	}

	public void setAccessible(boolean value) {
		this.accessible = value;
	}

	public boolean isWritable() {
		return writable;
	}

	public void setWritable(boolean value) {
		this.writable = value;
	}

	public boolean isAutoMount() {
		return autoMount;
	}

	public void setAutoMount(boolean value) {
		this.autoMount = value;
	}

	public String getLastAccessError() {
		return lastAccessError;
	}

	public void setLastAccessError(String value) {
		this.lastAccessError = value;
	}

}
