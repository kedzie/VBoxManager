package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

public interface IHost extends IManagedObjectRef, Parcelable {
	static ClassLoader loader = IHost.class.getClassLoader();
	
	public static final Parcelable.Creator<IHost> CREATOR = new Parcelable.Creator<IHost>() {
		public IHost createFromParcel(Parcel in) {
			Class<?> clazz = (Class<?>) in.readSerializable();
			VBoxSvc vmgr =  in.readParcelable(loader);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, loader);
			return (IHost) vmgr.getProxy(clazz, id, cache); 
		}
		public IHost[] newArray(int size) {  
			return new IHost[size]; 
		}
	};
	
	@KSOAP(cacheable=true) public Integer getMemorySize();
	@KSOAP(cacheable=true) public Integer getMemoryAvailable();
	@KSOAP(cacheable=true) public Integer getProcessorCount();
	@KSOAP(cacheable=true) public Integer getProcessorCoreCount();
	@KSOAP(cacheable=true) public Integer getProcessorOnlineCount();
	@KSOAP(cacheable=true) public Integer getProcessorSpeed();
	@KSOAP(cacheable=true) public String getOperatingSystem();
}
