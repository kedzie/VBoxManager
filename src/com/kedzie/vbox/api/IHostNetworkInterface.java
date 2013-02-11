package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.jaxb.HostNetworkInterfaceMediumType;
import com.kedzie.vbox.api.jaxb.HostNetworkInterfaceStatus;
import com.kedzie.vbox.api.jaxb.HostNetworkInterfaceType;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

public interface IHostNetworkInterface extends IManagedObjectRef, Parcelable {
	public static final String BUNDLE = "hostNetwork";
	static final ClassLoader LOADER = IHostNetworkInterface.class.getClassLoader();
	
	public static Parcelable.Creator<IHostNetworkInterface> CREATOR = new Parcelable.Creator<IHostNetworkInterface>() {
		@Override
		public IHostNetworkInterface createFromParcel(Parcel in) {
			VBoxSvc vmgr =  in.readParcelable(LOADER);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, LOADER);
			return (IHostNetworkInterface) vmgr.getProxy(IHostNetworkInterface.class, id, cache); 
		}
		
		@Override
		public IHostNetworkInterface[] newArray(int size) {
			return new IHostNetworkInterface[size];
		}
	};

	@KSOAP(cacheable=true) public String getName();
	
	@KSOAP(cacheable=true) public String getId();
	
	@KSOAP(cacheable=true) public String getNetworkName();
	
	@KSOAP(cacheable=true) public boolean getDHCPEnabled();

	@KSOAP(cacheable=true) public String getIPAddress();
	
	@KSOAP(cacheable=true) public String getNetworkMask();

	@KSOAP(cacheable=true) public boolean getIPV6Supported();
	
	@KSOAP(cacheable=true) public String getIPV6Address();
	
	@KSOAP(cacheable=true) public int getIPV6NetworkMaskPrefixLength();
	
	@KSOAP(cacheable=true) public String getHardwareAddress();
	
	@KSOAP(cacheable=true) public HostNetworkInterfaceMediumType getMediumType();
	
	@KSOAP(cacheable=true) public HostNetworkInterfaceStatus getStatus();
	
	@KSOAP(cacheable=true) public HostNetworkInterfaceType getInterfaceType();
	
	public void enableStaticIPConfig(@KSOAP("IPAddress") String ipAddress, @KSOAP("networkMask") String networkMask);
	
	public void enableStaticIPConfigV6(@KSOAP("IPV6Address") String ipv6Address, @KSOAP(type="unsignedInt", value="IPV6NetworkMaskPrefixLength") int ipv6NetworkMaskPrefixLength);
	
	public void enableDynamicIPConfig();
	
    @KSOAP(cacheable=true) public boolean DHCPRediscover();
}
