package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.jaxb.SessionState;
import com.kedzie.vbox.api.jaxb.SessionType;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * The {@link IMachine} objects) to prevent conflicting changes to the machine. 
 * <p>Any caller wishing to manipulate a virtual machine needs to create a session object first, which lives in its own process space. Such session objects are then associated with {@link IMachine}<b></b> objects living in the VirtualBox server process to coordinate such changes.<p>
 * There are two typical scenarios in which sessions are used:<p>
 * <ul>
 * <li>To alter machine settings or control a running virtual machine, one needs to lock a machine for a given session (client process) by calling {@link IMachine#lockMachine}<b></b>.<p>
 * Whereas multiple sessions may control a running virtual machine, only one process can obtain a write lock on the machine to prevent conflicting changes. A write lock is also needed if a process wants to actually run a virtual machine in its own context, such as the VirtualBox GUI or VBoxHeadless front-ends. They must also lock a machine for their own sessions before they are allowed to power up the virtual machine.<p>
 * As a result, no machine settings can be altered while another process is already using it, either because that process is modifying machine settings or because the machine is running.  </li>
 * <li>To start a VM using one of the existing VirtualBox front-ends (e.g. the VirtualBox GUI or VBoxHeadless), one would use {@link IMachine#launchVMProcess}<b></b>, which also takes a session object as its first parameter. This session then identifies the caller and lets the caller control the started machine (for example, pause machine execution or power it down) as well as be notified about machine execution state changes.  </li>
 * </ul>
 * <p>How sessions objects are created in a client process depends on whether you use the Main API via COM or via the webservice:<p>
 * <ul>
 * <li>When using the COM API directly, an object of the Session class from the VirtualBox type library needs to be created. In regular COM C++ client code, this can be done by calling <code>createLocalObject()</code>, a standard COM API. This object will then act as a local session object in further calls to open a session.  </li>
 * <li>In the webservice, the session manager ({@link IWebsessionManager}) instead creates a session object automatically whenever {@link IWebsessionManager#getSessionObject}<b></b>.  </li>
 * </ul>
 * <dl class="user" compact><dt><b>Interface ID:</b></dt><dd><code>{12F4DCDB-12B2-4EC1-B7CD-DDD9F6C5BF4D}</code> </dd></dl>
 */
@KSOAP
public interface ISession extends IManagedObjectRef, Parcelable {
	
	static ClassLoader loader = ISession.class.getClassLoader();
	
	public static final Parcelable.Creator<ISession> CREATOR = new Parcelable.Creator<ISession>() {
		public ISession createFromParcel(Parcel in) {
			Class<?> clazz = (Class<?>) in.readSerializable();
			VBoxSvc vmgr =  in.readParcelable(loader);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, loader);
			return (ISession) vmgr.getProxy(clazz, id, cache); 
		}
		public ISession[] newArray(int size) {  
			return new ISession[size]; 
		}
	};

	public void unlockMachine() throws IOException;
	
	public IConsole getConsole() throws IOException;
	
	/**
	 * @cached false
	 * @return
	 * @throws IOException
	 */
	public SessionType getType() throws IOException;
	
	/**
	 * @cached false
	 * @return
	 * @throws IOException
	 */
	public SessionState getState() throws IOException;
}
