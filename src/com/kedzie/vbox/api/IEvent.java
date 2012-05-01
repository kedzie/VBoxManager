package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.soap.Cacheable;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

public interface IEvent extends IManagedObjectRef, Parcelable {

	static ClassLoader loader = IEvent.class.getClassLoader();
	
	public static final Parcelable.Creator<IEvent> CREATOR = new Parcelable.Creator<IEvent>() {
		public IEvent createFromParcel(Parcel in) {
			Class<?> clazz = (Class<?>) in.readSerializable();
			VBoxSvc vmgr =  in.readParcelable(loader);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, loader);
			return (IEvent) vmgr.getProxy(clazz, id, cache); 
		}
		public IEvent[] newArray(int size) {  
			return new IEvent[size]; 
		}
	};
	
	@Cacheable @KSOAP(prefix="IEvent") public VBoxEventType getType();
	@KSOAP(prefix="IEvent") public void setProcessed();
	@KSOAP(prefix="IEvent")	public Boolean waitProcessed(@KSOAP(type="unsignedInt", value="timeout") int timeout);
}
