package com.kedzie.vbox.server;

import com.kedzie.vbox.api.WebSessionManager;

import android.os.Parcel;
import android.os.Parcelable;

public class Server implements Parcelable {
	
	private Long id=-1L;
	private Integer port=18083;
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
	public Server(Parcel p) {
		id = p.readLong();
		port = p.readInt();
		host = p.readString(); 
		username = p.readString();
		password = p.readString();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeInt(port);
		dest.writeString(host); 
		dest.writeString(username);
		dest.writeString(password);
	}
	
	@Override
	public int describeContents() { return 0; }
	 public static final Parcelable.Creator<WebSessionManager> CREATOR = new Parcelable.Creator<WebSessionManager>() {
		 public WebSessionManager createFromParcel(Parcel in) {  return new WebSessionManager(in); }
		 public WebSessionManager[] newArray(int size) {  return new WebSessionManager[size]; }
	 };
	
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
