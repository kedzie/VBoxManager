package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

@KSOAP(cacheable=true, prefix="IMachineEvent")
public interface IMachineEvent extends IEvent, Parcelable {

	static ClassLoader loader = IEvent.class.getClassLoader();
	
	public static final Parcelable.Creator<IMachineEvent> CREATOR = new Parcelable.Creator<IMachineEvent>() {
		public IMachineEvent createFromParcel(Parcel in) {
			VBoxSvc vmgr =  in.readParcelable(loader);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, loader);
			return (IMachineEvent) vmgr.getProxy(IMachineEvent.class, id, cache); 
		}
		public IMachineEvent[] newArray(int size) {  
			return new IMachineEvent[size]; 
		}
	};

	public String getMachineId();
}
