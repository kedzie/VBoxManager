package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;


/**
 * The {@link IVirtualBox} interface represents the main interface exposed by the product that provides virtual machine management. 
 * <p>An instance of {@link IVirtualBox} is required for the product to do anything useful. Even though the interface does not expose this, internally, {@link IVirtualBox} is implemented as a singleton and actually lives in the process of the VirtualBox server <code>(VBoxSVC.exe)</code>. This makes sure that {@link IVirtualBox} can track the state of all virtual machines on a particular host, regardless of which frontend started them.
 * <p>To enumerate all the virtual machines on the host, use the {@link IVirtualBox#getMachines} attribute.
 * <p><dl class="user" compact><dt><b>Interface ID:</b></dt><dd><code>{C28BE65F-1A8F-43B4-81F1-EB60CB516E66}</code> </dd></dl>
 */
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
	
	@KSOAP(cacheable=true) public String getVersion();
	@KSOAP(cacheable=true) public IEventSource getEventSource() ;
	@KSOAP(cacheable=true) public IPerformanceCollector getPerformanceCollector();
	@KSOAP(cacheable=true) public IHost getHost() ;
	@KSOAP(cacheable=true)  ISystemProperties getSystemProperties();
	
	@KSOAP(prefix="IWebsessionManager", thisReference="")
	public IVirtualBox logon(@KSOAP("username") String username, @KSOAP("password") String password) throws IOException;
	@KSOAP(prefix="IWebsessionManager", thisReference="refIVirtualBox")
	public void logoff() throws IOException;
	@KSOAP(cacheable=true, prefix="IWebsessionManager", thisReference="refIVirtualBox")
	public ISession getSessionObject() throws IOException;
	
	public List<IMachine> getMachines() throws IOException;
	public IMachine findMachine(@KSOAP("nameOrId") String nameOrId) throws IOException;
	
	/**
	 * <p>Array of all machine group names which are used by the machines which are accessible. </p>
     *<p>Each group is only listed once, however they are listed in no particular order and there is no guarantee that there are no gaps in the group hierarchy (i.e. <code>"/"</code>, <code>"/group/subgroup"</code> is a valid result). </p>
	 */
	public List<String> getMachineGroups();
	
	/**
	 * Gets all machine references which are in one of the specified groups.
     *  @param groups       What groups to match. The usual group list rules apply, i.e. passing an empty list will match VMs in the toplevel group, likewise the empty string.
     *  @return     All machines which matched.
	 */
	public List<IMachine> getMachinesByGroups(@KSOAP("groups")String...groups);
	
}
