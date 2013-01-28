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
 */
public interface IVirtualBox extends IManagedObjectRef, Parcelable {
	static ClassLoader loader = IVirtualBox.class.getClassLoader();

	/** Object corresponding to the supplied arguments does not exist. */
	public static final long  	VBOX_E_OBJECT_NOT_FOUND = 0x80BB000;

	/** Current virtual machine state prevents the operation.  */
	public static final long  	VBOX_E_INVALID_VM_STATE = 0x80BB0002;

	/** Virtual machine error occurred attempting the operation. */
	public static final long  	VBOX_E_VM_ERROR = 0x80BB0003;

	/** File not accessible or erroneous file contents.  */
	public static final long  	VBOX_E_FILE_ERROR = 0x80BB0004;

	/** Runtime subsystem error.  */
	public static final long  	VBOX_E_IPRT_ERROR = 0x80BB0005;

	/** Pluggable Device Manager error. */
	public static final long  	VBOX_E_PDM_ERROR = 0x80BB0006;

	/** Current object state prohibits operation. */
	public static final long  	VBOX_E_INVALID_OBJECT_STATE = 0x80BB0007;

	/** Host operating system related error.  */
	public static final long  	VBOX_E_HOST_ERROR = 0x80BB0008;

	/** Requested operation is not supported.  */
	public static final long  	VBOX_E_NOT_SUPPORTED = 0x80BB0009;

	/** Invalid XML found. */
	public static final long  	VBOX_E_XML_ERROR = 0x80BB000A;
	 
	/** Current session state prohibits operation.  */
	public static final long  	VBOX_E_INVALID_SESSION_STATE = 0x80BB000B;

	/** Object being in use prohibits operation.  */
	public static final long  	VBOX_E_OBJECT_IN_USE = 0x80BB000C;


	public static final Parcelable.Creator<IVirtualBox> CREATOR = new Parcelable.Creator<IVirtualBox>() {
		public IVirtualBox createFromParcel(Parcel in) {
			VBoxSvc vmgr =  in.readParcelable(loader);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, loader);
			return (IVirtualBox) vmgr.getProxy(IVirtualBox.class, id, cache); 
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

	/**
	 * <p>Creates a new virtual machine by creating a machine settings file at the given location. </p>
	 * <p>VirtualBox machine settings files use a custom XML dialect. Starting with VirtualBox 4.0, a ".vbox" extension is recommended, but not enforced, and machine files can be created at arbitrary locations.</p>
	 * <p>However, it is recommended that machines are created in the default machine folder (e.g. "/home/user/VirtualBox VMs/name/name.vbox"; see {@link IVirtualBox#composeMachineFilename}<b></b> is 
	 * called automatically to have such a recommended name composed based on the machine name given in the <em>name</em> argument and the primary group.</p>
	 * <p>If the resulting settings file already exists, this method will fail, unless the forceOverwrite flag is set.</p>
	 * <p>The new machine is created unregistered, with the initial configuration set according to the specified guest OS type. A typical sequence of actions to create a new virtual machine is as follows:</p>
	 * <ol>
	 * <li>Call this method to have a new machine created. The returned machine object will be "mutable" allowing to change any machine property.  </li>
	 * <li>Configure the machine using the appropriate attributes and methods.  </li>
	 * <li>Call {@link IMachine#saveSettings}<b></b> to write the settings to the machine's XML settings file. The configuration of the newly created machine will not be saved to disk until this method is called.  </li>
	 * <li>Call {@link IVirtualBox#registerMachine}<b></b> to add the machine to the list of machines known to VirtualBox.  </li>
	 * </ol>
	 * <p>The specified guest OS type identifier must match an ID of one of known guest OS types listed in the {@link IVirtualBox#getGuestOSTypes}<b></b> array.</p>
	 *    @param   settingsFile</em>&nbsp;</td><td>Fully qualified path where the settings file should be created, empty string or <code>null</code> for a default folder and file based on the <em>name</em> 
	 *    argument and the primary group. (see {@link composeMachineFilename}<b></b>).</td></tr>
	 *     @param   name</em>&nbsp;</td><td>Machine name.</td></tr>
	 *     @param   groups</em>&nbsp;</td><td>Array of group names. <code>null</code> or an empty array have the same meaning as an array with just the empty string or <code>"/"</code>, i.e. create a machine 
	 *     without group association.</td></tr>
	 *     @param   osTypeId</em>&nbsp;</td><td>Guest OS Type ID.</td></tr>
	 *    @param   flags</em>&nbsp;</td><td>Additional property parameters, passed as a comma-separated list of "name=value" type entries. The following ones are recognized: <code>forceOverwrite=1</code> to 
	 *    overwrite an existing machine settings file, <code>UUID=&lt;uuid&gt;</code> to specify a machine UUID and <code>directoryIncludesUUID=1</code> to switch to a special VM directory naming scheme which 
	 *    should not be used unless necessary.</td></tr>
	 *     @return Created machine object.</td></tr>
	 * <dl class="user"><dt><b>Expected result codes:</b></dt><dd><table class="doxtable">
	 * <tbody><tr>
	 * <td>VBOX_E_OBJECT_NOT_FOUND</td><td><em>osTypeId</em> is invalid.   </td></tr>
	 * <tr>
	 * <td>VBOX_E_FILE_ERROR </td><td>Resulting settings file name is invalid or the settings file already exists or could not be created due to an I/O error.   </td></tr>
	 * <tr>
	 * <td>E_INVALIDARG </td><td><em>name</em> is empty or <code>null</code>.   </td></tr>
	 * </tbody></table>
	 * </dd></dl>
	 * <dl class="note"><dt><b>Note:</b></dt><dd>There is no way to change the name of the settings file or subfolder of the created machine directly. </dd></dl>
	 */
	public IMachine createMachine(@KSOAP("settingsFile") String settingsFiles, @KSOAP("name") String name, @KSOAP("osTypeId") String osTypeId, @KSOAP("flags") String flags, @KSOAP("groups") String...groups);

	/**
	 *<p>Registers the machine previously created using {@link openMachine}<b></b> within this VirtualBox installation. </p>
	 *<p>After successful method invocation, the {@link IMachineRegisteredEvent}<b></b> event is fired.</p>
	 *<dl class="user"><dt><b>Expected result codes:</b></dt><dd><table class="doxtable">
	 *<tbody><tr>
	 *<td>VBOX_E_OBJECT_NOT_FOUND</td><td>No matching virtual machine found.   </td></tr>
	 *<tr>
	 *<td>VBOX_E_INVALID_OBJECT_STATE</td><td>Virtual machine was not created within this VirtualBox instance.   </td></tr>
	 *</tbody></table>
	 *</dd></dl>
	 *<dl class="note"><dt><b>Note:</b></dt><dd>This method implicitly calls {@link IMachine#saveSettings}<b></b> to save all current machine settings before registering it. </dd></dl>
	 */
	public void registerMachine(@KSOAP("machine") IMachine machine);
}
