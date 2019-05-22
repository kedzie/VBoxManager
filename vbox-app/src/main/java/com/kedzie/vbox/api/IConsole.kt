package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.IVRDEServerInfo
import com.kedzie.vbox.api.jaxb.MachineState
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

/**
 * <p>The <code>IConsole</code> interface represents an interface to control virtual machine execution.</p>
 * <p>A console object gets created when a machine has been locked for a particular session (client process) using {@link IMachine#lockMachine} or {@link IMachine#launchVMProcess}. The console object can then be found in the session's {@link ISession#console} attribute.
 * Methods of the {@link IConsole} interface allow the caller to query the current virtual machine execution state, pause the machine or power it down, save the machine state or take a snapshot, attach and detach removable media and so on.</p>
 * @see {@link ISession}
 */
@KsoapProxy
@Ksoap
interface IConsole: IManagedObjectRef, Parcelable {

    @Cacheable(value = "EventSource")
	suspend fun getEventSource(): IEventSource

    @Cacheable(value = "Display")
	suspend fun getDisplay(): IDisplay

    @Cacheable(value = "Guest")
	suspend fun getGuest(): IGuest

    @Cacheable(value = "State")
    suspend fun getState(): MachineState

    @Cacheable(value = "State", get = false)
    suspend fun getStateNoCache(): MachineState

    @Cacheable(value = "VRDEServerInfo")
	suspend fun getVRDEServerInfo(): IVRDEServerInfo

    /**
     * <p>Starts the virtual machine execution using the current machine state (that is, its current execution state, current settings and current storage devices).</p>
     * <ul>
     * <li>If the machine is powered off or aborted, the execution will start from the beginning (as if the real hardware were just powered on).</li>
     * <li>If the machine is in the @{link MachineState#Saved} state, it will continue its execution the point where the state has been saved.</li>
     * <li>If the machine {@link IMachine#teleporterEnabled} property is enabled on the machine being powered up, the machine will wait for an incoming teleportation in the <code>MachineState_TeleportingIn</code> state. The returned progress object will have at least three operations where the last three are defined as: (1) powering up and starting TCP server, (2) waiting for incoming teleportations, and (3) perform teleportation. These operations will be reflected as the last three operations of the progress objected returned by IMachine::launchVMProcess as well.</li>
     * </ul>
     * <p>Expected result codes:</p>
     * <ul>
     * <li><code>VBOX_E_INVALID_VM_STATE</code>	Virtual machine already running.</li>
     * <li><code>VBOX_E_HOST_ERROR</code>	Host interface does not exist or name not set.</li>
     * <li><code>VBOX_E_FILE_ERROR</code>	Invalid saved state file.</li>
     * </ul>
     * <p>Note: This method is only useful for front-ends that want to actually execute virtual machines in their own process (like the <code>VirtualBox</code> or <code>VBoxSDL</code> front-ends). Unless you are intending to write such a front-end, do not call this method. If you simply want to start virtual machine execution using one of the existing front-ends (for example the VirtualBox GUI or headless server), use IMachine::launchVMProcess instead; these front-ends will power up the machine automatically for you.</p>
     * @see {@link com.kedzie.vbox.api.IConsole#saveState()}
     * @return Progress information
     * @throws IOException
     */
    suspend fun powerUp(): IProgress

    suspend fun powerUpPaused(): IProgress

    suspend fun powerDown(): IProgress

    suspend fun sleepButton()

    suspend fun powerButton()

    suspend fun reset()

    suspend fun pause()

    suspend fun resume()

    suspend fun saveState(): IProgress

    suspend fun adoptSavedState(saveStateFile: String )

    suspend fun discardSavedState(fRemoveFile: Boolean)

    suspend fun takeSnapshot(name:  String, description:  String ): IProgress

    suspend fun deleteSnapshot(id: String): IProgress

    suspend fun deleteSnapshotAndChildren(id: String): IProgress

    suspend fun deleteSnapshotRange(startId: String, endId: String): IProgress

    suspend fun restoreSnapshot(snapshot: ISnapshot): IProgress

}