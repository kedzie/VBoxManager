package com.kedzie.vbox.server;

import com.kedzie.vbox.app.Utils;

import android.os.Parcel;
import android.os.Parcelable;

public class Server implements Parcelable {
	public final static String BUNDLE = "server";

	public static final Parcelable.Creator<Server> CREATOR = new Parcelable.Creator<Server>() {
		public Server createFromParcel(Parcel in) {  
			Server s = new Server();
			s.setId(in.readLong());
			s.setName(in.readString());
			s.setHost(in.readString());
			boolean []tmp = new boolean[1];
			in.readBooleanArray(tmp);
			s.setSSL(tmp[0]);
			s.setPort(in.readInt());
			s.setUsername(in.readString());
			s.setPassword(in.readString());
			return s;
		}
		public Server[] newArray(int size) {  return new Server[size]; }
	};

	private Long id=-1L;
	private String name="";
	private Integer port=18083;
	private String host="";
	private String username="";
	private String password="";
	private boolean ssl;
	
	public Server() {}
	
	public Server(Long id, String name, String host, Boolean ssl, Integer port, String username, String password) {
		this.id=id;
		this.name=name;
		this.ssl=ssl;
		this.port = port;
		this.host = host;
		this.username = username;
		this.password=password;
	}
	
	public Server(String name, String host, Boolean ssl, Integer port, String username, String password) {
		this(-1L, name, host, ssl, port, username, password);
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeString(host); 
		dest.writeBooleanArray(new boolean[] { ssl });
		dest.writeInt(port);
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
	public boolean isSSL() {
		return ssl;
	}
	public void setSSL(boolean ssl) {
		this.ssl = ssl;
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
		return !Utils.isEmpty(name) ? name : (ssl ? "https://" : "http://") + getHost() + ":" + getPort();
	}
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null  || getClass() != obj.getClass()) return false;
		Server that = (Server) obj;
		return id.equals(that.id);
	}
}
