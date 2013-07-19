package com.kedzie.vbox.api.jaxb;

public class IAdditionsFacility {
    protected AdditionsFacilityClass classType;
    protected long lastUpdated;
    protected String name;
    protected AdditionsFacilityStatus status;
    protected AdditionsFacilityType type;
    /**
     * Gets the value of the classType property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionsFacilityClass }
     *     
     */
    public AdditionsFacilityClass getClassType() {
        return classType;
    }
    /**
     * Sets the value of the classType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionsFacilityClass }
     *     
     */
    public void setClassType(AdditionsFacilityClass value) {
        this.classType = value;
    }
    /**
     * Gets the value of the lastUpdated property.
     * 
     */
    public long getLastUpdated() {
        return lastUpdated;
    }
    /**
     * Sets the value of the lastUpdated property.
     * 
     */
    public void setLastUpdated(long value) {
        this.lastUpdated = value;
    }
    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }
    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }
    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionsFacilityStatus }
     *     
     */
    public AdditionsFacilityStatus getStatus() {
        return status;
    }
    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionsFacilityStatus }
     *     
     */
    public void setStatus(AdditionsFacilityStatus value) {
        this.status = value;
    }
    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionsFacilityType }
     *     
     */
    public AdditionsFacilityType getType() {
        return type;
    }
    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionsFacilityType }
     *     
     */
    public void setType(AdditionsFacilityType value) {
        this.type = value;
    }
}
