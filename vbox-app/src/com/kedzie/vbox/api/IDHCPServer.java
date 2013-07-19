package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.soap.Asyncronous;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

@KSOAP
public interface IDHCPServer extends IManagedObjectRef, Parcelable {
	public static final String BUNDLE = "dhcp";
	static final ClassLoader LOADER = IDHCPServer.class.getClassLoader();
	
	public static final Parcelable.Creator<IDHCPServer> CREATOR = new Parcelable.Creator<IDHCPServer>() {
		public IDHCPServer createFromParcel(Parcel in) {
			VBoxSvc vmgr =  in.readParcelable(LOADER);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, LOADER);
			return (IDHCPServer) vmgr.getProxy(IDHCPServer.class, id, cache); 
		}
		public IDHCPServer[] newArray(int size) {  
			return new IDHCPServer[size]; 
		}
	};

	@KSOAP(cacheable=true) public boolean getEnabled();
	@Asyncronous public void setEnabled(@KSOAP("enabled") boolean enabled);
	
	@KSOAP(cacheable=true) public String getIPAddress();
	
	@KSOAP(cacheable=true) public String getNetworkMask();
	
	@KSOAP(cacheable=true) public String getNetworkName();
	
	@KSOAP(cacheable=true) public String getLowerIP();
	
	@KSOAP(cacheable=true) public String getUpperIP();
	
	@Asyncronous public void setConfiguration(@KSOAP("IPAddress") String ipAddress, @KSOAP("networkMask") String networkMask, @KSOAP("FromIPAddress") String fromIPAddress,@KSOAP("ToIPAddress") String toIPAddress);
	
	@Asyncronous public void start();
	
	@Asyncronous public void stop();
}
