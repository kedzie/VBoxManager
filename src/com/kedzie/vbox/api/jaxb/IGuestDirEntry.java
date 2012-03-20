package com.kedzie.vbox.api.jaxb;

public class IGuestDirEntry {

	protected long nodeId;

	protected String name;

	protected GuestDirEntryType type;

	public long getNodeId() {
		return nodeId;
	}

	public void setNodeId(long value) {
		this.nodeId = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String value) {
		this.name = value;
	}

	public GuestDirEntryType getType() {
		return type;
	}

	public void setType(GuestDirEntryType value) {
		this.type = value;
	}

}
