package com.kedzie.vbox;

import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.IManagedObjectRef;

public class ParcelableProxy implements Parcelable {

	private Class<?> type;
	private VBoxSvc api;
	private String idref;
	private Map<String, Object> cache;
	
	public ParcelableProxy() {}
	
	public ParcelableProxy(Class<?> clazz, IManagedObjectRef proxy) {
		type=clazz;
		idref=proxy.getIdRef();
		api=proxy.getVBoxAPI();
	}

	public IManagedObjectRef getProxy() {
		return (IManagedObjectRef) api.getProxy(type, idref, cache);
	}

	public String toString() {
		return "ParcelableProxy " + type + "/" + api;
	}
	
	public static final Parcelable.Creator<ParcelableProxy> CREATOR = new Parcelable.Creator<ParcelableProxy>() {
		public ParcelableProxy createFromParcel(Parcel in) {
			ParcelableProxy proxy = new ParcelableProxy();
			proxy.type = (Class<?>) in.readSerializable();
			proxy.api =  in.readParcelable(ParcelableProxy.class.getClassLoader());
			proxy.idref = in.readString();
			in.readMap(proxy.cache, ParcelableProxy.class.getClassLoader());
			return proxy; 
		}
		public ParcelableProxy[] newArray(int size) {  
			return new ParcelableProxy[size]; 
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeSerializable(type);
		out.writeParcelable(api, 0);
		out.writeString(idref);
		out.writeMap(cache);
	}
}
