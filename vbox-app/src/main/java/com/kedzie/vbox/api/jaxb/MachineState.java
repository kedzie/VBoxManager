

package com.kedzie.vbox.api.jaxb;


import com.kedzie.vbox.R;

/**
 * <p>Java class for MachineState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
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
 *     &lt;enumeration value="OnlineSnapshotting"/>
 *     &lt;enumeration value="RestoringSnapshot"/>
 *     &lt;enumeration value="DeletingSnapshot"/>
 *     &lt;enumeration value="SettingUp"/>
 *     &lt;enumeration value="Snapshotting"/>
 *     &lt;enumeration value="FirstOnline"/>
 *     &lt;enumeration value="LastOnline"/>
 *     &lt;enumeration value="FirstTransient"/>
 *     &lt;enumeration value="LastTransient"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
public enum MachineState {
    //TODO make new drawables for missing states
    NULL("Null", R.drawable.ic_list_start),
    POWERED_OFF("PoweredOff", R.drawable.ic_list_poweroff),
    SAVED("Saved", R.drawable.ic_list_save),
    TELEPORTED("Teleported", R.drawable.ic_list_start),
    ABORTED("Aborted", R.drawable.ic_list_abort),
    RUNNING("Running", R.drawable.ic_list_start),
    PAUSED("Paused", R.drawable.ic_list_pause),
    STUCK("Stuck", R.drawable.ic_list_stuck),
    TELEPORTING("Teleporting", R.drawable.ic_list_start),
    LIVE_SNAPSHOTTING("LiveSnapshotting", R.drawable.ic_list_snapshot),
    STARTING("Starting", R.drawable.ic_list_start),
    STOPPING("Stopping", R.drawable.ic_list_acpi),
    SAVING("Saving", R.drawable.ic_list_save),
    RESTORING("Restoring", R.drawable.ic_list_save),
    TELEPORTING_PAUSED_VM("TeleportingPausedVM", R.drawable.ic_list_start),
    TELEPORTING_IN("TeleportingIn", R.drawable.ic_list_start),
    FAULT_TOLERANT_SYNCING("FaultTolerantSyncing", R.drawable.ic_list_start),
    DELETING_SNAPSHOT_ONLINE("DeletingSnapshotOnline", R.drawable.ic_list_snapshot_del),
    DELETING_SNAPSHOT_PAUSED("DeletingSnapshotPaused", R.drawable.ic_list_snapshot_del),
    ONLINE_SNAPSHOTTING("OnlineSnapshotting", R.drawable.ic_list_snapshot_add),
    RESTORING_SNAPSHOT("RestoringSnapshot", R.drawable.ic_list_snapshot),
    DELETING_SNAPSHOT("DeletingSnapshot", R.drawable.ic_list_snapshot_del),
    SETTING_UP("SettingUp", R.drawable.ic_list_start),
    SNAPSHOTTING("Snapshotting", R.drawable.ic_list_snapshot_add),
    FIRST_ONLINE("FirstOnline", R.drawable.ic_list_start),
    LAST_ONLINE("LastOnline", R.drawable.ic_list_start),
    FIRST_TRANSIENT("FirstTransient", R.drawable.ic_list_start),
    LAST_TRANSIENT("LastTransient", R.drawable.ic_list_start);

    private final String value;
    private int drawable;

    MachineState(String v, int drawable) {
        value = v;
        this.drawable = drawable;
    }

    public String value() {
        return value;
    }

    public int drawable() { return drawable; }

    public static MachineState fromValue(String v) {
        for (MachineState c: MachineState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
