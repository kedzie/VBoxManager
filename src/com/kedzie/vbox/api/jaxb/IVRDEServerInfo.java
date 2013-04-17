package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

import com.kedzie.vbox.soap.KSoapObject;

@KSoapObject("IVRDEServerInfo")
public class IVRDEServerInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected Boolean active;
    protected Integer port;
    protected Integer numberOfClients;
    protected Long beginTime;
    protected Long endTime;
    protected Long bytesSent;
    protected Long bytesSentTotal;
    protected Long bytesReceived;
    protected Long bytesReceivedTotal;
    protected String user;
    protected String domain;
    protected String clientName;
    protected String clientIP;
    protected Integer clientVersion;
    protected Integer encryptionStyle;
    /**
     * Gets the value of the active property.
     * 
     */
    public Boolean isActive() {
        return active;
    }
    /**
     * Sets the value of the active property.
     * 
     */
    public void setActive(Boolean value) {
        this.active = value;
    }
    /**
     * Gets the value of the port property.
     * 
     */
    public Integer getPort() {
        return port;
    }
    /**
     * Sets the value of the port property.
     * 
     */
    public void setPort(Integer value) {
        this.port = value;
    }
    /**
     * Gets the value of the numberOfClients property.
     * 
     */
    public Integer getNumberOfClients() {
        return numberOfClients;
    }
    /**
     * Sets the value of the numberOfClients property.
     * 
     */
    public void setNumberOfClients(Integer value) {
        this.numberOfClients = value;
    }
    /**
     * Gets the value of the begIntegerime property.
     * 
     */
    public Long getBeginTime() {
        return beginTime;
    }
    /**
     * Sets the value of the begIntegerime property.
     * 
     */
    public void setBeginTime(Long value) {
        this.beginTime = value;
    }
    /**
     * Gets the value of the endTime property.
     * 
     */
    public Long getEndTime() {
        return endTime;
    }
    /**
     * Sets the value of the endTime property.
     * 
     */
    public void setEndTime(Long value) {
        this.endTime = value;
    }
    /**
     * Gets the value of the bytesSent property.
     * 
     */
    public Long getBytesSent() {
        return bytesSent;
    }
    /**
     * Sets the value of the bytesSent property.
     * 
     */
    public void setBytesSent(Long value) {
        this.bytesSent = value;
    }
    /**
     * Gets the value of the bytesSentTotal property.
     * 
     */
    public Long getBytesSentTotal() {
        return bytesSentTotal;
    }
    /**
     * Sets the value of the bytesSentTotal property.
     * 
     */
    public void setBytesSentTotal(Long value) {
        this.bytesSentTotal = value;
    }
    /**
     * Gets the value of the bytesReceived property.
     * 
     */
    public Long getBytesReceived() {
        return bytesReceived;
    }
    /**
     * Sets the value of the bytesReceived property.
     * 
     */
    public void setBytesReceived(Long value) {
        this.bytesReceived = value;
    }
    /**
     * Gets the value of the bytesReceivedTotal property.
     * 
     */
    public Long getBytesReceivedTotal() {
        return bytesReceivedTotal;
    }
    /**
     * Sets the value of the bytesReceivedTotal property.
     * 
     */
    public void setBytesReceivedTotal(Long value) {
        this.bytesReceivedTotal = value;
    }
    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }
    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }
    /**
     * Gets the value of the domain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomain() {
        return domain;
    }
    /**
     * Sets the value of the domain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomain(String value) {
        this.domain = value;
    }
    /**
     * Gets the value of the clientName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientName() {
        return clientName;
    }
    /**
     * Sets the value of the clientName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientName(String value) {
        this.clientName = value;
    }
    /**
     * Gets the value of the clientIP property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientIP() {
        return clientIP;
    }
    /**
     * Sets the value of the clientIP property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientIP(String value) {
        this.clientIP = value;
    }
    /**
     * Gets the value of the clientVersion property.
     * 
     */
    public Integer getClientVersion() {
        return clientVersion;
    }
    /**
     * Sets the value of the clientVersion property.
     * 
     */
    public void setClientVersion(Integer value) {
        this.clientVersion = value;
    }
    /**
     * Gets the value of the encryptionStyle property.
     * 
     */
    public Integer getEncryptionStyle() {
        return encryptionStyle;
    }
    /**
     * Sets the value of the encryptionStyle property.
     * 
     */
    public void setEncryptionStyle(Integer value) {
        this.encryptionStyle = value;
    }
}
