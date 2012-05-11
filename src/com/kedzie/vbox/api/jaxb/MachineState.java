package com.kedzie.vbox.api.jaxb;

import java.io.Serializable;

public enum MachineState implements Serializable {

	NULL("Null"), POWERED_OFF("PoweredOff"), SAVED("Saved"), TELEPORTED(
			"Teleported"), ABORTED("Aborted"), RUNNING("Running"), PAUSED(
			"Paused"), STUCK("Stuck"), TELEPORTING("Teleporting"), LIVE_SNAPSHOTTING(
			"LiveSnapshotting"), STARTING("Starting"), STOPPING("Stopping"), SAVING(
			"Saving"), RESTORING("Restoring"), TELEPORTING_PAUSED_VM(
			"TeleportingPausedVM"), TELEPORTING_IN("TeleportingIn"), FAULT_TOLERANT_SYNCING(
			"FaultTolerantSyncing"), DELETING_SNAPSHOT_ONLINE(
			"DeletingSnapshotOnline"), DELETING_SNAPSHOT_PAUSED(
			"DeletingSnapshotPaused"), RESTORING_SNAPSHOT("RestoringSnapshot"), DELETING_SNAPSHOT(
			"DeletingSnapshot"), SETTING_UP("SettingUp"), FIRST_ONLINE(
			"FirstOnline"), LAST_ONLINE("LastOnline"), FIRST_TRANSIENT(
			"FirstTransient"), LAST_TRANSIENT("LastTransient");
	private final String value;

	MachineState(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public String toString() {
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
