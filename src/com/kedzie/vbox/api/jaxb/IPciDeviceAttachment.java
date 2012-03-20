package com.kedzie.vbox.api.jaxb;

public class IPciDeviceAttachment {

	protected String name;
	protected boolean isPhysicalDevice;
	protected int hostAddress;
	protected int guestAddress;

	public String getName() {
		return name;
	}

	public void setName(String value) {
		this.name = value;
	}

	public boolean isIsPhysicalDevice() {
		return isPhysicalDevice;
	}

	public void setIsPhysicalDevice(boolean value) {
		this.isPhysicalDevice = value;
	}

	public int getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(int value) {
		this.hostAddress = value;
	}

	public int getGuestAddress() {
		return guestAddress;
	}

	public void setGuestAddress(int value) {
		this.guestAddress = value;
	}

}
