package com.kedzie.vbox.server;

public class Server {
	
	private Long id;
	private Integer port;
	private String host;
	private String username;
	private String password;

	public Server() {}
	public Server(Long id, String host, Integer port, String username, String password) {
		this.id=id;
		this.port = port;
		this.host = host;
		this.username = username;
		this.password=password;
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String toString() {
		return getHost() + ":" + getPort();
	}
}
