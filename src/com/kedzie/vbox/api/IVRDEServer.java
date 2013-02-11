package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.jaxb.AuthType;
import com.kedzie.vbox.soap.Asyncronous;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

public interface IVRDEServer extends IManagedObjectRef, Parcelable {
	public static final String BUNDLE = "vrde";
	static final ClassLoader LOADER = IVRDEServer.class.getClassLoader();
    
    public static final Parcelable.Creator<IVRDEServer> CREATOR = new Parcelable.Creator<IVRDEServer>() {
		public IVRDEServer createFromParcel(Parcel in) {
			VBoxSvc vmgr =  in.readParcelable(LOADER);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, LOADER);
			return (IVRDEServer) vmgr.getProxy(IVRDEServer.class, id, cache); 
		}
		public IVRDEServer[] newArray(int size) {  
			return new IVRDEServer[size]; 
		}
	};
	
	@KSOAP(cacheable=true) public boolean getEnabled();
	@Asyncronous public void setEnabled(@KSOAP("enabled") boolean enabled);

	@KSOAP(cacheable=true) public AuthType getAuthType();
	@Asyncronous public void setAuthType(@KSOAP("authType") AuthType authType);
	     
	@KSOAP(cacheable=true) public int getAuthTimeout();
	@Asyncronous public void setAuthTimeout(@KSOAP(type="unsignedInt", value="authTimeout") int authTimeout);
     
	@KSOAP(cacheable=true) public boolean getAllowMultiConnection();
	@Asyncronous public void setAllowMultiConnection(@KSOAP("allowMultiConnection") boolean allowMultiConnection);
	
	@KSOAP(cacheable=true) public boolean getReuseSingleConnection();
	@Asyncronous public void setReuseSingleConnection(@KSOAP("reuseSingleConnection") boolean reuseSingleConnection);
	
	@KSOAP(cacheable=true) public String getVRDEExtPack();
	@Asyncronous public void setVRDEExtPack(@KSOAP("VRDEExtPack") String vrdeExtPack);
	
	@KSOAP(cacheable=true) public String getAuthLibrary();
	@Asyncronous public void setAuthLibrary(@KSOAP("authLibrary") String authLibrary);
	
	@KSOAP(cacheable=true) public String[] getVRDEProperties();
	
	@Asyncronous public void setVRDEProperty(@KSOAP("key") String key, @KSOAP("value") String value);
	
	@KSOAP(cacheable=true) public String getVRDEProperty(@KSOAP("key") String key);
}
