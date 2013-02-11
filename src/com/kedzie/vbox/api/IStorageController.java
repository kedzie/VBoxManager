package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.jaxb.StorageBus;
import com.kedzie.vbox.api.jaxb.StorageControllerType;
import com.kedzie.vbox.soap.Asyncronous;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

public interface IStorageController extends IManagedObjectRef, Parcelable {
	public static final String BUNDLE = "controller";
	static final ClassLoader LOADER = IMachine.class.getClassLoader();
	
	public static final Parcelable.Creator<IStorageController> CREATOR = new Parcelable.Creator<IStorageController>() {
		public IStorageController createFromParcel(Parcel in) {
			VBoxSvc vmgr =  in.readParcelable(LOADER);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, LOADER);
			return (IStorageController) vmgr.getProxy(IStorageController.class, id, cache); 
		}
		public IStorageController[] newArray(int size) {  
			return new IStorageController[size]; 
		}
	};

	@KSOAP(cacheable=true) public String getName();
	@KSOAP(cacheable=true) public Integer getDevicesPerPortCount();
	@KSOAP(cacheable=true) public Integer getMinPortCount();
	@KSOAP(cacheable=true) public Integer getMaxPortCount();
	@KSOAP(cacheable=true) public StorageBus getBus();

	@KSOAP(cacheable=true) public Integer getInstance();
	@Asyncronous public void setInstance(@KSOAP(type="unsignedInt", value="instance") int instance);
	
	@KSOAP(cacheable=true) public Integer getPortCount();
	@Asyncronous public void setPortCount(@KSOAP(type="unsignedInt", value="portCount") int portCount);
	
	@KSOAP(cacheable=true) public StorageControllerType getControllerType();
	@Asyncronous public void setControllerType(@KSOAP("controllerType") StorageControllerType controllerType);

	@KSOAP(cacheable=true) public Boolean getUseHostIOCache();
	@Asyncronous public void setUseHostIOCache(@KSOAP("useHostIOCache") boolean useHostIOCache);

	@KSOAP(cacheable=true) public Boolean getBootable();
}