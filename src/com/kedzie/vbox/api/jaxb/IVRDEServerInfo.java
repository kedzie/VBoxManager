package com.kedzie.vbox.api.jaxb;

public class IVRDEServerInfo {

	protected boolean active;
	protected int port;
	protected long numberOfClients;
	protected long beginTime;
	protected long endTime;
	protected long bytesSent;
	protected long bytesSentTotal;
	protected long bytesReceived;
	protected long bytesReceivedTotal;

	protected String user;

	protected String domain;

	protected String clientName;

	protected String clientIP;
	protected long clientVersion;
	protected long encryptionStyle;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean value) {
		this.active = value;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int value) {
		this.port = value;
	}

	public long getNumberOfClients() {
		return numberOfClients;
	}

	public void setNumberOfClients(long value) {
		this.numberOfClients = value;
	}

	public long getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(long value) {
		this.beginTime = value;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long value) {
		this.endTime = value;
	}

	public long getBytesSent() {
		return bytesSent;
	}

	public void setBytesSent(long value) {
		this.bytesSent = value;
	}

	public long getBytesSentTotal() {
		return bytesSentTotal;
	}

	public void setBytesSentTotal(long value) {
		this.bytesSentTotal = value;
	}

	public long getBytesReceived() {
		return bytesReceived;
	}

	public void setBytesReceived(long value) {
		this.bytesReceived = value;
	}

	public long getBytesReceivedTotal() {
		return bytesReceivedTotal;
	}

	public void setBytesReceivedTotal(long value) {
		this.bytesReceivedTotal = value;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String value) {
		this.user = value;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String value) {
		this.domain = value;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String value) {
		this.clientName = value;
	}

	public String getClientIP() {
		return clientIP;
	}

	public void setClientIP(String value) {
		this.clientIP = value;
	}

	public long getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(long value) {
		this.clientVersion = value;
	}

	public long getEncryptionStyle() {
		return encryptionStyle;
	}

	public void setEncryptionStyle(long value) {
		this.encryptionStyle = value;
	}

}
