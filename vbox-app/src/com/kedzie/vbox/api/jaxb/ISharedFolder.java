package com.kedzie.vbox.api.jaxb;

import com.kedzie.vbox.soap.KSoapObject;

import java.io.Serializable;


@KSoapObject("ISharedFolder")
public class ISharedFolder
		implements Serializable {

	private final static long serialVersionUID = 1L;
	protected String name;
	protected String hostPath;
	protected boolean accessible;
	protected boolean writable;
	protected boolean autoMount;
	protected String lastAccessError;

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
	 * Gets the value of the hostPath property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getHostPath() {
		return hostPath;
	}

	/**
	 * Sets the value of the hostPath property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setHostPath(String value) {
		this.hostPath = value;
	}

	/**
	 * Gets the value of the accessible property.
	 */
	public boolean isAccessible() {
		return accessible;
	}

	/**
	 * Sets the value of the accessible property.
	 */
	public void setAccessible(boolean value) {
		this.accessible = value;
	}

	/**
	 * Gets the value of the writable property.
	 */
	public boolean isWritable() {
		return writable;
	}

	/**
	 * Sets the value of the writable property.
	 */
	public void setWritable(boolean value) {
		this.writable = value;
	}

	/**
	 * Gets the value of the autoMount property.
	 */
	public boolean isAutoMount() {
		return autoMount;
	}

	/**
	 * Sets the value of the autoMount property.
	 */
	public void setAutoMount(boolean value) {
		this.autoMount = value;
	}

	/**
	 * Gets the value of the lastAccessError property.
	 *
	 * @return possible object is
	 * {@link String }
	 */
	public String getLastAccessError() {
		return lastAccessError;
	}

	/**
	 * Sets the value of the lastAccessError property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setLastAccessError(String value) {
		this.lastAccessError = value;
	}

}
