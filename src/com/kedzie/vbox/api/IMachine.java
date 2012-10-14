package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;
import android.service.textservice.SpellCheckerService.Session;

import com.kedzie.vbox.api.jaxb.ChipsetType;
import com.kedzie.vbox.api.jaxb.LockType;
import com.kedzie.vbox.api.jaxb.MachineState;
import com.kedzie.vbox.api.jaxb.SessionState;
import com.kedzie.vbox.api.jaxb.SessionType;
import com.kedzie.vbox.app.TreeNode;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * <p>The IMachine interface represents a virtual machine, or guest, created in VirtualBox.</p> 
<p>This interface is used in two contexts. First of all, a collection of objects implementing this interface is stored in the {@link IVirtualBox#getMachines()}} attribute which lists all the virtual machines that are currently registered with this VirtualBox installation. Also, once a session has been opened for the given virtual machine (e.g. the virtual machine is running), the machine object associated with the open session can be queried from the session object; see {@link ISession} for details.<p>
The main role of this interface is to expose the settings of the virtual machine and provide methods to change various aspects of the virtual machine's configuration. For machine objects stored in the {@link IVirtualBox#getMachines} collection, all attributes are read-only unless explicitly stated otherwise in individual attribute and method descriptions.<p>
In order to change a machine setting, a session for this machine must be opened using one of the {@link IMachine#lockMachine} or {@link IMachine#launchVMProcess} methods. After the machine has been successfully locked for a session, a mutable machine object needs to be queried from the session object and then the desired settings changes can be applied to the returned object using IMachine attributes and methods. See the {@link ISession} interface description for more information about sessions.<p>
Note that {@link IMachine} does not provide methods to control virtual machine execution (such as start the machine, or power it down) -- these methods are grouped in a separate interface called {@link IConsole}.<p>
<dl><dt><b>See also:</b></dt><dd>{@link ISession}, {@link IConsole}</dd></dl>
<dl><dt><b>Interface ID:</b></dt><dd><code>{5EAA9319-62FC-4B0A-843C-0CB1940F8A91}</code> </dd></dl>
 */
public interface IMachine extends IManagedObjectRef, TreeNode {
	public static String BUNDLE = "machine";
	static final ClassLoader LOADER = IMachine.class.getClassLoader();
	
	public static final Parcelable.Creator<IMachine> CREATOR = new Parcelable.Creator<IMachine>() {
		public IMachine createFromParcel(Parcel in) {
			Class<?> clazz = (Class<?>) in.readSerializable();
			VBoxSvc vmgr =  in.readParcelable(LOADER);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, LOADER);
			return (IMachine) vmgr.getProxy(clazz, id, cache); 
		}
		public IMachine[] newArray(int size) {  
			return new IMachine[size]; 
		}
	};
	
	public static enum LaunchMode { headless, gui; }
	
	 /**
	  * @return Name of the virtual machine. 
	  */
	 @KSOAP(cacheable=true) public String getName() ;
	 
	 public void setName(@KSOAP() String name);

	 /**
	  * @return Description of the virtual machine. 
	  */
	 @KSOAP(cacheable=true) public String getDescription();
	 
	 public void setDescription(@KSOAP() String description);

	/**
	 * @return UUID of the virtual machine. 
	 */
	@KSOAP(cacheable=true) public String getId();
	
	/**
	 * @return User-defined identifier of the Guest OS type. 
	 */
	@KSOAP(cacheable=true) public String getOSTypeId();
	
	/**
	 * @return Number of virtual CPUs in the VM. 
	 */
	@KSOAP(cacheable=true) public Integer getCPUCount();
	
	//public void setCPUCount(@KSOAP() int cpuCount());
	
	
	/**
	 * This setting determines whether VirtualBox allows CPU hotplugging for this machine. 
	 */
	@KSOAP(cacheable=true) public Boolean getCPUHotPlugEnabled();
	
	/**
	 * @return Number of virtual CPUs in the VM. 
	 */
	@KSOAP(cacheable=true) public Integer getCPUExecutionCap();

	/**
	 * @return System memory size in megabytes. 
	 */ 
	@KSOAP(cacheable=true) public Integer getMemorySize();
	
	/**
	 * @return Memory balloon size in megabytes. 
	 */
	@KSOAP(cacheable=true) public Integer getMemoryBalloonSize();

	/**
	 * @return Video memory size in megabytes. 
	 */
	@KSOAP(cacheable=true) public Integer getVRAMSize();
	
	/**
	 * @return This setting determines whether VirtualBox allows this machine to make use of the 3D graphics support available on the host. 
	 */
	@KSOAP(cacheable=true) public Boolean getAccelerate3DEnabled();
	
	/**
	 * @return This setting determines whether VirtualBox allows this machine to make use of the 2D video acceleration support available on the host. 
	 */
	@KSOAP(cacheable=true) public Boolean getAccelerate2DVideoEnabled();

	/**
	 * @return Number of virtual monitors. 
	 */
	@KSOAP(cacheable=true) public Integer getMonitorCount();

	/**
	 * @return This attribute controls if High Precision Event Timer (HPET) is enabled in this VM. 
	 */
	@KSOAP(cacheable=true) public Boolean getHpetEnabled();
 	
	/**
	 * @return  Chipset type used in this VM. 
	 */
	@KSOAP(cacheable=true) public  ChipsetType 	getChipsetType();
 	
 	/**
	 * @return  Full path to the directory used to store snapshot data (differencing media and saved state files) of this machine. 
	 */
	@KSOAP(cacheable=true) public String getSnapshotFolder();
 	
// 	/**
//	 * @return  VirtualBox Remote Desktop Extension (VRDE) server object. 
//	 */
//	@KSOAP(cacheable=true) public  IVRDEServer 	getVRDEServer();
	
//  /**
//  * @return Object containing all BIOS settings. 
//  */
// @KSOAP(cacheable=true) public BIOSSettings getBIOSSettings();
	
 	/**
 	 * @return Current session state for this machine. 
 	 */
 	@KSOAP(cacheable=true) public SessionState getSessionState();

	/**
	 * @return  Type of the session. 
	 */
	@KSOAP(cacheable=true) public SessionType getSessionType();
	
	/**
	 * @return Identifier of the session process. 
	 */
	@KSOAP(cacheable=true) public Integer getSessionPid();
	
	/**
	 * @return Current execution state of this machine. 
	 */
	@KSOAP(cacheable=true) public MachineState getState() ;

	/**
	 * @return Number of snapshots taken on this machine. 
	 */
	@KSOAP(cacheable=true) public  Long getSnapshotCount();
	
	/**
	 * @return	Current snapshot of this machine. 
	 */
	@KSOAP(cacheable=true) public ISnapshot getCurrentSnapshot();
 	
	/**
	 * @return Returns true if the current state of the machine is not identical to the state stored in the current snapshot. 
	 */
	@KSOAP(cacheable=true) public Boolean getCurrentStateModified();

	
	/**
	 * <p>Locks the machine for the given session to enable the caller to make changes to the machine or start the VM or control VM execution.</p> 
	 * <p>There are two ways to lock a machine for such uses:<p>
	 * <ul>
	 * <li>If you want to make changes to the machine settings, you must obtain an exclusive write lock on the machine by setting {@link LockType} to {@link LockType.Write}.<p>
	 * This will only succeed if no other process has locked the machine to prevent conflicting changes. Only after an exclusive write lock has been obtained using this method, one can change all VM settings or execute the VM in the process space of the session object. (Note that the latter is only of interest if you actually want to write a new front-end for virtual machines; but this API gets called internally by the existing front-ends such as VBoxHeadless and the VirtualBox GUI to acquire a write lock on the machine that they are running.)<p>
	 * On success, write-locking the machine for a session creates a second copy of the IMachine object. It is this second object upon which changes can be made; in VirtualBox terminology, the second copy is "mutable". It is only this second, mutable machine object upon which you can call methods that change the machine state. After having called this method, you can obtain this second, mutable machine object using the {@link ISession#getMachine} attribute.  </li>
	 * <li> If you only want to check the machine state or control machine execution without actually changing machine settings (e.g. to get access to VM statistics or take a snapshot or save the machine state), then set the {@link LockType} argument to <code>Shared</code>.<p>
	 * If no other session has obtained a lock, you will obtain an exclusive write lock as described above. However, if another session has already obtained such a lock, then a link to that existing session will be established which allows you to control that existing session.<p>
	 * To find out which type of lock was obtained, you can inspect {@link ISession#getType}, which will have been set to either {@link LockType.WriteLock} or {@link LockType.Shared}.  </li>
	 * </ul>
	 * <p>In either case, you can get access to the {@link IConsole} object which controls VM execution.<p>
	 * Also in all of the above cases, one must always call {@link ISession#unlockMachine} to release the lock on the machine, or the machine's state will eventually be set to "Aborted".<p>
	 * To change settings on a machine, the following sequence is typically performed:<p>
	 * <ol>
	 * <li>Call this method to obtain an exclusive write lock for the current session. </li>
	 * <li>Obtain a mutable {@link IMachine}</a> object from {@link ISession#getMachine}. </li>
	 * <li>Change the settings of the machine by invoking {@link IMachine}</a> methods. </li>
	 * <li>Call {@link IMachine#saveSettings}. </li>
	 * <li>Release the write lock by calling {@link ISession#unlockMachine}. </li>
	 * </ol>
	 * <dl><dt><b>Expected result codes:</b></dt><dd><table border="1" cellspacing="3" cellpadding="3">
	 * <tr><td>E_UNEXPECTED</td><td>Virtual machine not registered.   </td></tr>
	 * <tr><td>E_ACCESSDENIED</td><td>Process not started by OpenRemoteSession.   </td></tr>
	 * <tr><td>>VBOX_E_INVALID_OBJECT_STATE</td><td>Session already open or being opened.   </td></tr>
	 * <tr><td>VBOX_E_VM_ERROR </td><td>Failed to assign machine to session.   </td></tr>
	 * </table>
	 * </dd></dl>
	 * @param s  Session object for which the machine will be locked.
	 * @param lockType	If set to {@link LockType.Write}, then attempt to acquire an exclusive write lock or fail. If set to {@link LockType.Shared}, then either acquire an exclusive write lock or establish a link to an existing session.
	 * @throws IOException
	 */
	public void lockMachine(@KSOAP("session")ISession s, @KSOAP("lockType")LockType lockType) throws IOException;
	
	/**
	 * <p>Spawns a new process that will execute the virtual machine and obtains a shared lock on the machine for the calling session. 
	 * <p>If launching the VM succeeds, the new VM process will create its own session and write-lock the machine for it, preventing conflicting changes from other processes. If the machine is already locked (because it is already running or because another session has a write lock), launching the VM process will therefore fail. Reversely, future attempts to obtain a write lock will also fail while the machine is running.<p>
	 * The caller's session object remains separate from the session opened by the new VM process. It receives its own {@link IConsole} object which can be used to control machine execution, but it cannot be used to change all VM settings which would be available after a {@link IMachine#lockMachine} call.<p>
	 * The caller must eventually release the session's shared lock by calling {@link ISession#unlockMachine} on the local session object once this call has returned. However, the session's state {@link Session#getState}) will not return to {@link SessionState.Unlocked} until the remote session has also unlocked the machine (i.e. the machine has stopped running).<p>
	 * Launching a VM process can take some time (a new VM is started in a new process, for which memory and other resources need to be set up). Because of this, an {@link IProgress} object is returned to allow the caller to wait for this asynchronous operation to be completed. Until then, the caller's session object remains in the {@link ISessionState#Unlocked} state, and its {@link ISession#getMachine} and {@link ISession#getConsole} attributes cannot be accessed. It is recommended to use {@link IProgress#waitForCompletion} or similar calls to wait for completion. Completion is signalled when the VM is powered on. If launching the VM fails, error messages can be queried via the progress object, if available.<p>
	 * The progress object will have at least 2 sub-operations. The first operation covers the period up to the new VM process calls powerUp. The subsequent operations mirror the {@link IConsole#powerUp} progress object. Because {@link IConsole#powerUp} may require some extra sub-operations, the {@link IProgress#getOperationCount} may change at the completion of operation.<p>
	 * For details on the teleportation progress operation, see {@link IConsole#powerUp}.<p>
	 * The <em>environment</em> argument is a string containing definitions of environment variables in the following format: 
	 * <pre>
	 *      NAME[=VALUE]<br>
	 *      NAME[=VALUE]<br>
	 *      ...
	 * </pre> where <code>\n</code> is the new line character. These environment variables will be appended to the environment of the VirtualBox server process. If an environment variable exists both in the server process and in this list, the value from this list takes precedence over the server's variable. If the value of the environment variable is omitted, this variable will be removed from the resulting environment. If the environment string is <code>null</code> or empty, the server environment is inherited by the started process as is.<p>
	 * <dl class="user" compact><dt><b>Expected result codes:</b></dt><dd><table border="1" cellspacing="3" cellpadding="3">
	 * <tr><td>E_UNEXPECTED </td><td>Virtual machine not registered. </td></tr>
	 * <tr><td>E_INVALIDARG </td><td>Invalid session type <em>type</em>. </td></tr>
	 * <tr><td>VBOX_E_OBJECT_NOT_FOUND</td><td>No machine matching <em>machineId</em> found.   </td></tr>
	 * <tr><td>VBOX_E_INVALID_OBJECT_STATE </a> </td><td>Session already open or being opened. </td></tr>
	 * <tr><td>VBOX_E_IPRT_ERRORLaunching process for machine failed.</td></tr>
	 * <tr><td>VBOX_E_VM_ERROR Failed to assign machine to session.</td></tr>
	 * </table>
	 * </dd></dl>
	 * @param session	Client session object to which the VM process will be connected (this must be in {@link SessionState.Unlocked} state).
	 * @param type	Front-end to use for the new VM process. The following are currently supported: <ul>
	 * <li><code>"gui"</code>: VirtualBox Qt GUI front-end </li>
	 * <li><code>"headless"</code>: VBoxHeadless (VRDE Server) front-end </li>
	 * <li><code>"sdl"</code>: VirtualBox SDL front-end </li>
	 * <li><code>"emergencystop"</code>: reserved value, used for aborting the currently running VM or session owner. In this case the <em>session</em> parameter may be <code>NULL</code> (if it is non-null it isn't used in any way), and the <em>progress</em> return value will be always NULL. The operation completes immediately. </li>
	 * </ul>	
	 * @param environment	Environment to pass to the VM process. 
	 * @return		Progress object to track the operation completion.
	 * @throws IOException
	 */
	public IProgress launchVMProcess(@KSOAP("session")ISession session, @KSOAP("type") LaunchMode type) throws IOException;
	
	public Map<String, List<String>> querySavedThumbnailSize(@KSOAP(type="unsignedInt", value="screenId") int screenId);
	
	public Map<String, List<String>> readSavedThumbnailPNGToArray(@KSOAP(type="unsignedInt", value="screenId") int screenId);
	
	public Map<String, List<String>> querySavedScreenshotPNGSize(@KSOAP(type="unsignedInt", value="screenId") int screenId);
	
	@Deprecated
	public Map<String, List<String>> readSavedScreenshotPNGToArray(@KSOAP(type="unsignedInt", value="screenId") int screenId);
	
	public String queryLogFilename(@KSOAP(type="unsignedInt", value="idx") int idx);
	
	public byte[] readLog(@KSOAP(type="unsignedInt", value="idx") int idx, @KSOAP(type="long", value="offset") long offset, @KSOAP(type="long", value="size") long size);
	
	/**
	 * <p>Array of machine group names of which this machine is a member. </p>
	 * <p><code>""</code> and <code>"/"</code> are synonyms for the toplevel group. Each group is only listed once, however they are listed in no particular order and there is no guarantee that there are no gaps in the group hierarchy (i.e. <code>"/group"</code>, <code>"/group/subgroup/subsubgroup"</code> is a valid result). </p>
	 */
	@KSOAP(cacheable=true) public List<String> getGroups();
}
