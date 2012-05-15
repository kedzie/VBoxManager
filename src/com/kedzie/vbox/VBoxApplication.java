package com.kedzie.vbox;


import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import com.kedzie.vbox.api.jaxb.MachineState;

/**
 * Stores a resource map storing OS Icons, Virtual Machine Action Icons, and Machine State Icons.
 * @author Marek Kedzierski
 */
public class VBoxApplication extends Application {
	
	protected Map<String,Integer> resources = new HashMap<String, Integer>();
	protected Map<String,Integer> resources_color = new HashMap<String, Integer>();
	protected static Map<String, Integer> metricColor = new HashMap<String, Integer>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		PreferenceManager.setDefaultValues(this, R.xml.metric_preferences, false);
		resources.put(MachineState.RUNNING.name(), R.drawable.ic_list_start);
		resources_color.put(MachineState.RUNNING.name(), R.drawable.ic_list_start_c);
		resources.put(MachineState.STARTING.name(), R.drawable.ic_list_start);
		resources_color.put(MachineState.STARTING.name(), R.drawable.ic_list_start_c);
		resources.put(MachineState.STOPPING.name(), R.drawable.ic_list_acpi);
		resources_color.put(MachineState.STOPPING.name(), R.drawable.ic_list_acpi_c);
		resources.put(MachineState.POWERED_OFF.name(), R.drawable.ic_list_acpi);
		resources_color.put(MachineState.POWERED_OFF.name(), R.drawable.ic_list_acpi_c);
		resources.put(MachineState.PAUSED.name(), R.drawable.ic_list_pause);
		resources_color.put(MachineState.PAUSED.name(), R.drawable.ic_list_pause_c);
		resources.put(MachineState.LIVE_SNAPSHOTTING.name(), R.drawable.ic_list_snapshot);
		resources_color.put(MachineState.LIVE_SNAPSHOTTING.name(), R.drawable.ic_list_snapshot_add_c);
		resources.put(MachineState.DELETING_SNAPSHOT.name(), R.drawable.ic_list_snapshot_del);
		resources_color.put(MachineState.DELETING_SNAPSHOT.name(), R.drawable.ic_list_snapshot_del_c);
		resources.put(MachineState.DELETING_SNAPSHOT_ONLINE.name(), R.drawable.ic_list_snapshot_del);
		resources_color.put(MachineState.DELETING_SNAPSHOT_ONLINE.name(), R.drawable.ic_list_snapshot_del_c);
		resources.put(MachineState.DELETING_SNAPSHOT_PAUSED.name(), R.drawable.ic_list_snapshot_del);
		resources_color.put(MachineState.DELETING_SNAPSHOT_PAUSED.name(), R.drawable.ic_list_snapshot_del_c);
		resources.put(MachineState.RESTORING_SNAPSHOT.name(), R.drawable.ic_list_snapshot);
		resources_color.put(MachineState.RESTORING_SNAPSHOT.name(), R.drawable.ic_list_snapshot_c);
		resources.put(MachineState.SAVING.name(), R.drawable.ic_list_save);
		resources_color.put(MachineState.SAVING.name(), R.drawable.ic_list_save_c);
		resources.put(MachineState.SAVED.name(), R.drawable.ic_list_save);
		resources_color.put(MachineState.SAVED.name(), R.drawable.ic_list_save_c);
		resources.put(MachineState.RESTORING.name(), R.drawable.ic_list_save);
		resources_color.put(MachineState.RESTORING.name(), R.drawable.ic_list_save_c);
		resources.put(MachineState.ABORTED.name(), R.drawable.ic_list_abort);
		resources_color.put(MachineState.ABORTED.name(), R.drawable.ic_list_abort_c);
		resources.put(MachineState.STUCK.name(), R.drawable.ic_list_stuck);
		resources_color.put(MachineState.STUCK.name(), R.drawable.ic_list_stuck_c);
		resources.put( VMAction.START.name(), R.drawable.ic_list_start );
		resources_color.put( VMAction.START.name(), R.drawable.ic_list_start_c );
		resources.put(VMAction.POWER_OFF.name(), R.drawable.ic_list_poweroff);
		resources_color.put(VMAction.POWER_OFF.name(), R.drawable.ic_list_poweroff_c);
		resources.put(VMAction.PAUSE.name(), R.drawable.ic_list_pause);
		resources_color.put(VMAction.PAUSE.name(), R.drawable.ic_list_pause_c);
		resources.put(VMAction.RESUME.name(), R.drawable.ic_list_start);
		resources_color.put(VMAction.RESUME.name(), R.drawable.ic_list_start_c);
		resources.put(VMAction.RESET.name(), R.drawable.ic_list_reset);
		resources_color.put(VMAction.RESET.name(), R.drawable.ic_list_reset_c);
		resources.put(VMAction.POWER_BUTTON.name(), R.drawable.ic_list_acpi );
		resources_color.put(VMAction.POWER_BUTTON.name(), R.drawable.ic_list_acpi_c );
		resources.put(VMAction.SAVE_STATE.name(), R.drawable.ic_list_save);
		resources_color.put(VMAction.SAVE_STATE.name(), R.drawable.ic_list_save_c);
		resources.put(VMAction.DISCARD_STATE.name(), R.drawable.ic_list_save);
		resources_color.put(VMAction.DISCARD_STATE.name(), R.drawable.ic_list_save_c);
		resources.put(VMAction.TAKE_SNAPSHOT.name(), R.drawable.ic_list_snapshot_add);
		resources_color.put(VMAction.TAKE_SNAPSHOT.name(), R.drawable.ic_list_snapshot_add_c);
		resources.put(VMAction.RESTORE_SNAPSHOT.name(), R.drawable.ic_list_snapshot);
		resources_color.put(VMAction.RESTORE_SNAPSHOT.name(), R.drawable.ic_list_snapshot_c);
		resources.put(VMAction.DELETE_SNAPSHOT.name(), R.drawable.ic_list_snapshot_del);
		resources_color.put(VMAction.DELETE_SNAPSHOT.name(), R.drawable.ic_list_snapshot_del_c);
		resources.put(VMAction.VIEW_METRICS.name(), R.drawable.ic_menu_metric);
		resources_color.put(VMAction.VIEW_METRICS.name(), R.drawable.ic_menu_metric);
	}

	/**
	 * @return black/white or colored icons based on Shared Preferences
	 */
	protected Map<String,Integer> getDrawables() {
		return Utils.getBooleanPreference(this, PreferencesActivity.ICON_COLORS)	? resources_color :  resources;
	}
	
	/**
	 * Get Drawable Resource based on the name
	 * @param name name of resource
	 * @return address of resource
	 */
	public int getDrawable(String name) {
		if(!getDrawables().containsKey(name)) {
			int id = getResources().getIdentifier(name, "drawable", getPackageName());
			getDrawables().put(name, id!=0 ? id : R.drawable.ic_list_os_other);
		}
		return getDrawables().get(name);
	}
	
	/**
	 * Get Drawable Resource based on the name
	 * @param name name of resource
	 * @return address of resource
	 */
	public int getDrawable(VMAction name) {
		return getDrawables().get(name.name());
	}
	
	/**
	 * Get resource drawable for given {@link MachineState}
	 * @param state name of resource
	 * @return address of resource
	 */
	public int getDrawable(MachineState state) {
		return getDrawables().containsKey(state.name())  ? getDrawables().get(state.name()) : R.drawable.ic_list_start;	
	}
	
	/**
	 * Get a color resource by name
	 * @param name name of color resource
	 * @return 0xAARRGGBB
	 */
	public static int getColor(Context ctx, String name) {
		if(!metricColor.containsKey(name)) 
			metricColor.put(name, ctx.getResources()
						.getColor(
								ctx.getResources().getIdentifier(name, 
								"color", 
								ctx.getPackageName())) );
		return metricColor.get(name);
	}
}
