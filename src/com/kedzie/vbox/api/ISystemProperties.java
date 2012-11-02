package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

@KSOAP(cacheable=true) 
public interface ISystemProperties extends IManagedObjectRef, Parcelable {
    static ClassLoader loader = ISystemProperties.class.getClassLoader();
    
    public static final Parcelable.Creator<ISystemProperties> CREATOR = new Parcelable.Creator<ISystemProperties>() {
        public ISystemProperties createFromParcel(Parcel in) {
            Class<?> clazz = (Class<?>) in.readSerializable();
            VBoxSvc vmgr =  in.readParcelable(loader);
            String id = in.readString();
            Map<String, Object> cache = new HashMap<String, Object>();
            in.readMap(cache, loader);
            return (ISystemProperties) vmgr.getProxy(clazz, id, cache); 
        }
        public ISystemProperties[] newArray(int size) {  
            return new ISystemProperties[size]; 
        }
    };
    
	public Integer getMinGuestRAM();
	public Integer getMaxGuestRAM();
	public Integer getMinGuestVRAM();
	public Integer getMaxGuestVRAM();
	public Integer getMinGuestCPUCount();
	public Integer getMaxGuestCPUCount();
	public Integer getMaxGuestMonitors();
}
