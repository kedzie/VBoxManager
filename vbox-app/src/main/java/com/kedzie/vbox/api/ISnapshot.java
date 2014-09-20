package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.soap.Asyncronous;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

@KSOAP(cacheable=true)
public interface ISnapshot extends IManagedObjectRef, Parcelable {
	public static final String BUNDLE = "snapshot";

	static ClassLoader loader = ISession.class.getClassLoader();
	
	public static final Parcelable.Creator<ISnapshot> CREATOR = new Parcelable.Creator<ISnapshot>() {
		public ISnapshot createFromParcel(Parcel in) {
			VBoxSvc vmgr =  in.readParcelable(loader);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, loader);
			return (ISnapshot) vmgr.getProxy(ISnapshot.class, id, cache); 
		}
		public ISnapshot[] newArray(int size) {  
			return new ISnapshot[size]; 
		}
	};
	
	public String getName();
	public String getId();
	public String getDescription();
	public Long getTimestamp();
	public Boolean getOnline();
	public ISnapshot getParent();
	public List<ISnapshot> getChildren();
	public IMachine getMachine();
	
	@KSOAP(cacheable=false)
    @Asyncronous
    public void setName(@KSOAP("name") String name);

    @KSOAP(cacheable=false)
    @Asyncronous
    public void setDescription(@KSOAP("description") String description);
}
