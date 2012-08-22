package com.kedzie.vbox.api;

import java.io.IOException;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.kedzie.vbox.soap.KSOAP;

/**
 * <p>The <code>IConsole</code> interface represents an interface to control virtual machine execution.</p>
 * <p>A console object gets created when a machine has been locked for a particular session (client process) using {@link IMachine#lockMachine} or {@link IMachine#launchVMProcess}. The console object can then be found in the session's {@link ISession#console} attribute.
 * Methods of the {@link IConsole} interface allow the caller to query the current virtual machine execution state, pause the machine or power it down, save the machine state or take a snapshot, attach and detach removable media and so on.</p>
 * @see {@link ISession}
 */
public interface IConsole extends IManagedObjectRef {

	/**
	 * <p>Starts the virtual machine execution using the current machine state (that is, its current execution state, current settings and current storage devices).</p>
	 * <ul>
	 * <li>If the machine is powered off or aborted, the execution will start from the beginning (as if the real hardware were just powered on).</li>
	 * <li>If the machine is in the <code>MachineState_Saved</code> state, it will continue its execution the point where the state has been saved.</li>
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
	public IProgress powerUp() throws IOException;
	
	/**
	 * @return
	 * @throws IOException
	 */
	public IProgress powerUpPaused() throws IOException;
	/**
	 * @return
	 * @throws IOException
	 */
	public IProgress powerDown() throws IOException;
	
	/**
	 * @throws IOException
	 */
	public void sleepButton() throws IOException;
	
	/**
	 * @throws IOException
	 */
	public void powerButton() throws IOException;
	
	/**
	 * @throws IOException
	 */
	public void reset() throws IOException;
	
	/**
	 * @throws IOException
	 */
	public void pause() throws IOException;
	
	/**
	 * @throws IOException
	 */
	public void resume() throws IOException;
	
	/**
	 * @return
	 * @throws IOException
	 */
	public IProgress saveState() throws IOException;
	
	/**
	 * @param saveStateFile
	 * @throws IOException
	 */
	public void adoptSavedState(@KSOAP("saveStateFile") String saveStateFile) throws IOException;
	/**
	 * @param removeFile
	 * @throws IOException
	 */
	public void discardSavedState( @KSOAP("fRemoveFile") boolean removeFile) throws IOException;
	
	/**
	 * @return
	 * @throws IOException
	 */
	public IEventSource getEventSource() throws IOException;
	
	/**
	 * @return
	 * @throws IOException
	 */
	public IDisplay getDisplay() throws IOException;
	
	/**
	 * @param name
	 * @param description
	 * @return
	 * @throws IOException
	 */
	public IProgress takeSnapshot(@KSOAP("name") String name, @KSOAP("description") String description) throws IOException;
	
	/**
	 * @param id  id of snapshot to be deleted
	 * @return
	 * @throws IOException
	 */
	public IProgress deleteSnapshot(@KSOAP(namespace=SoapSerializationEnvelope.XSD, type="string", value="id") String id) throws IOException;
	
	/**
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public IProgress restoreSnapshot(@KSOAP("name") String name) throws IOException;
}
