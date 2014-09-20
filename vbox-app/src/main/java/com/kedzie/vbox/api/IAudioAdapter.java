package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.jaxb.AudioControllerType;
import com.kedzie.vbox.api.jaxb.AudioDriverType;
import com.kedzie.vbox.soap.Asyncronous;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

@KSOAP
public interface IAudioAdapter extends IManagedObjectRef, Parcelable {
	public final static String BUNDLE = "audio";
	static final ClassLoader LOADER = IAudioAdapter.class.getClassLoader();
	
	public static final Parcelable.Creator<IAudioAdapter> CREATOR = new Parcelable.Creator<IAudioAdapter>() {
		public IAudioAdapter createFromParcel(Parcel in) {
			VBoxSvc vmgr =  in.readParcelable(LOADER);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, LOADER);
			return (IAudioAdapter) vmgr.getProxy(IAudioAdapter.class, id, cache); 
		}
		public IAudioAdapter[] newArray(int size) {  
			return new IAudioAdapter[size]; 
		}
	};

     @KSOAP(cacheable=true) public Boolean getEnabled();
 	@Asyncronous public void setEnabled(@KSOAP("enabled") boolean enabled);
 	
 	@KSOAP(cacheable=true) public AudioControllerType getAudioController();
	@Asyncronous public void setAudioController(@KSOAP("audioController") AudioControllerType audioController);
     
	@KSOAP(cacheable=true) public AudioDriverType getAudioDriver();
	@Asyncronous public void setAudioDriver(@KSOAP("audioDriver") AudioDriverType audioDriver);
     
}
