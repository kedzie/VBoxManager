package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.*
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy
import org.ksoap2.SoapFault
import timber.log.Timber


@KsoapProxy
@Ksoap
interface IVirtualBox : IManagedObjectRef, Parcelable {

    companion object {
        const val BUNDLE = "vbox"
        /** Object corresponding to the supplied arguments does not exist.  */
        const val VBOX_E_OBJECT_NOT_FOUND: Long = 0x80BB000
        /** Current virtual machine state prevents the operation.   */
        const val VBOX_E_INVALID_VM_STATE: Long = -0x7f44fffe
        /** Virtual machine error occurred attempting the operation.  */
        const val VBOX_E_VM_ERROR: Long = -0x7f44fffd
        /** File not accessible or erroneous file contents.   */
        const val VBOX_E_FILE_ERROR: Long = -0x7f44fffc
        /** Runtime subsystem error.   */
        const val VBOX_E_IPRT_ERROR: Long = -0x7f44fffb
        /** Pluggable Device Manager error.  */
        const val VBOX_E_PDM_ERROR: Long = -0x7f44fffa
        /** Current object state prohibits operation.  */
        const val VBOX_E_INVALID_OBJECT_STATE: Long = -0x7f44fff9
        /** Host operating system related error.   */
        const val VBOX_E_HOST_ERROR: Long = -0x7f44fff8
        /** Requested operation is not supported.   */
        const val VBOX_E_NOT_SUPPORTED: Long = -0x7f44fff7
        /** Invalid XML found.  */
        const val VBOX_E_XML_ERROR: Long = -0x7f44fff6
        /** Current session state prohibits operation.   */
        const val VBOX_E_INVALID_SESSION_STATE: Long = -0x7f44fff5
        /** Object being in use prohibits operation.   */
        const val VBOX_E_OBJECT_IN_USE: Long = -0x7f44fff4
    }

    @Cacheable("Version")
	suspend fun getVersion(): String
    @Cacheable("VersionNormalized")
	suspend fun getVersionNormalized(): String
    @Cacheable("Revision")
	suspend fun getRevision(): String
    @Cacheable("PackageType")
	suspend fun getPackageType(): String
    @Cacheable("APIVersion")
	suspend fun getAPIVersion(): String
    @Cacheable("HomeFolder")
	suspend fun getHomeFolder(): String
    @Cacheable("SettingsFilePath")
	suspend fun getSettingsFilePath(): String
    @Cacheable("InternalNetworks")
	suspend fun getInternalNetworks(): Array<String>
    @Cacheable("GenericNetworkDrivers")
	suspend fun getGenericNetworkDrivers(): Array<String>

    @Cacheable("EventSource")
	suspend fun getEventSource(): IEventSource
    @Cacheable("PerformanceCollector")
	suspend fun getPerformanceCollector(): IPerformanceCollector
    @Cacheable("Host")
	suspend fun getHost(): IHost
    @Cacheable("SystemProperties")
	suspend fun getSystemProperties(): ISystemProperties


    @Ksoap(prefix = "IWebsessionManager", thisReference = "")
    suspend fun logon(username: String, password: String): IVirtualBox

    @Ksoap(prefix = "IWebsessionManager", thisReference = "refIVirtualBox")
    suspend fun logoff()

    @Ksoap(cacheable = true, prefix = "IWebsessionManager", thisReference = "refIVirtualBox")
    suspend fun getSessionObject(): ISession

    suspend fun getMachines(): List<IMachine>

    suspend fun findMachine(nameOrId: String): IMachine

    /**
     *
     * Array of all machine group names which are used by the machines which are accessible.
     *
     * Each group is only listed once, however they are listed in no particular order and there is no guarantee that there are no gaps in the group hierarchy (i.e. `"/"`, `"/group/subgroup"` is a valid result).
     */
    suspend fun getMachineGroups(): List<String>

    /**
     * Gets all machine references which are in one of the specified groups.
     * @param groups       What groups to match. The usual group list rules apply, i.e. passing an empty list will match VMs in the toplevel group, likewise the empty string.
     * @return     All machines which matched.
     */
    suspend fun getMachinesByGroups(groups: Array<String>): List<IMachine>

    /**
     *
     * Creates a new virtual machine by creating a machine settings file at the given location.
     *
     * VirtualBox machine settings files use a custom XML dialect. Starting with VirtualBox 4.0, a ".vbox" extension is recommended, but not enforced, and machine files can be created at arbitrary locations.
     *
     * However, it is recommended that machines are created in the default machine folder (e.g. "/home/user/VirtualBox VMs/name/name.vbox"; see [IVirtualBox.composeMachineFilename]**** is
     * called automatically to have such a recommended name composed based on the machine name given in the *name* argument and the primary group.
     *
     * If the resulting settings file already exists, this method will fail, unless the forceOverwrite flag is set.
     *
     * The new machine is created unregistered, with the initial configuration set according to the specified guest OS type. A typical sequence of actions to create a new virtual machine is as follows:
     *
     *  1. Call this method to have a new machine created. The returned machine object will be "mutable" allowing to change any machine property.
     *  1. Configure the machine using the appropriate attributes and methods.
     *  1. Call [IMachine.saveSettings]**** to write the settings to the machine's XML settings file. The configuration of the newly created machine will not be saved to disk until this method is called.
     *  1. Call [IVirtualBox.registerMachine]**** to add the machine to the list of machines known to VirtualBox.
     *
     *
     * The specified guest OS type identifier must match an ID of one of known guest OS types listed in the [IVirtualBox.getGuestOSTypes]**** array.
     * @param   settingsFile&nbsp;<td>Fully qualified path where the settings file should be created, empty string or `null` for a default folder and file based on the *name*
     * argument and the primary group. (see [composeMachineFilename]****).</td>
     * @param   name&nbsp;<td>Machine name.</td>
     * @param   groups&nbsp;<td>Array of group names. `null` or an empty array have the same meaning as an array with just the empty string or `"/"`, i.e. create a machine
     * without group association.</td>
     * @param   osTypeId&nbsp;<td>Guest OS Type ID.</td>
     * @param   flags&nbsp;<td>Additional property parameters, passed as a comma-separated list of "name=value" type entries. The following ones are recognized: `forceOverwrite=1` to
     * overwrite an existing machine settings file, `UUID=<uuid>` to specify a machine UUID and `directoryIncludesUUID=1` to switch to a special VM directory naming scheme which
     * should not be used unless necessary.</td>
     * @return Created machine object.
     * <dl><dt>**Expected result codes:**</dt><dd><table>
     * <tbody><tr><td>VBOX_E_OBJECT_NOT_FOUND</td><td>*osTypeId* is invalid.   </td></tr>
     * <tr><td>VBOX_E_FILE_ERROR </td><td>Resulting settings file name is invalid or the settings file already exists or could not be created due to an I/O error.   </td></tr>
     * <tr><td>E_INVALIDARG </td><td>*name* is empty or `null`.   </td></tr></tbody></table></dd></dl>
     * <dl class="note"><dt>**Note:**</dt><dd>There is no way to change the name of the settings file or subfolder of the created machine directly. </dd></dl>
     */
    suspend fun createMachine(settingsFile: String, name: String, osTypeId: String, flags: String, groups: Array<String>): IMachine

    /**
     *
     * Registers the machine previously created using [openMachine]**** within this VirtualBox installation.
     *
     * After successful method invocation, the [IMachineRegisteredEvent]**** event is fired.
     * <dl><dt>**Expected result codes:**</dt><dd><table>
     * <tbody><tr>
     * <td>[IVirtualBox.VBOX_E_OBJECT_NOT_FOUND]</td><td>No matching virtual machine found.   </td></tr>
     * <tr><td>[IVirtualBox.VBOX_E_INVALID_OBJECT_STATE]</td><td>Virtual machine was not created within this VirtualBox instance.   </td></tr>
    </tbody></table></dd></dl> *
     * <dl><dt>**Note:**</dt><dd>This method implicitly calls [IMachine.saveSettings]**** to save all current machine settings before registering it. </dd></dl>
     */
    suspend fun registerMachine(machine: IMachine)

    @Cacheable("GuestOSTypes")
	suspend fun getGuestOSTypes(): List<IGuestOSType>

    /**
     *
     * Returns an object describing the specified guest OS type.
     *
     * The requested guest OS type is specified using a string which is a mnemonic identifier of the guest operating system, such as `win31` or `ubuntu`.
     * The guest OS type ID of a particular virtual machine can be read or set using the [IMachine.getOSTypeId] attribute.
     *
     * The [IVirtualBox.getGuestOSTypes] collection contains all available guest OS type objects. Each object has an
     * [IGuestOSType.getId] attribute which contains an identifier of the guest OS this object describes.
     * <dl><dt>**Expected result codes:**</dt><dd><table>
     * <tbody><tr><td>[IVirtualBox.E_INVALIDARG]</td><td>`id` is not a valid Guest OS type.  </td></tr>
    </tbody></table></dd></dl> *
     * @param id        Guest OS type ID string.
     * @return            Guest OS type object.
     */
    suspend fun getGuestOSType(id: String): IGuestOSType

    suspend fun getProgressOperations(): List<IProgress>

    /**
     *
     * Creates a new base medium object that will use the given storage format and location for medium data.
     *
     * The actual storage unit is not created by this method. In order to do it, and before you are able to attach the created medium to virtual machines, you must call one of the following methods to allocate a format-specific storage unit at the specified location:
     *  * [IMedium.createBaseStorage]
     *  * [IMedium.createDiffStorage]
     *
     * Some medium attributes, such as [IMedium.getId], may remain uninitialized until the medium storage unit is successfully created by one of the above methods.
     *
     * After the storage unit is successfully created, it will be accessible through the [IVirtualBox.getHardDisks] array.
     *
     * The list of all storage formats supported by this VirtualBox installation can be obtained using [ISystemProperties.getDefaultHardDiskFormat] will be used for creating a storage unit of the medium.
     *
     * Note that the format of the location string is storage format specific. See [IMedium] for more details.
     * <dl><dt>**Expected result codes:**</dt><dd><table>
     * <tbody><tr><td>[ISystemProperties.getMediumFormats].</td></tr>
     * <tr><td>[IVirtualBox.VBOX_E_FILE_ERROR] </td><td>*location* is a not valid file name (for file-based formats only).</td></tr>
    </tbody></table></dd></dl> *
     * @param format            Identifier of the storage format to use for the new medium.
     * @param location            Location of the storage unit for the new medium.
     * @return        Created medium object.
     */
    suspend fun createHardDisk(format: String, location: String): IMedium

    /**
     *
     * Finds existing media or opens a medium from an existing storage location.
     *
     * Once a medium has been opened, it can be passed to other VirtualBox methods, in particular to [IMachine.attachDevice].
     *
     * Depending on the given device type, the file at the storage location must be in one of the media formats understood by `VirtualBox`:
     *
     *  * With a *HardDisk* device type, the file must be a hard disk image in one of the formats supported by `VirtualBox`
     * (see [ISystemProperties.getMediumFormats]). After this method succeeds, if the medium is a base medium, it will be added to the
     * [IVirtualBox.getHardDisks] array attribute.
     *  * With a *DVD* device type, the file must be an ISO 9960 CD/DVD image. After this method succeeds, the medium will be added to the [IVirtualBox.getDVDImages] array attribute.
     *  * With a *Floppy* device type, the file must be an RAW floppy image. After this method succeeds, the medium will be added to the [IVirtualBox.getFloppyImages] array attribute.
     *
     *
     * After having been opened, the medium can be re-found by this method and can be attached to virtual machines. See [IMedium] for more details.
     *
     * The UUID of the newly opened medium will either be retrieved from the storage location, if the format supports it (e.g. for hard disk images), or a new UUID will be randomly
     * generated (e.g. for ISO and RAW files). If for some reason you need to change the medium's UUID, use [::setIds][IMedium].
     *
     * If a differencing hard disk medium is to be opened by this method, the operation will succeed only if its parent medium and all ancestors, if any, are already known to this VirtualBox
     * installation (for example, were opened by this method before).
     *
     * This method attempts to guess the storage format of the specified medium by reading medium data at the specified location.
     *
     * If *accessMode* is ReadWrite (which it should be for hard disks and floppies), the image is opened for read/write access and must have according permissions, as VirtualBox may
     * actually write status information into the disk's metadata sections.
     *
     * Note that write access is required for all typical hard disk usage in VirtualBox, since VirtualBox may need to write metadata such as a UUID into the image. The only exception is opening a
     * source image temporarily for copying and cloning (see [::cloneTo][IMedium] when the image will be closed again soon.
     *
     * The format of the location string is storage format specific. See [IMedium.getLocation] and [IMedium] for more details.
     *
     * <dl><dt><h4>Expected result codes:</h4></dt><dd><table>
     * <tbody><tr><td>[IVirtualBox.VBOX_E_FILE_ERROR]</td><td>Invalid medium storage file location or could not find the medium at the specified location.   </td></tr>
     * <tr><td>[IVirtualBox.VBOX_E_IPRT_ERROR]</td><td>Could not get medium storage format.   </td></tr>
     * <tr><td>[IVirtualBox.E_INVALIDARG]</td><td>Invalid medium storage format.   </td></tr>
     * <tr><td>[IVirtualBox.VBOX_E_INVALID_OBJECT_STATE]</td><td>Medium has already been added to a media registry.   </td></tr>
    </tbody></table></dd></dl> *
     * @param location                    Location of the storage unit that contains medium data in one of the supported storage formats.
     * @param deviceType            Must be one of "HardDisk", "DVD" or "Floppy".
     * @param accessMode            Whether to open the image in read/write or read-only mode. For a "DVD" device type, this is ignored and read-only mode is always assumed.
     * @param forceNewUuid        Allows the caller to request a completely new medium UUID for the image which is to be opened. Useful if one intends to open an exact copy of a previously opened image, as this would normally fail due to the duplicate UUID.
     * @return                                Opened medium object
     */
    suspend fun openMedium(location: String, deviceType: DeviceType, accessMode: AccessMode, forceNewUuid: Boolean): IMedium

    /**
     * @return	Array of medium objects known to this VirtualBox installation.
     */
    @Cacheable("HardDisks")
	suspend fun getHardDisks(): List<IMedium>

    /**
     * @return	Array of CD/DVD image objects currently in use by this VirtualBox instance.
     */
    @Cacheable("DVDImages")
	suspend fun getDVDImages(): List<IMedium>

    /**
     * @return	Array of floppy image objects currently in use by this VirtualBox instance.
     */
    @Cacheable("FloppyImages")
	suspend fun getFloppyImages(): List<IMedium>

    /**
     * @return DHCP Servers
     */
    @Cacheable("DHCPServers")
	suspend fun getDHCPServers(): List<IDHCPServer>

    /**
     * Searches a DHCP server settings to be used for the given internal network name.
     *
     * <dl><dt>**Expected result codes:**</dt><dd><table><tbody><tr>
     * <td>[IVirtualBox.E_INVALIDARG]</td><td>Host network interface *name* already exists.  </td></tr>
    </tbody></table></dd></dl> *
     * @param name        server name
     * @param server    DHCP server settings
     */
    suspend fun findDHCPServerByNetworkName(name: String): IDHCPServer

    suspend fun removeDHCPServer(server: IDHCPServer)

    /**
     *
     * Returns associated global extra data.
     *
     * If the requested data `key` does not exist, this function will succeed and return an empty string in the `value` argument.
     * <dl><dt>**Expected result codes:**</dt><dd><table>
     * <tbody><tr><td>[IVirtualBox.VBOX_E_FILE_ERROR]</td><td>Settings file not accessible.</td></tr>
     * <tr><td>[IVirtualBox.VBOX_E_XML_ERROR]</td><td>Could not parse the settings file.</td></tr>
    </tbody></table></dd></dl> *
     * @param key        Name of the data key to get.
     * @return            Value of the requested data key.
     */
    suspend fun getExtraData(key: String): String

    suspend fun setExtraData(key: String, value: String)

    @Cacheable
    suspend fun getExtraDataKeys(): List<String>

    suspend fun createDHCPServer(networkName: String): IDHCPServer

    suspend fun setSettingsSecret(password: String)

    suspend fun createSharedFolder(name: String, hostPath: String, writeable: Boolean, automount: Boolean)

    suspend fun removeSharedFolder(name: String)

//	@Ksoap(cacheable=true) List<ISharedFolder> getSharedFolders();

//	public IAppliance createAppliance();

    suspend fun getMachineStates(machines: List<IMachine>): List<MachineState>

    suspend fun openMachine(settingsFile: String): IMachine

    @Cacheable
    suspend fun getNATNetworks(): List<INATNetwork>

    suspend fun createNATNetwork(networkName: String): INATNetwork

    suspend fun findNATNetworkByName(networkName: String): INATNetwork

    suspend fun removeNATNetwork(network: INATNetwork)
}

/**
 * Searches a DHCP server settings to be used for the given internal network name.
 * @param name        server name
 */
suspend fun IVirtualBox.findDHCPServerByNetworkNameOrNull(name: String): IDHCPServer? {
    try {
        return findDHCPServerByNetworkName(name)
    } catch (e: SoapFault) {
        Timber.e("Couldn't find DHCP Server: %s", e.message)
        return null
    }
}

suspend fun IVirtualBox.takeScreenshot(machine: IMachine): Screenshot? {
    if (machine.getState() == MachineState.RUNNING || machine.getState() == MachineState.SAVED) {
        val session = getSessionObject()
        machine.lockMachine(session, LockType.SHARED)
        try {
            val display = session.getConsole().getDisplay()
            val res = display.getScreenResolution(0)
            val width = res.get("width")!!.toInt()
            val height = res.get("height")!!.toInt()
            return Screenshot(width, height, display.takeScreenShotToArray(0, width, height, BitmapFormat.PNG))
        } finally {
            session.unlockMachine()
        }
    }
    return null
}

suspend fun IVirtualBox.takeScreenshot(machine: IMachine, width: Int, height: Int): Screenshot {
    var width = width
    var height = height
    val session = getSessionObject()
    machine.lockMachine(session, LockType.SHARED)
    try {
        val display = session.getConsole().getDisplay()
        val res = display.getScreenResolution(0)
        val screenW = res.get("width")!!.toFloat()
        val screenH = res.get("height")!!.toFloat()
        if (screenW > screenH) {
            val aspect = screenH / screenW
            height = (aspect * width).toInt()
        } else if (screenH > screenW) {
            val aspect = screenW / screenH
            width = (aspect * height).toInt()
        }
        return Screenshot(width, height,
                session.getConsole().getDisplay().takeScreenShotToArray(0, width, height, BitmapFormat.PNG))
    } finally {
        session.unlockMachine()
    }
}