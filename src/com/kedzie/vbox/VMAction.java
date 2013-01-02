package com.kedzie.vbox;

import com.kedzie.vbox.api.jaxb.MachineState;

/**
 * Action which can be performed on a Virtual Machine
 * @author Marek Kedzierski
 */
public enum VMAction {
	START("Start"),
	RESET("Reset"),
	PAUSE("Pause"),
	RESUME("Resume"),
	TAKE_SNAPSHOT("Take Snapshot"),
	RESTORE_SNAPSHOT("Restore Snapshot"),
	DELETE_SNAPSHOT("Delete Snapshot"),
	SAVE_STATE("Save State"),
	DISCARD_STATE("Discard State"),
	POWER_BUTTON("Power Button"),
	POWER_OFF("Power Off"),
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
