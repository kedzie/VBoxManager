package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.jaxb.MachineState;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

public interface IMachineStateChangedEvent extends IMachineEvent, Parcelable {

	static ClassLoader loader = IMachineStateChangedEvent.class.getClassLoader();
	
	public static final Parcelable.Creator<IMachineStateChangedEvent> CREATOR = new Parcelable.Creator<IMachineStateChangedEvent>() {
		public IMachineStateChangedEvent createFromParcel(Parcel in) {
			VBoxSvc vmgr =  in.readParcelable(loader);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, loader);
			return (IMachineStateChangedEvent) vmgr.getProxy(IMachineStateChangedEvent.class, id, cache); 
		}
		public IMachineStateChangedEvent[] newArray(int size) {  
			return new IMachineStateChangedEvent[size]; 
		}
	};

	@KSOAP(cacheable=true, prefix="IMachineStateChangedEvent") 	public MachineState getState();
}
