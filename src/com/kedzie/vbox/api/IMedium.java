package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.jaxb.DeviceType;
import com.kedzie.vbox.api.jaxb.MediumState;
import com.kedzie.vbox.api.jaxb.MediumType;
import com.kedzie.vbox.soap.Asyncronous;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

@KSOAP
public interface IMedium extends IManagedObjectRef, Parcelable {
	public static final String BUNDLE = "medium";
	static final ClassLoader LOADER = IMedium.class.getClassLoader();
	
	public static final Parcelable.Creator<IMedium> CREATOR = new Parcelable.Creator<IMedium>() {
		public IMedium createFromParcel(Parcel in) {
			VBoxSvc vmgr =  in.readParcelable(LOADER);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, LOADER);
			return (IMedium) vmgr.getProxy(IMedium.class, id, cache); 
		}
		public IMedium[] newArray(int size) {  
			return new IMedium[size]; 
		}
	};
	
     @KSOAP(cacheable=true) public String getId();
     
     @KSOAP(cacheable=true) public String getDescription();
     @Asyncronous public void setDescription(@KSOAP("description")String description);
     
     @KSOAP(cacheable=true) public MediumState getState();
     
     @KSOAP(cacheable=true) public Integer getVariant();

     @KSOAP(cacheable=true) public String getLocation();
     @Asyncronous public void setLocation(@KSOAP("location")String location);
     
     @KSOAP(cacheable=true) public String getName();
     
     @KSOAP(cacheable=true) public DeviceType getDeviceType();
     
     @KSOAP(cacheable=true) public Boolean getHostDrive();

     @KSOAP(cacheable=true) public Long getSize();

     @KSOAP(cacheable=true) public String getFormat();
     
     @KSOAP(cacheable=true) public IMediumFormat getMediumFormat();
     
     @KSOAP(cacheable=true) public MediumType getType();
     @Asyncronous public void setType(@KSOAP("type") MediumType type);
     
     @KSOAP(cacheable=true) public MediumType[]  getAllowedTypes();
     
     @KSOAP(cacheable=true) public IMedium getParent();
     
     @KSOAP(cacheable=true) public List<IMedium> getChildren();
     
     @KSOAP(cacheable=true) public IMedium getBase();

     @KSOAP(cacheable=true) public Boolean getReadOnly();

     @KSOAP(cacheable=true) public Long getLogicalSize();

     @KSOAP(cacheable=true) public Boolean getAutoReset();
     @Asyncronous public void setAutoReset(@KSOAP("autoReset") boolean autoReset);
     
     @KSOAP(cacheable=true) public String getLastAccessError();

     @KSOAP(cacheable=true) public List<String> getMachineIds();
     
     public void setIds(@KSOAP("setImageId") boolean setImageId, @KSOAP("imageId") String imageId, @KSOAP("setParentId") boolean setParentId, @KSOAP("parentId") String parentId);
     
     public MediumState refreshState();

     @KSOAP(cacheable=true) public List<String> getSnapshotIds();

     public MediumState lockRead();
     public MediumState unlockRead();

     public MediumState lockWrite();
     public MediumState unlockWrite();

     public void close();

     @KSOAP(cacheable=true) String getProperty(@KSOAP("key") String key);
 	@Asyncronous public void setProperty(@KSOAP("key") String key, @KSOAP("value") String value);
 	@KSOAP(cacheable=true) public Map<String, List<String>> getProperties(@KSOAP("names") String names);
 	@Asyncronous public void setProperties(@KSOAP("names") List<String> names, @KSOAP("values") List<String> values);

 	public IProgress createBaseStorage(@KSOAP(type="long", value="logicalSize") long logicalSize, @KSOAP(type="unsignedInt", value="variant") int variant);
 	
 	public IProgress createDiffStorage(@KSOAP(type="long", value="logicalSize") long logicalSize, @KSOAP(type="unsignedInt", value="variant") int variant);

 	public IProgress deleteStorage();

 	public IProgress mergeTo(@KSOAP("target") IMedium target);

 	public IProgress cloneTo(@KSOAP("target") IMedium target, @KSOAP(type="unsignedInt", value="variant")int variant, @KSOAP("parent") IMedium parent);

 	public IProgress cloneToBase(@KSOAP("target") IMedium target, @KSOAP(type="unsignedInt", value="variant")int variant);

 	public IProgress compact();

 	public IProgress resize(@KSOAP(type="long", value="logicalSize") long logicalSize);
 	
 	public IProgress reset();
}