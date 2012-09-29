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
    protected boolean discard;
    protected String bandwidthGroup;
    /**
     * Gets the value of the medium property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMedium() {
        return medium;
    }
    /**
     * Sets the value of the medium property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMedium(String value) {
        this.medium = value;
    }
    /**
     * Gets the value of the controller property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getController() {
        return controller;
    }
    /**
     * Sets the value of the controller property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setController(String value) {
        this.controller = value;
    }
    /**
     * Gets the value of the port property.
     * 
     */
    public int getPort() {
        return port;
    }
    /**
     * Sets the value of the port property.
     * 
     */
    public void setPort(int value) {
        this.port = value;
    }
    /**
     * Gets the value of the device property.
     * 
     */
    public int getDevice() {
        return device;
    }
    /**
     * Sets the value of the device property.
     * 
     */
    public void setDevice(int value) {
        this.device = value;
    }
    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link DeviceType }
     *     
     */
    public DeviceType getType() {
        return type;
    }
    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeviceType }
     *     
     */
    public void setType(DeviceType value) {
        this.type = value;
    }
    /**
     * Gets the value of the passthrough property.
     * 
     */
    public boolean isPassthrough() {
        return passthrough;
    }
    /**
     * Sets the value of the passthrough property.
     * 
     */
    public void setPassthrough(boolean value) {
        this.passthrough = value;
    }
    /**
     * Gets the value of the temporaryEject property.
     * 
     */
    public boolean isTemporaryEject() {
        return temporaryEject;
    }
    /**
     * Sets the value of the temporaryEject property.
     * 
     */
    public void setTemporaryEject(boolean value) {
        this.temporaryEject = value;
    }
    /**
     * Gets the value of the isEjected property.
     * 
     */
    public boolean isIsEjected() {
        return isEjected;
    }
    /**
     * Sets the value of the isEjected property.
     * 
     */
    public void setIsEjected(boolean value) {
        this.isEjected = value;
    }
    /**
     * Gets the value of the nonRotational property.
     * 
     */
    public boolean isNonRotational() {
        return nonRotational;
    }
    /**
     * Sets the value of the nonRotational property.
     * 
     */
    public void setNonRotational(boolean value) {
        this.nonRotational = value;
    }
    /**
     * Gets the value of the discard property.
     * 
     */
    public boolean isDiscard() {
        return discard;
    }
    /**
     * Sets the value of the discard property.
     * 
     */
    public void setDiscard(boolean value) {
        this.discard = value;
    }
    /**
     * Gets the value of the bandwidthGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBandwidthGroup() {
        return bandwidthGroup;
    }
    /**
     * Sets the value of the bandwidthGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBandwidthGroup(String value) {
        this.bandwidthGroup = value;
    }
}
