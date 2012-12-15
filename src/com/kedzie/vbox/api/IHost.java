package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * The IHost interface represents the physical machine that this VirtualBox installation runs on.
 * An object implementing this interface is returned by the {@link IVirtualBox#getHost} attribute. This interface 
 * contains read-only information about the host's physical hardware (such as what processors and disks 
 * are available, what the host operating system is, and so on) and also allows for manipulating some of the host's 
 * hardware, such as global USB device filters and host interface networking.
 */
public interface IHost extends IManagedObjectRef, Parcelable {
	static ClassLoader loader = IHost.class.getClassLoader();
	
	public static final Parcelable.Creator<IHost> CREATOR = new Parcelable.Creator<IHost>() {
		public IHost createFromParcel(Parcel in) {
			Class<?> clazz = (Class<?>) in.readSerializable();
			VBoxSvc vmgr =  in.readParcelable(loader);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, loader);
			return (IHost) vmgr.getProxy(clazz, id, cache); 
		}
		public IHost[] newArray(int size) {  
			return new IHost[size]; 
		}
	};
	
	/**
	 * @return	Amount of system memory in megabytes installed in the host system. 
	 */
	@KSOAP(cacheable=true) public Integer getMemorySize();
	
	/**
	 * @return	Available system memory in the host system. 
	 */
	@KSOAP(cacheable=true) public Integer getMemoryAvailable();
	
	/**
	 * @return	Number of (logical) CPUs installed in the host system.
	 */
	@KSOAP(cacheable=true) public Integer getProcessorCount();
	
	/**
	 * @return	Number of physical processor cores installed in the host system. 
	 */
	@KSOAP(cacheable=true) public Integer getProcessorCoreCount();
	
	/**
	 * @return	Number of (logical) CPUs online in the host system. 
	 */
	@KSOAP(cacheable=true) public Integer getProcessorOnlineCount();
	
	/**
	 * Query the (approximate) maximum speed of a specified host CPU in Megahertz.
	 * @param cpuId		Identifier of the CPU
	 * @return 	Speed value. 0 is returned if value is not known or cpuId is invalid.
	 */
	@KSOAP(cacheable=true) public Integer getProcessorSpeed(@KSOAP(type="unsignedint", value="cpuId") int cpuId);
	
	/**
	 * @return	Name of the host system's operating system. 
	 */
	@KSOAP(cacheable=true) public String getOperatingSystem();
	
	/**
	 * @return	Host operating system's version string.
	 */
	@KSOAP(cacheable=true) public String getOSVersion();
	
	/**
	 * @return	Returns true when the host supports 3D hardware acceleration. 
	 */
	@KSOAP(cacheable=true) public boolean getAcceleration3DAvailable();
	
	/**
	 * @return	Returns the current host time in milliseconds since 1970-01-01 UTC. 
	 */
	@KSOAP(cacheable=true) public Long getUTCTime();
}
