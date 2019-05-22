package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.*
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy
import java.io.IOException

@KsoapProxy
@Ksoap
interface IMachine : IManagedObjectRef, Parcelable {

    enum class LaunchMode constructor(val value: String) {
        headless("headless"),
        gui("gui"),
        sdl("sdl"),
        emergencystop("emergencystop");

        fun value(): String {
            return value
        }
    }

    companion object {
        const val BUNDLE = "machine"
    }

    /**
     * @return UUID of the virtual machine.
     */
    @Cacheable("id")
    suspend fun getId(): String

    /**
     * @return Name of the virtual machine.
     */
    @Cacheable("name")
    suspend fun getName(): String

    suspend fun setName(@Cacheable("name") name: String)

    /**
     * @return Description of the virtual machine.
     */
    @Cacheable("description")
    suspend fun getDescription(): String
    suspend fun setDescription(@Cacheable("description") description: String)

    /**
     * @return User-defined identifier of the Guest OS type.
     */
    @Cacheable("OSTypeId")
    suspend fun getOSTypeId(): String
    suspend fun setOSTypeId(@Cacheable("OSTypeId") osTypeId: String)

    /**
     * @return Number of virtual CPUs in the VM.
     */
    @Cacheable("CPUCount")
    suspend fun getCPUCount(): Int

    suspend fun setCPUCount(@Cacheable("CPUCount") @Ksoap(type = "unsignedInt") CPUCount: Int)


    /**
     * This setting determines whether VirtualBox allows CPU hotplugging for this machine.
     */
    @Cacheable("CPUHotPlugEnabled")
    suspend fun getCPUHotPlugEnabled(): Boolean;

    /**
     * @return Number of virtual CPUs in the VM.
     */
    @Cacheable("CPUExecutionCap")
    suspend fun getCPUExecutionCap(): Int
    suspend fun setCPUExecutionCap(@Cacheable("CPUExecutionCap") @Ksoap(type = "unsignedInt") CPUExecutionCap: Int)

    /**
     * @return System memory size in megabytes.
     */
    @Cacheable("memorySize")
    suspend fun getMemorySize(): Int
    suspend fun setMemorySize(@Cacheable("memorySize") @Ksoap(type = "unsignedInt") memorySize: Int)

    /**
     * @return Memory balloon size in megabytes.
     */
    @Cacheable("MemoryBalloonSize")
    suspend fun getMemoryBalloonSize(): Int

    /**
     * @return Video memory size in megabytes.
     */
    @Cacheable("VRAMSize")
    suspend fun getVRAMSize(): Int
    suspend fun setVRAMSize(@Cacheable("VRAMSize") @Ksoap(type = "unsignedInt") VRAMSize: Int)

    /**
     * @return This setting determines whether VirtualBox allows this machine to make use of the 3D graphics support available on the host.
     */
    @Cacheable("accelerate3DEnabled")
    suspend fun getAccelerate3DEnabled(): Boolean
    suspend fun setAccelerate3DEnabled(@Cacheable("accelerate3DEnabled") @Ksoap(type = "Boolean") accelerate3DEnabled: Boolean)

    /**
     * @return This setting determines whether VirtualBox allows this machine to make use of the 2D video acceleration support available on the host.
     */
    @Cacheable("Accelerate2DVideoEnabled")
    suspend fun getAccelerate2DVideoEnabled(): Boolean
    suspend fun setAccelerate2DVideoEnabled(@Cacheable("Accelerate2DVideoEnabled") @Ksoap(type = "Boolean") accelerate2DVideoEnabled: Boolean)

    /**
     * @return Number of virtual monitors.
     */
    @Cacheable("MonitorCount")
    suspend fun getMonitorCount(): Int
    suspend fun setMonitorCount(@Cacheable("MonitorCount") @Ksoap(type = "unsignedInt") monitorCount: Int)

    /**
     * @return This attribute controls if High Precision Event Timer (HPET) is enabled in this VM.
     */
    @Cacheable("HPETEnabled")
    suspend fun getHPETEnabled(): Boolean
    suspend fun setHPETEnabled(@Cacheable("HPETEnabled") HPETEnabled: Boolean)

    /**
     * @return  Chipset type used in this VM.
     */
    @Cacheable("chipsetType")
    suspend fun getChipsetType(): ChipsetType
    suspend fun setChipsetType(@Cacheable("chipsetType") chipsetType: ChipsetType)

    @Cacheable("FirmwareType")
    suspend fun getFirmwareType(): FirmwareType
    suspend fun setFirmwareType(@Cacheable("firmwareType") firmwareType: FirmwareType)

    @Cacheable("RTCUseUTC")
    suspend fun getRTCUseUTC(): Boolean
    suspend fun setRTCUseUTC(@Cacheable("RTCUseUTC") RTCUseUTC: Boolean)

    /**
     * <p>Array of machine group names of which this machine is a member. </p>
     * <p><code>""</code> and <code>"/"</code> are synonyms for the toplevel group. Each group is only listed once, however they are listed in no particular order and
     * there is no guarantee that there are no gaps in the group hierarchy (i.e. <code>"/group"</code>, <code>"/group/subgroup/subsubgroup"</code> is a valid result). </p>
     */
    @Cacheable("groups")
    suspend fun getGroups(): List<String>
    suspend fun setGroups(@Cacheable("groups") group: Array<String>)

    @Cacheable("BIOSSettings")
    suspend fun getBIOSSettings(): IBIOSSettings

    suspend fun getHWVirtExProperty(property: HWVirtExPropertyType): Boolean
    suspend fun setHWVirtExProperty(property: HWVirtExPropertyType, value: Boolean)

    suspend fun getCPUProperty(property: CPUPropertyType): Boolean

    suspend fun setCPUProperty(property: CPUPropertyType, @Ksoap("value") value: Boolean)

    suspend fun getBootOrder(@Ksoap(type = "unsignedInt") position: Int): DeviceType

    suspend fun setBootOrder(@Ksoap(type = "unsignedInt") position: Int, device: DeviceType)

    @Cacheable("AudioAdapter")
    suspend fun getAudioAdapter(): IAudioAdapter

    @Cacheable("MediumAttachments")
	suspend fun getMediumAttachments(): List<IMediumAttachment>
    suspend fun getMedium(name: String, @Ksoap(type = "int") controllerPort: Int, @Ksoap(type = "int") device: Int): IMedium
    suspend fun getMediumAttachment(name: String, @Ksoap(type = "int") controllerPort: Int, @Ksoap(type = "int") device: Int): IMediumAttachment
    suspend fun getMediumAttachmentsOfController( name: String): List<IMediumAttachment>

    @Cacheable("StorageControllers")
	suspend fun getStorageControllers(): List<IStorageController>
    suspend fun getStorageControllerByName(name: String): IStorageController
    suspend fun getStorageControllerByInstance(@Ksoap(type = "unsignedInt") instance: Int): IStorageController
    suspend fun removeStorageController(name: String)
    suspend fun addStorageController(name: String, connectionType: StorageBus): IStorageController
    suspend fun setStorageControllerBootable(name: String, bootable: Boolean)

    suspend fun attachDevice(name: String, @Ksoap(type = "int") controllerPort: Int, @Ksoap(type = "int") device: Int, type: DeviceType, medium: IMedium)

    suspend fun attachDeviceWithoutMedium(name: String, @Ksoap(type = "int") controllerPort: Int, @Ksoap(type = "int") device: Int, type: DeviceType)

    /**
     *
     * Detaches the device attached to a device slot of the specified bus.
     *
     * Detaching the device from the virtual machine is deferred. This means that the medium remains associated with the machine when this method returns and gets actually de-associated only after a successful [saveSettings](interface_i_machine.html#a2eb47e1d878566569b26893cc12bd8e1)**** call. See [IMedium](interface_i_medium.html)**** for more detailed information about attaching media.
     * <dl><dt>**Expected result codes:**</dt><dd><table>
     * <tbody><tr><td>[IVirtualBox.VBOX_E_INVALID_VM_STATE]</td><td>Attempt to detach medium from a running virtual machine.   </td></tr>
     * <tr><td>[IVirtualBox.VBOX_E_OBJECT_NOT_FOUND]</td><td>No medium attached to given slot/bus.   </td></tr>
     * <tr><td>[IVirtualBox.VBOX_E_NOT_SUPPORTED]</td><td>Medium format does not support storage deletion (only for implicitly created differencing media, should not happen).   </td></tr>
    </tbody></table></dd></dl> *
     * <dl><dt>**Note:**</dt><dd>You cannot detach a device from a running machine.</dd><dd>
     * Detaching differencing media implicitly created by [attachDevice] for the indirect attachment using this method will **not** implicitly delete them. The [IMedium.deleteStorage]
     * operation should be explicitly performed by the caller after the medium is successfully detached and the settings are saved with [saveSettings], if it is the desired action. </dd></dl>
     * @param name            Name of the storage controller to detach the medium from.
     * @param controllerPort        Port number to detach the medium from.
     * @param device        Device slot number to detach the medium from.
     */
    suspend fun detachDevice(name: String, @Ksoap(type = "int") controllerPort: Int, @Ksoap(type = "int") device: Int)

    suspend fun mountMedium(name: String, @Ksoap(type = "int") controllerPort: Int, @Ksoap(type = "int") device: Int, medium: IMedium, force: Boolean)

    suspend fun unmountMedium(name: String, @Ksoap(type = "int") controllerPort: Int, @Ksoap(type = "int") device: Int, force: Boolean)

    /**
     * @return  Full path to the directory used to store snapshot data (differencing media and saved state files) of this machine.
     */
    @Cacheable("SnapshotFolder")
	suspend fun getSnapshotFolder(): String

    suspend fun  getNetworkAdapter(@Ksoap(type="unsignedInt") slot: Int): INetworkAdapter

    /**
     * @return  VirtualBox Remote Desktop Extension (VRDE) server object.
     */
    @Cacheable("VRDEServer")
	suspend fun getVRDEServer(): IVRDEServer

    /**
     * @return Current session state for this machine.
     */
    @Cacheable("SessionState")
	suspend fun getSessionState(): SessionState

    /**
     * @return  Type of the session.
     */
    @Cacheable("SessionType")
	suspend fun getSessionType(): SessionType

    @Cacheable("SettingsModified")
	suspend fun getSettingsModified(): Boolean

    /**
     * @return Identifier of the session process.
     */
    @Cacheable("SessionPid")
	suspend fun getSessionPid(): Int

    /**
     * @return Current execution state of this machine.
     */
    @Cacheable("State")
    suspend fun getState(): MachineState

    /**
     * @return Number of snapshots taken on this machine.
     */
    @Cacheable("snapshotCount")
    suspend fun getSnapshotCount(): Int

    /**
     * @return	Current snapshot of this machine.
     */
    @Cacheable("CurrentSnapshot")
    suspend fun getCurrentSnapshot(): ISnapshot

    /**
     * @return Returns true if the current state of the machine is not identical to the state stored in the current snapshot.
     */
    @Cacheable("CurrentStateModified")
	suspend fun getCurrentStateModified(): Boolean

    @Cacheable("IOCacheEnabled")
	suspend fun getIOCacheEnabled(): Boolean
    suspend fun setIOCacheEnabled(@Cacheable("IOCacheEnabled") IOCacheEnabled: Boolean)

    @Cacheable("IOCacheSize")
	suspend fun getIOCacheSize(): Int
    suspend fun setIOCacheSize(@Cacheable("IOCacheSize") @Ksoap(type="unsignedInt") IOCacheSize: Int)

    /**
     *
     * Locks the machine for the given session to enable the caller to make changes to the machine or start the VM or control VM execution.
     *
     * There are two ways to lock a machine for such uses:
     *
     *
     *
     *  * If you want to make changes to the machine settings, you must obtain an exclusive write lock on the machine by setting [LockType] to [LockType.Write].
     *
     *
     * This will only succeed if no other process has locked the machine to prevent conflicting changes. Only after an exclusive write lock has been obtained using this method,
     * one can change all VM settings or execute the VM in the process space of the session object. (Note that the latter is only of interest if you actually want to write a new
     * front-end for virtual machines; but this API gets called internally by the existing front-ends such as VBoxHeadless and the VirtualBox GUI to acquire a write lock on the
     * machine that they are running.)
     *
     *
     * On success, write-locking the machine for a session creates a second copy of the IMachine object. It is this second object upon which changes can be made; in VirtualBox
     * terminology, the second copy is "mutable". It is only this second, mutable machine object upon which you can call methods that change the machine state. After having called
     * this method, you can obtain this second, mutable machine object using the [ISession.getMachine] attribute.
     *  *  If you only want to check the machine state or control machine execution without actually changing machine settings (e.g. to get access to VM statistics or take a
     * snapshot or save the machine state), then set the [LockType] argument to `Shared`.
     *
     *
     * If no other session has obtained a lock, you will obtain an exclusive write lock as described above. However, if another session has already obtained such a lock, then a link to
     * that existing session will be established which allows you to control that existing session.
     *
     *
     * To find out which type of lock was obtained, you can inspect [ISession.getType], which will have been set to either [LockType.WriteLock] or [LockType.Shared].
     *
     *
     * In either case, you can get access to the [IConsole] object which controls VM execution.
     *
     *
     * Also in all of the above cases, one must always call [ISession.unlockMachine] to release the lock on the machine, or the machine's state will eventually be set to "Aborted".
     *
     *
     * To change settings on a machine, the following sequence is typically performed:
     *
     *
     *
     *  1. Call this method to obtain an exclusive write lock for the current session.
     *  1. Obtain a mutable [IMachine] object from [ISession.getMachine].
     *  1. Change the settings of the machine by invoking [IMachine] methods.
     *  1. Call [IMachine.saveSettings].
     *  1. Release the write lock by calling [ISession.unlockMachine].
     *
     * <dl><dt>**Expected result codes:**</dt><dd><table border="1" cellspacing="3" cellpadding="3">
     * <tr><td>[IVirtualBox.E_UNEXPECTED]</td><td>Virtual machine not registered.   </td></tr>
     * <tr><td>[IVirtualBox.E_ACCESSDENIED]</td><td>Process not started by OpenRemoteSession.   </td></tr>
     * <tr><td>[IVirtualBox.VBOX_E_INVALID_OBJECT_STATE]</td><td>Session already open or being opened.   </td></tr>
     * <tr><td>[IVirtualBox.VBOX_E_VM_ERROR]</td><td>Failed to assign machine to session.   </td></tr>
    </table> *
    </dd></dl> *
     * @param s  Session object for which the machine will be locked.
     * @param lockType    If set to [LockType.Write], then attempt to acquire an exclusive write lock or fail. If set to [LockType.Shared], then either acquire an exclusive write lock or establish a link to an existing session.
     * @throws IOException
     */
    suspend fun lockMachine(session: ISession, lockType: LockType)

    /**
     *
     * Spawns a new process that will execute the virtual machine and obtains a shared lock on the machine for the calling session.
     *
     * If launching the VM succeeds, the new VM process will create its own session and write-lock the machine for it, preventing conflicting changes from other
     * processes. If the machine is already locked (because it is already running or because another session has a write lock), launching the VM process will therefore
     * fail. Reversely, future attempts to obtain a write lock will also fail while the machine is running.
     *
     *
     * The caller's session object remains separate from the session opened by the new VM process. It receives its own [IConsole] object which can be used to
     * control machine execution, but it cannot be used to change all VM settings which would be available after a [IMachine.lockMachine] call.
     *
     *
     * The caller must eventually release the session's shared lock by calling [ISession.unlockMachine] on the local session object once this call has returned.
     * However, the session's state [Session.getState]) will not return to [SessionState.Unlocked] until the remote session has also unlocked the machine (i.e. the machine has stopped running).
     *
     *
     * Launching a VM process can take some time (a new VM is started in a new process, for which memory and other resources need to be set up). Because of this,
     * an [IProgress] object is returned to allow the caller to wait for this asynchronous operation to be completed. Until then, the caller's session object remains
     * in the [ISessionState.Unlocked] state, and its [ISession.getMachine] and [ISession.getConsole] attributes cannot be accessed. It is recommended
     * to use [IProgress.waitForCompletion] or similar calls to wait for completion. Completion is signalled when the VM is powered on. If launching the VM fails, error
     * messages can be queried via the progress object, if available.
     *
     *
     * The progress object will have at least 2 sub-operations. The first operation covers the period up to the new VM process calls powerUp. The subsequent operations
     * mirror the [IConsole.powerUp] progress object. Because [IConsole.powerUp] may require some extra sub-operations, the [IProgress.getOperationCount] may change at the completion of operation.
     *
     *
     * For details on the teleportation progress operation, see [IConsole.powerUp].
     *
     *
     * The *environment* argument is a string containing definitions of environment variables in the following format:
     * <pre>
     * NAME[=VALUE]<br></br>
     * NAME[=VALUE]<br></br>
     * ...
    </pre> *  where `\n` is the new line character. These environment variables will be appended to the environment of the VirtualBox server process.
     * If an environment variable exists both in the server process and in this list, the value from this list takes precedence over the server's variable. If the value of the
     * environment variable is omitted, this variable will be removed from the resulting environment. If the environment string is `null` or empty, the server
     * environment is inherited by the started process as is.
     *
     *
     * <dl class="user" compact><dt>**Expected result codes:**</dt><dd><table border="1" cellspacing="3" cellpadding="3">
     * <tr><td>[IVirtualBox.E_UNEXPECTED]</td><td>Virtual machine not registered. </td></tr>
     * <tr><td>[IVirtualBox.E_INVALIDARG]</td><td>Invalid session type *type*. </td></tr>
     * <tr><td>[IVirtualBox.VBOX_E_OBJECT_NOT_FOUND]</td><td>No machine matching *machineId* found.   </td></tr>
     * <tr><td>[IVirtualBox.VBOX_E_INVALID_OBJECT_STATE]</td><td>Session already open or being opened. </td></tr>
     * <tr><td>[IVirtualBox.VBOX_E_IPRT_ERROR]</td><td>Launching process for machine failed.</td></tr>
     * <tr><td>[IVirtualBox.VBOX_E_VM_ERROR]</td><td>Failed to assign machine to session.</td></tr>
    </table> *
    </dd></dl> *
     * @param session    Client session object to which the VM process will be connected (this must be in [SessionState.Unlocked] state).
     * @param type    Front-end to use for the new VM process. The following are currently supported:
     *  * [LaunchMode.gui]: VirtualBox Qt GUI front-end
     *  * [LaunchMode.headless]: VBoxHeadless (VRDE Server) front-end
     *  * [LaunchMode.sdl]: VirtualBox SDL front-end
     *  * [LaunchMode.emergencystop]: reserved value, used for aborting the currently running VM or session owner. In this case the *session* parameter may be `NULL` (if it is non-null it isn't used in any way), and the *progress* return value will be always NULL. The operation completes immediately.
     *
     * @return        Progress object to track the operation completion.
     * @throws IOException
     */
    suspend fun launchVMProcess(session: ISession, type: LaunchMode): IProgress

    suspend fun querySavedScreenshotInfo(@Ksoap(type = "unsignedInt") screenId: Int): Map<String, List<String>>
    suspend fun readSavedScreenshotToArray(@Ksoap(type = "unsignedInt") screenId: Int, format: BitmapFormat): Map<String, String>

    suspend fun queryLogFilename(@Ksoap(type = "unsignedInt", value = "idx") idx: Int): String

    suspend fun readLog(@Ksoap(type = "unsignedInt") idx: Int, @Ksoap(type = "long") offset: Long, @Ksoap(type = "long") size: Long): ByteArray

    /**
     *
     * Creates a clone of this machine, either as a full clone (which means creating independent copies of the hard disk media, save states and so on), or as a linked clone
     * (which uses its own differencing media, sharing the parent media with the source machine).
     *
     * The target machine object must have been created previously with [IVirtualBox.createMachine], and all the settings will be transferred except the VM name and the hardware UUID.
     * You can set the VM name and the new hardware UUID when creating the target machine. The network MAC addresses are newly created for all newtwork adapters.
     * You can change that behaviour with the options parameter. The operation is performed asynchronously, so the machine object will be not
     * be usable until the *progress* object signals completion.
     * @param mode  Which states should be cloned.
     * @param options   Options for the cloning operation.
     * @return progress  Progress object to track the operation completion.
     * <dl class="user"><dt>**Expected result codes:**</dt><dd><table class="doxtable">
     * <tbody><tr>
     * <td>E_INVALIDARG </td><td>*target* is `null`.  </td></tr>
    </tbody></table> *
    </dd></dl> *
     *
     */
    suspend fun cloneTo( target: IMachine, mode: CloneMode, options: Array<CloneOptions>): IProgress

    suspend fun saveSettings()

    suspend fun discardSettings()

    suspend fun getExtraData(key: String): String

    suspend fun setExtraData(key: String, value: String)

    @Cacheable
    suspend fun getExtraDataKeys(): List<String>
}