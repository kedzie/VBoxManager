

package com.kedzie.vbox.api.jaxb;


/**
 * <p>Java class for MachineState.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="MachineState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Null"/>
 *     &lt;enumeration value="PoweredOff"/>
 *     &lt;enumeration value="Saved"/>
 *     &lt;enumeration value="Teleported"/>
 *     &lt;enumeration value="Aborted"/>
 *     &lt;enumeration value="Running"/>
 *     &lt;enumeration value="Paused"/>
 *     &lt;enumeration value="Stuck"/>
 *     &lt;enumeration value="Teleporting"/>
 *     &lt;enumeration value="LiveSnapshotting"/>
 *     &lt;enumeration value="Starting"/>
 *     &lt;enumeration value="Stopping"/>
 *     &lt;enumeration value="Saving"/>
 *     &lt;enumeration value="Restoring"/>
 *     &lt;enumeration value="TeleportingPausedVM"/>
 *     &lt;enumeration value="TeleportingIn"/>
 *     &lt;enumeration value="FaultTolerantSyncing"/>
 *     &lt;enumeration value="DeletingSnapshotOnline"/>
 *     &lt;enumeration value="DeletingSnapshotPaused"/>
 *     &lt;enumeration value="RestoringSnapshot"/>
 *     &lt;enumeration value="DeletingSnapshot"/>
 *     &lt;enumeration value="SettingUp"/>
 *     &lt;enumeration value="FirstOnline"/>
 *     &lt;enumeration value="LastOnline"/>
 *     &lt;enumeration value="FirstTransient"/>
 *     &lt;enumeration value="LastTransient"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
public enum MachineState {

	NULL("Null"),
	POWERED_OFF("PoweredOff"),
	SAVED("Saved"),
	TELEPORTED("Teleported"),
	ABORTED("Aborted"),
	RUNNING("Running"),
	PAUSED("Paused"),
	STUCK("Stuck"),
	TELEPORTING("Teleporting"),
	LIVE_SNAPSHOTTING("LiveSnapshotting"),
	STARTING("Starting"),
	STOPPING("Stopping"),
	SAVING("Saving"),
	RESTORING("Restoring"),
	TELEPORTING_PAUSED_VM("TeleportingPausedVM"),
	TELEPORTING_IN("TeleportingIn"),
	FAULT_TOLERANT_SYNCING("FaultTolerantSyncing"),
	DELETING_SNAPSHOT_ONLINE("DeletingSnapshotOnline"),
	DELETING_SNAPSHOT_PAUSED("DeletingSnapshotPaused"),
	RESTORING_SNAPSHOT("RestoringSnapshot"),
	DELETING_SNAPSHOT("DeletingSnapshot"),
	SETTING_UP("SettingUp"),
	FIRST_ONLINE("FirstOnline"),
	LAST_ONLINE("LastOnline"),
	FIRST_TRANSIENT("FirstTransient"),
	LAST_TRANSIENT("LastTransient");
	private final String value;

	MachineState(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static MachineState fromValue(String v) {
		for (MachineState c : MachineState.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
