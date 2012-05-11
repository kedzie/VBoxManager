package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.jaxb.SessionState;
import com.kedzie.vbox.api.jaxb.SessionType;
import com.kedzie.vbox.soap.VBoxSvc;

public interface ISession extends IManagedObjectRef, Parcelable {
	
	static ClassLoader loader = ISession.class.getClassLoader();
	
	public static final Parcelable.Creator<ISession> CREATOR = new Parcelable.Creator<ISession>() {
		public ISession createFromParcel(Parcel in) {
			Class<?> clazz = (Class<?>) in.readSerializable();
			VBoxSvc vmgr =  in.readParcelable(loader);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, loader);
			return (ISession) vmgr.getProxy(clazz, id, cache); 
		}
		public ISession[] newArray(int size) {  
			return new ISession[size]; 
		}
	};

	public void unlockMachine() throws IOException;;
	public IConsole getConsole() throws IOException;
	public SessionType getType() throws IOException;
	public SessionState getState() throws IOException;
}
