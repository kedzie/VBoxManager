package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.jaxb.AudioDriverType;
import com.kedzie.vbox.api.jaxb.ChipsetType;
import com.kedzie.vbox.api.jaxb.DeviceType;
import com.kedzie.vbox.api.jaxb.NetworkAttachmentType;
import com.kedzie.vbox.api.jaxb.StorageBus;
import com.kedzie.vbox.soap.Asyncronous;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

@KSOAP(cacheable=true) 
public interface ISystemProperties extends IManagedObjectRef, Parcelable {
    static ClassLoader loader = ISystemProperties.class.getClassLoader();
    
    public static final Parcelable.Creator<ISystemProperties> CREATOR = new Parcelable.Creator<ISystemProperties>() {
        public ISystemProperties createFromParcel(Parcel in) {
            VBoxSvc vmgr =  in.readParcelable(loader);
            String id = in.readString();
            Map<String, Object> cache = new HashMap<String, Object>();
            in.readMap(cache, loader);
            return (ISystemProperties) vmgr.getProxy(ISystemProperties.class, id, cache); 
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

	public Integer getMaxBootPosition();
	
	public String getWebServiceAuthLibrary();
	@Asyncronous @KSOAP(cacheable=false) public void setWebServiceAuthLibrary(@KSOAP("webServiceAuthLibrary") String webServiceAuthLibrary);
	
	public String getVRDEAuthLibrary();
	@Asyncronous @KSOAP(cacheable=false) public void setVRDEAuthLibrary(@KSOAP("VRDEAuthLibrary") String vrdeAuthLibrary);
	
	public Integer getMaxNetworkAdapters(@KSOAP("chipset") ChipsetType chipset);
	public Integer getMaxNetworkAdapters(@KSOAP("chipset") ChipsetType chipset, @KSOAP("type") NetworkAttachmentType type);

	public Integer getMaxDevicesPerPortForStorageBus(@KSOAP("bus") StorageBus bus);
	public Integer getMinPortCountForStorageBus(@KSOAP("bus") StorageBus bus);
	public Integer getMaxPortCountForStorageBus(@KSOAP("bus") StorageBus bus);
	public Integer getMaxInstancesOfStorageBus(@KSOAP("chipset") ChipsetType chipset, @KSOAP("bus") StorageBus bus);
	public List<DeviceType> getDeviceTypesForStorageBus(@KSOAP("bus") StorageBus bus);
	
	public AudioDriverType getDefaultAudioDriver();
	
	public IMediumFormat getMediumFormats();
}
