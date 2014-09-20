package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

@KSOAP(cacheable=true)  
public interface IPerformanceMetric extends IManagedObjectRef, Parcelable {
    
    static final ClassLoader LOADER = IPerformanceMetric.class.getClassLoader();

    public static Parcelable.Creator<IPerformanceMetric> CREATOR = new Parcelable.Creator<IPerformanceMetric>() {
        @Override
        public IPerformanceMetric createFromParcel(Parcel in) {
            VBoxSvc vmgr =  in.readParcelable(LOADER);
            String id = in.readString();
            Map<String, Object> cache = new HashMap<String, Object>();
            in.readMap(cache, LOADER);
            return (IPerformanceMetric) vmgr.getProxy(IPerformanceMetric.class, id, cache); 
        }

        @Override
        public IPerformanceMetric[] newArray(int size) {
            return new IPerformanceMetric[size];
        }
    };
    
	public String getMetricName();
	public String getDescription();
	public Integer getMinimumValue();
	public Integer getMaximumValue();
	public String getUnit();
	public String getObject();
}
