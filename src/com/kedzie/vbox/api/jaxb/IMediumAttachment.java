package com.kedzie.vbox.api.jaxb;

public class IMediumAttachment {

	protected String medium;

	protected String controller;
	protected int port;
	protected int device;

	protected DeviceType type;
	protected boolean passthrough;
	protected boolean temporaryEject;
	protected boolean isEjected;
	protected boolean nonRotational;

	protected String bandwidthGroup;

	public String getMedium() {
		return medium;
	}

	public void setMedium(String value) {
		this.medium = value;
	}

	public String getController() {
		return controller;
	}

	public void setController(String value) {
		this.controller = value;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int value) {
		this.port = value;
	}

	public int getDevice() {
		return device;
	}

	public void setDevice(int value) {
		this.device = value;
	}

	public DeviceType getType() {
		return type;
	}

	public void setType(DeviceType value) {
		this.type = value;
	}

	public boolean isPassthrough() {
		return passthrough;
	}

	public void setPassthrough(boolean value) {
		this.passthrough = value;
	}

	public boolean isTemporaryEject() {
		return temporaryEject;
	}

	public void setTemporaryEject(boolean value) {
		this.temporaryEject = value;
	}

	public boolean isIsEjected() {
		return isEjected;
	}

	public void setIsEjected(boolean value) {
		this.isEjected = value;
	}

	public boolean isNonRotational() {
		return nonRotational;
	}

	public void setNonRotational(boolean value) {
		this.nonRotational = value;
	}

	public String getBandwidthGroup() {
		return bandwidthGroup;
	}

	public void setBandwidthGroup(String value) {
		this.bandwidthGroup = value;
	}

}
