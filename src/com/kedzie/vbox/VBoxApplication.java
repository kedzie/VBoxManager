package com.kedzie.vbox;


import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import com.kedzie.vbox.api.jaxb.MachineState;

/**
 * 
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
		resources.put( "Start", R.drawable.ic_list_start );
		resources_color.put( "Start", R.drawable.ic_list_start_c );
		resources.put("Power Off", R.drawable.ic_list_poweroff);
		resources_color.put("Power Off", R.drawable.ic_list_poweroff_c);
		resources.put("Pause", R.drawable.ic_list_pause);
		resources_color.put("Pause", R.drawable.ic_list_pause_c);
		resources.put("Resume", R.drawable.ic_list_start);
		resources_color.put("Resume", R.drawable.ic_list_start_c);
		resources.put("Reset", R.drawable.ic_list_reset);
		resources_color.put("Reset", R.drawable.ic_list_reset_c);
		resources.put("Power Button", R.drawable.ic_list_acpi );
		resources_color.put("Power Button", R.drawable.ic_list_acpi_c );
		resources.put("Save State", R.drawable.ic_list_save);
		resources_color.put("Save State", R.drawable.ic_list_save_c);
		resources.put("Discard State", R.drawable.ic_list_save);
		resources_color.put("Discard State", R.drawable.ic_list_save_c);
		resources.put("Take Snapshot", R.drawable.ic_list_snapshot_add);
		resources_color.put("Take Snapshot", R.drawable.ic_list_snapshot_add_c);
		resources.put("Restore Snapshot", R.drawable.ic_list_snapshot);
		resources_color.put("Restore Snapshot", R.drawable.ic_list_snapshot_c);
		resources.put("Delete Snapshot", R.drawable.ic_list_snapshot_del);
		resources_color.put("Delete Snapshot", R.drawable.ic_list_snapshot_del_c);
	}

	/**
	 * @return black/white or colored icons based on Shared Preferences
	 */
	protected Map<String,Integer> getDrawables() {
		return Utils.getColoredIconsPreference(this)	? resources_color :  resources;
	}
	
	/**
	 * Get Drawable Resource based on the name
	 * @param name name of resource
	 * @return address of resource
	 */
	public int getDrawableResource(String name) {
		if(!getDrawables().containsKey(name)) {
			int id = getResources().getIdentifier(name, "drawable", getPackageName());
			getDrawables().put(name, id!=0 ? id : R.drawable.ic_list_os_other);
		}
		return getDrawables().get(name);
	}
	
	/**
	 * Get resource drawable for given {@link MachineState}
	 * @param state name of resource
	 * @return address of resource
	 */
	public int getDrawableResource(MachineState state) {
		return getDrawables().containsKey(state.name())  ? getDrawables().get(state.name()) : R.drawable.ic_list_start;	
	}
	
	/**
	 * Which actions can be performed on a Virtual Machine for each {@link MachineState}
	 * @param state virtual machine state
	 * @return actions which can be performed
	 */
	public String[] getActions(MachineState state) {
		if(state.equals(MachineState.RUNNING)) return new String[] { "Pause", "Reset", "Power Off" , "Power Button", "Save State", "Take Snapshot" };
		 else if (state.equals(MachineState.POWERED_OFF) || state.equals(MachineState.ABORTED))	return new String[] { "Start",  "Take Snapshot" };
		else if (state.equals(MachineState.PAUSED))	return new String[] { "Resume", "Reset", "Power Off", "Take Snapshot" };
		 else if (state.equals(MachineState.SAVED))	return new String[] { "Restore State", "Discard State" };
		return new String[] {};
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
