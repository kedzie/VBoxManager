package com.kedzie.vbox.server;

import android.os.Parcel;
import android.os.Parcelable;

public class Server implements Parcelable {
	 public static final Parcelable.Creator<Server> CREATOR = new Parcelable.Creator<Server>() {
		 public Server createFromParcel(Parcel in) {  return new Server(in); }
		 public Server[] newArray(int size) {  return new Server[size]; }
	 };
	 
	private Long id=-1L;
	private String name;
	private Integer port=18083;
	private String host;
	private String username;
	private String password;

	public Server() {}
	public Server(Long id, String name, String host, Integer port, String username, String password) {
		this.id=id;
		this.port = port;
		this.host = host;
		this.username = username;
		this.password=password;
	}
	public Server(Parcel p) {
		id = p.readLong();
		name=p.readString();
		port = p.readInt();
		host = p.readString(); 
		username = p.readString();
		password = p.readString();
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeInt(port);
		dest.writeString(host); 
		dest.writeString(username);
		dest.writeString(password);
	}
	
	@Override
	public int describeContents() { 
		return 0; 
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
		return (name!=null && !name.equals("")) ? name : getHost() + ":" + getPort();
	}
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null  || getClass() != obj.getClass()) return false;
		Server other = (Server) obj;
		return id.equals(other.id);
	}
}
