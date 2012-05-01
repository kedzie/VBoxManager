package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.soap.Cacheable;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;


public interface IVirtualBox extends IManagedObjectRef, Parcelable {
	static ClassLoader loader = IVirtualBox.class.getClassLoader();
	
	public static final Parcelable.Creator<IVirtualBox> CREATOR = new Parcelable.Creator<IVirtualBox>() {
		public IVirtualBox createFromParcel(Parcel in) {
			Class<?> clazz = (Class<?>) in.readSerializable();
			VBoxSvc vmgr =  in.readParcelable(loader);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, loader);
			return (IVirtualBox) vmgr.getProxy(clazz, id, cache); 
		}
		public IVirtualBox[] newArray(int size) {  
			return new IVirtualBox[size]; 
		}
	};
	
	@Cacheable public String getVersion();
	@Cacheable public IEventSource getEventSource() ;
	@Cacheable public IPerformanceCollector getPerformanceCollector();
	@Cacheable public IHost getHost() ;
	@Cacheable  ISystemProperties getSystemProperties();
	
	@KSOAP(prefix="IWebsessionManager", thisReference="")
	public IVirtualBox logon(@KSOAP("username") String username, @KSOAP("password") String password) throws IOException;
	@KSOAP(prefix="IWebsessionManager", thisReference="refIVirtualBox")
	public void logoff() throws IOException;
	@Cacheable @KSOAP(prefix="IWebsessionManager", thisReference="refIVirtualBox")
	public ISession getSessionObject() throws IOException;
	
	public List<IMachine> getMachines() throws IOException;
	public IMachine findMachine(@KSOAP("nameOrId") String nameOrId) throws IOException;
}
