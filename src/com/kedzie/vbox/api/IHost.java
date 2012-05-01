package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.soap.Cacheable;
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
	
	@Cacheable public Integer getMemorySize();
	@Cacheable public Integer getMemoryAvailable();
	@Cacheable public Integer getProcessorCount();
	@Cacheable public Integer getProcessorCoreCount();
	@Cacheable public Integer getProcessorOnlineCount();
	@Cacheable public Integer getProcessorSpeed();
	@Cacheable public String getOperatingSystem();
}
