package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.soap.Asyncronous;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * 
 */
public interface IBIOSSettings extends IManagedObjectRef, Parcelable {
	
static final ClassLoader LOADER = IBIOSSettings.class.getClassLoader();
	
	public static final Parcelable.Creator<IBIOSSettings> CREATOR = new Parcelable.Creator<IBIOSSettings>() {
		public IBIOSSettings createFromParcel(Parcel in) {
			VBoxSvc vmgr =  in.readParcelable(LOADER);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, LOADER);
			return (IBIOSSettings) vmgr.getProxy(IBIOSSettings.class, id, cache); 
		}
		public IBIOSSettings[] newArray(int size) {  
			return new IBIOSSettings[size]; 
		}
	};

	@KSOAP(cacheable=true) public Boolean getLogoFadeIn();
	
	@Asyncronous public void setLogoFadeIn(@KSOAP("logoFadeIn") boolean logoFadeIn);
	
	@KSOAP(cacheable=true) public Boolean getLogoFadeOut();
	
	@Asyncronous public void setLogoFadeOut(@KSOAP("logoFadeOut") boolean logoFadeOut);
	
	@KSOAP(cacheable=true) public int getLogoDisplayTime();
	
	@Asyncronous public void setLogoDisplayTime(@KSOAP(type="unsignedInt", value="logoDisplayTime") int logoDisplayTime);

	@KSOAP(cacheable=true) public Boolean getACPIEnabled();
	
	@Asyncronous public void setACPIEnabled(@KSOAP("ACPIEnabled") boolean acpiEnabled);
	
	@KSOAP(cacheable=true) public Boolean getIOAPICEnabled();
	
	@Asyncronous public void setIOAPICEnabled(@KSOAP("IOAPICEnabled") boolean ioAPICEnabled);
}
