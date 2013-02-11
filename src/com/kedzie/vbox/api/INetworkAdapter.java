package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.jaxb.NetworkAdapterPromiscModePolicy;
import com.kedzie.vbox.api.jaxb.NetworkAdapterType;
import com.kedzie.vbox.api.jaxb.NetworkAttachmentType;
import com.kedzie.vbox.soap.Asyncronous;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

public interface INetworkAdapter extends IManagedObjectRef, Parcelable {
	public static final String BUNDLE = "network";
	static final ClassLoader LOADER = INetworkAdapter.class.getClassLoader();
	
	public static final Parcelable.Creator<INetworkAdapter> CREATOR = new Parcelable.Creator<INetworkAdapter>() {
		public INetworkAdapter createFromParcel(Parcel in) {
			VBoxSvc vmgr =  in.readParcelable(LOADER);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, LOADER);
			return (INetworkAdapter) vmgr.getProxy(INetworkAdapter.class, id, cache); 
		}
		public INetworkAdapter[] newArray(int size) {  
			return new INetworkAdapter[size]; 
		}
	};
	
	@KSOAP(cacheable=true) public NetworkAdapterType getAdapterType();
	@Asyncronous public void setAdapterType(@KSOAP("adapterType") NetworkAdapterType adapterType);
	
	@KSOAP(cacheable=true) public Integer getSlot();

	@KSOAP(cacheable=true) public Boolean getEnabled();
	@Asyncronous public void setEnabled(@KSOAP("enabled") boolean enabled);

	@KSOAP(cacheable=true) public String getMACAddress();
	@Asyncronous public void setMACAddress(@KSOAP("MACAddress") String macAddress);

	@KSOAP(cacheable=true) public NetworkAttachmentType getAttachmentType();
	@Asyncronous public void setAttachmentType(@KSOAP("attachmentType") NetworkAttachmentType attachmentType);

	@KSOAP(cacheable=true) public String getBridgedInterface();
	@Asyncronous public void setBridgedInterface(@KSOAP("bridgedInterface") String bridgedInterface);

	@KSOAP(cacheable=true) public String getHostOnlyInterface();
	@Asyncronous public void setHostOnlyInterface(@KSOAP("hostOnlyInterface") String hostOnlyInterface);

	@KSOAP(cacheable=true) public String getInternalNetwork();
	@Asyncronous public void setInternalNetwork(@KSOAP("internalNetwork") String internalNetwork);

	@KSOAP(cacheable=true) public String getNATNetwork();
	@Asyncronous public void setNATNetwork(@KSOAP("NATNetwork") String natNetwork);

	@KSOAP(cacheable=true) public String getGenericDriver();
	@Asyncronous public void setGenericDriver(@KSOAP("genericDriver") String genericDriver);
	
	@KSOAP(cacheable=true) public Boolean getCableConnected();
	@Asyncronous public void setCableConnected(@KSOAP("cableConnected") boolean cableConnected);

	@KSOAP(cacheable=true) public Integer getLineSpeed();
	@Asyncronous public void setLineSpeed(@KSOAP(type="unsignedInt", value="lineSpeed") int lineSpeed);
	
	@KSOAP(cacheable=true) public NetworkAdapterPromiscModePolicy getPromiscModePolicy();
	@Asyncronous public void setPromiscModePolicy(@KSOAP("promiscModePolicy") NetworkAdapterPromiscModePolicy promiscModePolicy);

	@KSOAP(cacheable=true) public Boolean getTraceEnabled();
	@Asyncronous public void setTraceEnabled(@KSOAP("traceEnabled") boolean traceEnabled);
	
	@KSOAP(cacheable=true) public String getTraceFile();
	@Asyncronous public void setTraceFile(@KSOAP("traceFile") String traceFile);
	
	@KSOAP(cacheable=true) public Integer getBootPriority();
	@Asyncronous public void setBootPriority(@KSOAP("bootPriority") int bootPriority);
	
//	@KSOAP(cacheable=true) public IBandwidthGroup getBandwidthGroup();
//	@Asyncronous public void setBandwidthGroup(@KSOAP("bandwidthGroup") IBandwidthGroup bandwidthGroup);
//	KSOAP(cacheable=true) public INATEngine getNATEngine();
	
	@KSOAP(cacheable=true) String getProperty(@KSOAP("key") String key);
	@Asyncronous public void setProperty(@KSOAP("key") String key, @KSOAP("value") String value);
	@KSOAP(cacheable=true) public Map<String, List<String>> getProperties(@KSOAP("names") String names);
}
