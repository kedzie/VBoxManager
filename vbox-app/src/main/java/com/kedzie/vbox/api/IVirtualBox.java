package com.kedzie.vbox.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.jaxb.AccessMode;
import com.kedzie.vbox.api.jaxb.DeviceType;
import com.kedzie.vbox.api.jaxb.IGuestOSType;
import com.kedzie.vbox.api.jaxb.MachineState;
import com.kedzie.vbox.soap.Asyncronous;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * The {@link IVirtualBox} interface represents the main interface exposed by the product that provides virtual machine management. 
 * <p>An instance of {@link IVirtualBox} is required for the product to do anything useful. Even though the interface does not expose this, internally, {@link IVirtualBox} is implemented as a singleton and actually lives in the process of the VirtualBox server <code>(VBoxSVC.exe)</code>. This makes sure that {@link IVirtualBox} can track the state of all virtual machines on a particular host, regardless of which frontend started them.
 * <p>To enumerate all the virtual machines on the host, use the {@link IVirtualBox#getMachines} attribute.
 */
@KSOAP
public interface IVirtualBox extends IManagedObjectRef, Parcelable {
	static ClassLoader loader = IVirtualBox.class.getClassLoader();
    public static final String BUNDLE = "vbox";

	/** Object corresponding to the supplied arguments does not exist. */
	public static final long VBOX_E_OBJECT_NOT_FOUND = 0x80BB000;
	/** Current virtual machine state prevents the operation.  */
	public static final long VBOX_E_INVALID_VM_STATE = 0x80BB0002;
	/** Virtual machine error occurred attempting the operation. */
	public static final long VBOX_E_VM_ERROR = 0x80BB0003;
	/** File not accessible or erroneous file contents.  */
	public static final long VBOX_E_FILE_ERROR = 0x80BB0004;
	/** Runtime subsystem error.  */
	public static final long VBOX_E_IPRT_ERROR = 0x80BB0005;
	/** Pluggable Device Manager error. */
	public static final long VBOX_E_PDM_ERROR = 0x80BB0006;
	/** Current object state prohibits operation. */
	public static final long VBOX_E_INVALID_OBJECT_STATE = 0x80BB0007;
	/** Host operating system related error.  */
	public static final long VBOX_E_HOST_ERROR = 0x80BB0008;
	/** Requested operation is not supported.  */
	public static final long VBOX_E_NOT_SUPPORTED = 0x80BB0009;
	/** Invalid XML found. */
	public static final long VBOX_E_XML_ERROR = 0x80BB000A;
	/** Current session state prohibits operation.  */
	public static final long VBOX_E_INVALID_SESSION_STATE = 0x80BB000B;
	/** Object being in use prohibits operation.  */
	public static final long VBOX_E_OBJECT_IN_USE = 0x80BB000C;

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
	@KSOAP(cacheable=true) public String getVersionNormalized();
	@KSOAP(cacheable=true) public String getRevision();
	@KSOAP(cacheable=true) public String getPackageType();
	@KSOAP(cacheable=true) public String getAPIVersion();
	@KSOAP(cacheable=true) public String getHomeFolder();
	@KSOAP(cacheable=true) public String getSettingsFilePath();
	@KSOAP(cacheable=true) public String[] getInternalNetworks();
	@KSOAP(cacheable=true) public String[] getGenericNetworkDrivers();

	@KSOAP(cacheable=true) public IEventSource getEventSource() ;
	@KSOAP(cacheable=true) public IPerformanceCollector getPerformanceCollector();
	@KSOAP(cacheable=true) public IHost getHost() ;
	@KSOAP(cacheable=true)  ISystemProperties getSystemProperties();

	@KSOAP(prefix="IWebsessionManager", thisReference="") public IVirtualBox logon(@KSOAP("username") String username, @KSOAP("password") String password) throws IOException;

	@KSOAP(prefix="IWebsessionManager", thisReference="refIVirtualBox") public void logoff() throws IOException;

	@KSOAP(cacheable=true, prefix="IWebsessionManager", thisReference="refIVirtualBox") public ISession getSessionObject() throws IOException;

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
	 * <dl><dt><b>Expected result codes:</b></dt><dd><table>
	 * <tbody><tr><td>VBOX_E_OBJECT_NOT_FOUND</td><td><em>osTypeId</em> is invalid.   </td></tr>
	 * <tr><td>VBOX_E_FILE_ERROR </td><td>Resulting settings file name is invalid or the settings file already exists or could not be created due to an I/O error.   </td></tr>
	 * <tr><td>E_INVALIDARG </td><td><em>name</em> is empty or <code>null</code>.   </td></tr></tbody></table></dd></dl>
	 * <dl class="note"><dt><b>Note:</b></dt><dd>There is no way to change the name of the settings file or subfolder of the created machine directly. </dd></dl>
	 */
	public IMachine createMachine(@KSOAP("settingsFile") String settingsFile, @KSOAP("name") String name, @KSOAP("osTypeId") String osTypeId, @KSOAP("flags") String flags, @KSOAP("groups") String...groups) throws IOException;

	/**
	 *<p>Registers the machine previously created using {@link openMachine}<b></b> within this VirtualBox installation. </p>
	 *<p>After successful method invocation, the {@link IMachineRegisteredEvent}<b></b> event is fired.</p>
	 *<dl><dt><b>Expected result codes:</b></dt><dd><table>
	 *<tbody><tr>
	 *<td>{@link IVirtualBox#VBOX_E_OBJECT_NOT_FOUND}</td><td>No matching virtual machine found.   </td></tr>
	 *<tr><td>{@link IVirtualBox#VBOX_E_INVALID_OBJECT_STATE}</td><td>Virtual machine was not created within this VirtualBox instance.   </td></tr>
	 *</tbody></table></dd></dl>
	 *<dl><dt><b>Note:</b></dt><dd>This method implicitly calls {@link IMachine#saveSettings}<b></b> to save all current machine settings before registering it. </dd></dl>
	 */
	public void registerMachine(@KSOAP("machine") IMachine machine) throws IOException;
	
	@KSOAP(cacheable=true) public ArrayList<IGuestOSType> getGuestOSTypes() throws IOException;
	
	/**
	 * <p>Returns an object describing the specified guest OS type. </p>
	 * <p>The requested guest OS type is specified using a string which is a mnemonic identifier of the guest operating system, such as <code>win31</code> or <code>ubuntu</code>. 
	 * The guest OS type ID of a particular virtual machine can be read or set using the {@link IMachine#getOSTypeId} attribute.</p>
	 * <p>The {@link IVirtualBox#getGuestOSTypes} collection contains all available guest OS type objects. Each object has an
	 * {@link IGuestOSType#getId} attribute which contains an identifier of the guest OS this object describes.</p>
	 * <dl><dt><b>Expected result codes:</b></dt><dd><table>
	 * <tbody><tr><td>{@link IVirtualBox#E_INVALIDARG}</td><td><code>id</code> is not a valid Guest OS type.  </td></tr>
	 * </tbody></table></dd></dl>
	 * @param id		Guest OS type ID string.
	 * @return			Guest OS type object.
	 */
	@KSOAP(cacheable=true) public IGuestOSType getGuestOSType(@KSOAP("id") String id) throws IOException;
	
	@KSOAP(cacheable=true) public ArrayList<IProgress> getProgressOperations() throws IOException;
	
	/**
	 * <p>Creates a new base medium object that will use the given storage format and location for medium data. </p>
	 * <p>The actual storage unit is not created by this method. In order to do it, and before you are able to attach the created medium to virtual machines, you must call one of the following methods to allocate a format-specific storage unit at the specified location: </p>
	 * <ul><li>{@link IMedium#createBaseStorage}</li>
	 * <li>{@link IMedium#createDiffStorage}</li></ul>
	 * <p>Some medium attributes, such as {@link IMedium#getId}, may remain uninitialized until the medium storage unit is successfully created by one of the above methods.</p>
	 * <p>After the storage unit is successfully created, it will be accessible through the {@link IVirtualBox#getHardDisks} array.</p>
	 * <p>The list of all storage formats supported by this VirtualBox installation can be obtained using {@link ISystemProperties#getDefaultHardDiskFormat} will be used for creating a storage unit of the medium.</p>
	 * <p>Note that the format of the location string is storage format specific. See {@link IMedium} for more details.</p>
	 * <dl><dt><b>Expected result codes:</b></dt><dd><table>
	 * <tbody><tr><td>{@link ISystemProperties#getMediumFormats}.</td></tr>
	 * <tr><td>{@link IVirtualBox#VBOX_E_FILE_ERROR } </td><td><em>location</em> is a not valid file name (for file-based formats only).</td></tr>
	 * </tbody></table></dd></dl>
	 * @param format			Identifier of the storage format to use for the new medium.
	 * @param location			Location of the storage unit for the new medium.
	 * @return		Created medium object.
	 */
	public IMedium createHardDisk(@KSOAP("format") String format, @KSOAP("location") String location) throws IOException;
	
	/**
	 * <p>Finds existing media or opens a medium from an existing storage location. </p>
	 * <p>Once a medium has been opened, it can be passed to other VirtualBox methods, in particular to {@link IMachine#attachDevice}.</p>
	 * <p>Depending on the given device type, the file at the storage location must be in one of the media formats understood by <code>VirtualBox</code>:</p>
	 * <ul>
	 * <li>With a <em>HardDisk</em> device type, the file must be a hard disk image in one of the formats supported by <code>VirtualBox</code> 
	 * (see {@link ISystemProperties#getMediumFormats}). After this method succeeds, if the medium is a base medium, it will be added to the 
	 * {@link IVirtualBox#getHardDisks} array attribute.  </li>
	 * <li>With a <em>DVD</em> device type, the file must be an ISO 9960 CD/DVD image. After this method succeeds, the medium will be added to the {@link IVirtualBox#getDVDImages} array attribute. </li>
	 * <li>With a <em>Floppy</em> device type, the file must be an RAW floppy image. After this method succeeds, the medium will be added to the {@link IVirtualBox#getFloppyImages} array attribute. </li>
	 * </ul>
	 * <p>After having been opened, the medium can be re-found by this method and can be attached to virtual machines. See {@link IMedium} for more details.</p>
	 * <p>The UUID of the newly opened medium will either be retrieved from the storage location, if the format supports it (e.g. for hard disk images), or a new UUID will be randomly 
	 * generated (e.g. for ISO and RAW files). If for some reason you need to change the medium's UUID, use {@link IMedium::setIds}.</p>
	 * <p>If a differencing hard disk medium is to be opened by this method, the operation will succeed only if its parent medium and all ancestors, if any, are already known to this VirtualBox 
	 * installation (for example, were opened by this method before).</p>
	 * <p>This method attempts to guess the storage format of the specified medium by reading medium data at the specified location.</p>
	 * <p>If <em>accessMode</em> is ReadWrite (which it should be for hard disks and floppies), the image is opened for read/write access and must have according permissions, as VirtualBox may 
	 * actually write status information into the disk's metadata sections.</p>
	 * <p>Note that write access is required for all typical hard disk usage in VirtualBox, since VirtualBox may need to write metadata such as a UUID into the image. The only exception is opening a 
	 * source image temporarily for copying and cloning (see {@link IMedium::cloneTo} when the image will be closed again soon.</p>
	 * <p>The format of the location string is storage format specific. See {@link IMedium#getLocation} and {@link IMedium} for more details.</p>
	 * 
	 * <dl><dt><h4>Expected result codes:</h4></dt><dd><table>
	 * <tbody><tr><td>{@link IVirtualBox#VBOX_E_FILE_ERROR}</td><td>Invalid medium storage file location or could not find the medium at the specified location.   </td></tr>
	 * <tr><td>{@link IVirtualBox#VBOX_E_IPRT_ERROR}</td><td>Could not get medium storage format.   </td></tr>
	 * <tr><td>{@link IVirtualBox#E_INVALIDARG}</td><td>Invalid medium storage format.   </td></tr>
	 * <tr><td>{@link IVirtualBox#VBOX_E_INVALID_OBJECT_STATE}</td><td>Medium has already been added to a media registry.   </td></tr>
	 * </tbody></table></dd></dl>
	 * @param location					Location of the storage unit that contains medium data in one of the supported storage formats.
	 * @param deviceType			Must be one of "HardDisk", "DVD" or "Floppy".
	 * @param accessMode			Whether to open the image in read/write or read-only mode. For a "DVD" device type, this is ignored and read-only mode is always assumed.
	 * @param forceNewUuid		Allows the caller to request a completely new medium UUID for the image which is to be opened. Useful if one intends to open an exact copy of a previously opened image, as this would normally fail due to the duplicate UUID.
	 * @return								Opened medium object
	 */
	public IMedium openMedium(@KSOAP("location") String location, @KSOAP("deviceType") DeviceType deviceType, @KSOAP("accessMode") AccessMode accessMode, @KSOAP("forceNewUuid") boolean forceNewUuid) throws IOException;
	
	/**
	 * @return	Array of medium objects known to this VirtualBox installation.
	 */
	@KSOAP(cacheable=true) public List<IMedium> getHardDisks() throws IOException;
	
	/**
	 * @return	Array of CD/DVD image objects currently in use by this VirtualBox instance. 
	 */
	@KSOAP(cacheable=true) public List<IMedium> getDVDImages() throws IOException;
	
	/**
	 * @return	Array of floppy image objects currently in use by this VirtualBox instance. 
	 */
	@KSOAP(cacheable=true) public List<IMedium> getFloppyImages() throws IOException;
	
	/**
	 * @return DHCP Servers
	 */
	@KSOAP(cacheable=true) public List<IDHCPServer> getDHCPServers() throws IOException;
	
	/**
	 * Searches a DHCP server settings to be used for the given internal network name. 
	 * <p><dl><dt><b>Expected result codes:</b></dt><dd><table><tbody><tr>
	 * <td>{@link IVirtualBox#E_INVALIDARG}</td><td>Host network interface <em>name</em> already exists.  </td></tr>
	 * </tbody></table></dd></dl></p>
	 * @param name		server name
	 * @param server	DHCP server settings
	 */
	@KSOAP(cacheable=true) public IDHCPServer findDHCPServerByNetworkName(@KSOAP("name") String name) throws IOException;
	
	@Asyncronous public void removeDHCPServer(@KSOAP("server") IDHCPServer server) throws IOException;
	
	/**
	 * <p>Returns associated global extra data. </p>
	 * <p>If the requested data <code>key</code> does not exist, this function will succeed and return an empty string in the <code>value</code> argument.</p>
	 * <dl><dt><b>Expected result codes:</b></dt><dd><table>
	 * <tbody><tr><td>{@link IVirtualBox#VBOX_E_FILE_ERROR}</td><td>Settings file not accessible.</td></tr>
	 * <tr><td>{@link IVirtualBox#VBOX_E_XML_ERROR}</td><td>Could not parse the settings file.</td></tr>
	 * </tbody></table></dd></dl>
	 * @param key		Name of the data key to get.
	 * @return			Value of the requested data key.
	 */
	@KSOAP(cacheable=true) String getExtraData(@KSOAP("key") String key) throws IOException;
	
	@Asyncronous public void setExtraData(@KSOAP("key") String key, @KSOAP("value") String value) throws IOException;
	
	@KSOAP(cacheable=true) List<String> getExtraDataKeys() throws IOException;
	
	public IDHCPServer createDHCPServer(@KSOAP("name") String networkName) throws IOException;
	
	@Asyncronous public void setSettingsSecret(@KSOAP("password") String password) throws IOException;
	
	public void createSharedFolder(@KSOAP("name") String name, @KSOAP("hostPath") String hostPath, @KSOAP("writeable") boolean writeable, @KSOAP("automount") boolean automount) throws IOException;
	
	public void removeSharedFolder(@KSOAP("name") String name) throws IOException;
	
//	@KSOAP(cacheable=true) List<ISharedFolder> getSharedFolders();
	
//	public IAppliance createAppliance();
	
	public List<MachineState> getMachineStates(@KSOAP("machines") List<IMachine> machines) throws IOException;
	
	public IMachine openMachine(@KSOAP("settingsFile") String settingsFile) throws IOException;

	@KSOAP(cacheable=true) public List<INATNetwork> getNATNetworks() throws IOException;

	public INATNetwork createNATNetwork(@KSOAP("networkName") String networkName) throws IOException;

	@KSOAP(cacheable=true) public INATNetwork findNATNetworkByName(@KSOAP("networkName") String networkName) throws IOException;

	public void removeNATNetwork(@KSOAP("network") INATNetwork network) throws IOException;
}
