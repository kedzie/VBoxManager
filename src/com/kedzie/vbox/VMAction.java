package com.kedzie.vbox;

import com.kedzie.vbox.api.jaxb.MachineState;

/**
 * Action which can be performed on a Virtual Machine
 * @author Marek Kedzierski
 */
public enum VMAction {
	START(VBoxApplication.getInstance().getResources().getString(R.string.action_start)),
	RESET("Reset"),
	PAUSE(VBoxApplication.getInstance().getResources().getString(R.string.action_pause)),
	RESUME(VBoxApplication.getInstance().getResources().getString(R.string.action_resume)),
	TAKE_SNAPSHOT("Take Snapshot"),
	RESTORE_SNAPSHOT(VBoxApplication.getInstance().getResources().getString(R.string.action_restore_snapshot)),
	DELETE_SNAPSHOT(VBoxApplication.getInstance().getResources().getString(R.string.action_delete_snapshot)),
	SAVE_STATE(VBoxApplication.getInstance().getResources().getString(R.string.action_save_state)),
	DISCARD_STATE(VBoxApplication.getInstance().getResources().getString(R.string.action_discard_state)),
	POWER_BUTTON(VBoxApplication.getInstance().getResources().getString(R.string.action_acpi)),
	POWER_OFF(VBoxApplication.getInstance().getResources().getString(R.string.action_poweroff)),
	VIEW_METRICS("View Metrics"),
	TAKE_SCREENSHOT("Take Screenshot"),
	EDIT_SETTINGS("Settings");

	private String _val;

	private VMAction(String val) {
		_val=val;
	}

	public String toString() {
		return _val;
	}

	/**
	 * Which actions can be performed on a Virtual Machine for each {@link MachineState}
	 * @param state virtual machine state
	 * @return actions which can be performed
	 */
	public static VMAction[] getVMActions(MachineState state) {
		if(state.equals(MachineState.RUNNING)) return new VMAction[] { PAUSE, RESET, POWER_OFF , POWER_BUTTON, SAVE_STATE, TAKE_SNAPSHOT, VIEW_METRICS, TAKE_SCREENSHOT };
		else if (state.equals(MachineState.POWERED_OFF) || state.equals(MachineState.ABORTED))	return new VMAction[] { START,  TAKE_SNAPSHOT, EDIT_SETTINGS };
		else if (state.equals(MachineState.PAUSED))	return new VMAction[] { RESUME, RESET, POWER_OFF, TAKE_SNAPSHOT, TAKE_SCREENSHOT };
		else if (state.equals(MachineState.SAVED))	return new VMAction[] { START, DISCARD_STATE };
		return new VMAction[] {};
	}
}
