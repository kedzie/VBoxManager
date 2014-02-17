package com.kedzie.vbox.api.jaxb;

import com.kedzie.vbox.soap.KSoapObject;

import java.io.Serializable;


@KSoapObject("IPCIDeviceAttachment")
public class IPCIDeviceAttachment
		implements Serializable {

	private final static long serialVersionUID = 1L;
	protected String name;
	protected boolean isPhysicalDevice;
	protected int hostAddress;
	protected int guestAddress;

	/**
	 * Gets the value of the name property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of the name property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setName(String value) {
		this.name = value;
	}

	/**
	 * Gets the value of the isPhysicalDevice property.
	 */
	public boolean isIsPhysicalDevice() {
		return isPhysicalDevice;
	}

	/**
	 * Sets the value of the isPhysicalDevice property.
	 */
	public void setIsPhysicalDevice(boolean value) {
		this.isPhysicalDevice = value;
	}

	/**
	 * Gets the value of the hostAddress property.
	 */
	public int getHostAddress() {
		return hostAddress;
	}

	/**
	 * Sets the value of the hostAddress property.
	 */
	public void setHostAddress(int value) {
		this.hostAddress = value;
	}

	/**
	 * Gets the value of the guestAddress property.
	 */
	public int getGuestAddress() {
		return guestAddress;
	}

	/**
	 * Sets the value of the guestAddress property.
	 */
	public void setGuestAddress(int value) {
		this.guestAddress = value;
	}

}
