package com.kedzie.vbox.api.jaxb;

public class IPciAddress {

	protected short bus;
	protected short device;
	protected short devFunction;

	public short getBus() {
		return bus;
	}

	public void setBus(short value) {
		this.bus = value;
	}

	public short getDevice() {
		return device;
	}

	public void setDevice(short value) {
		this.device = value;
	}

	public short getDevFunction() {
		return devFunction;
	}

	public void setDevFunction(short value) {
		this.devFunction = value;
	}

}
