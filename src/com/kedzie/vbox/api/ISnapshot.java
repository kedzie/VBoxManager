package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

public interface ISnapshot extends IManagedObjectRef, Parcelable {

static ClassLoader loader = ISession.class.getClassLoader();
	
	public static final Parcelable.Creator<ISnapshot> CREATOR = new Parcelable.Creator<ISnapshot>() {
		public ISnapshot createFromParcel(Parcel in) {
			Class<?> clazz = (Class<?>) in.readSerializable();
			VBoxSvc vmgr =  in.readParcelable(loader);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, loader);
			return (ISnapshot) vmgr.getProxy(clazz, id, cache); 
		}
		public ISnapshot[] newArray(int size) {  
			return new ISnapshot[size]; 
		}
	};
	
	@KSOAP(cacheable=true) public String getName();
	@KSOAP(cacheable=true) public String getDescription();
	@KSOAP(cacheable=true) public Long getTimestamp();
	@KSOAP(cacheable=true) public Boolean getOnline();
	@KSOAP(cacheable=true) public ISnapshot getParent();
	@KSOAP(cacheable=true) public List<ISnapshot> getChildren();
	@KSOAP(cacheable=true) public IMachine getMachine();
}
