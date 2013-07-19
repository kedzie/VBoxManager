package com.kedzie.vbox.api.jaxb;

public class IPCIAddress {
    protected short bus;
    protected short device;
    protected short devFunction;
    /**
     * Gets the value of the bus property.
     * 
     */
    public short getBus() {
        return bus;
    }
    /**
     * Sets the value of the bus property.
     * 
     */
    public void setBus(short value) {
        this.bus = value;
    }
    /**
     * Gets the value of the device property.
     * 
     */
    public short getDevice() {
        return device;
    }
    /**
     * Sets the value of the device property.
     * 
     */
    public void setDevice(short value) {
        this.device = value;
    }
    /**
     * Gets the value of the devFunction property.
     * 
     */
    public short getDevFunction() {
        return devFunction;
    }
    /**
     * Sets the value of the devFunction property.
     * 
     */
    public void setDevFunction(short value) {
        this.devFunction = value;
    }
}
