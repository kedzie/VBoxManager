package com.kedzie.vbox;

import com.kedzie.vbox.api.jaxb.MachineState;

/**
 * Action which can be performed on a Virtual Machine
 * @author Marek Kedzierski
 */
public enum VMAction {
	START(R.string.action_start, R.drawable.ic_list_start),
	RESET(R.string.action_reset, R.drawable.ic_list_reset),
	PAUSE(R.string.action_pause, R.drawable.ic_list_pause),
	RESUME(R.string.action_resume, R.drawable.ic_list_start),
	TAKE_SNAPSHOT(R.string.action_take_snapshot, R.drawable.ic_list_snapshot_add),
	RESTORE_SNAPSHOT(R.string.action_restore_snapshot, R.drawable.ic_list_snapshot),
	DELETE_SNAPSHOT(R.string.action_delete_snapshot, R.drawable.ic_list_snapshot_del),
	SAVE_STATE(R.string.action_save_state, R.drawable.ic_list_save),
	DISCARD_STATE(R.string.action_discard_state, R.drawable.ic_list_save),
	POWER_BUTTON(R.string.action_acpi, R.drawable.ic_list_acpi),
	POWER_OFF(R.string.action_poweroff, R.drawable.ic_list_poweroff),
	VIEW_METRICS(R.string.action_view_metrics, R.drawable.ic_menu_metrics),
	TAKE_SCREENSHOT(R.string.action_take_screenshot, R.drawable.ic_list_snapshot_add),
	EDIT_SETTINGS(R.string.action_settings, R.drawable.ic_menu_settings);

	private int value;
	private int drawable;

	VMAction(int value, int drawable) {

		this.value=value;
		this.drawable = drawable;
	}

	public int value() { return value; }

	public int drawable() { return drawable; }

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
