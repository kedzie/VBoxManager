package com.kedzie.vbox.server;

public class Server {
	
	private Long id;
	private Integer port;
	private String host;

	public Server() {}
	public Server(Long id, String host, Integer port) {
		this.id=id;
		this.port = port;
		this.host = host;
	}
	public Server(String host, Integer port) {
		this(null, host, port);
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String toString() {
		return getHost() + ":" + getPort();
	}
}
