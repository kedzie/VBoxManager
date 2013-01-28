package com.kedzie.vbox;


import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.kedzie.vbox.api.jaxb.MachineState;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.PreferencesActivity;

/**
 * Stores a resource map storing Operating System, VMAction, and MachineState Icons.
 * @author Marek Kedzierski
 * @apiviz.stereotype application
 */
public class VBoxApplication extends Application {
	private static final String TAG = "VBoxApplication";
	
    private Map<String,Integer> resources = new HashMap<String, Integer>();
	private Map<String,Integer> resources_color = new HashMap<String, Integer>();
	private Map<String, Integer> metricColor = new HashMap<String, Integer>();
	
	private static VBoxApplication _instance;
	
	public static VBoxApplication getInstance() {
	    return _instance;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		_instance=this;
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		PreferenceManager.setDefaultValues(this, R.xml.metric_preferences, false);
		
		putResource(MachineState.RUNNING.name(), R.drawable.ic_list_start, R.drawable.ic_list_start_c);		
		putResource(MachineState.STARTING.name(), R.drawable.ic_list_start, R.drawable.ic_list_start_c);		
		putResource(MachineState.STOPPING.name(), R.drawable.ic_list_acpi, R.drawable.ic_list_acpi_c);		
		putResource(MachineState.POWERED_OFF.name(), R.drawable.ic_list_acpi, R.drawable.ic_list_acpi_c);		
		putResource(MachineState.PAUSED.name(), R.drawable.ic_list_pause, R.drawable.ic_list_pause_c);		
		putResource(MachineState.LIVE_SNAPSHOTTING.name(), R.drawable.ic_list_snapshot, R.drawable.ic_list_snapshot_add_c);		
		putResource(MachineState.DELETING_SNAPSHOT.name(), R.drawable.ic_list_snapshot_del, R.drawable.ic_list_snapshot_del_c);		
		putResource(MachineState.DELETING_SNAPSHOT_ONLINE.name(), R.drawable.ic_list_snapshot_del, R.drawable.ic_list_snapshot_del_c);		
		putResource(MachineState.DELETING_SNAPSHOT_PAUSED.name(), R.drawable.ic_list_snapshot_del, R.drawable.ic_list_snapshot_del_c);		
		putResource(MachineState.RESTORING_SNAPSHOT.name(), R.drawable.ic_list_snapshot, R.drawable.ic_list_snapshot_c);		
		putResource(MachineState.SAVING.name(), R.drawable.ic_list_save, R.drawable.ic_list_save_c);		
		putResource(MachineState.SAVED.name(), R.drawable.ic_list_save, R.drawable.ic_list_save_c);		
		putResource(MachineState.RESTORING.name(), R.drawable.ic_list_save, R.drawable.ic_list_save_c);		
		putResource(MachineState.ABORTED.name(), R.drawable.ic_list_abort, R.drawable.ic_list_abort_c);		
		putResource(MachineState.STUCK.name(), R.drawable.ic_list_stuck, R.drawable.ic_list_stuck_c);		
		
		putResource( VMAction.START.name(), R.drawable.ic_list_start , R.drawable.ic_list_start_c );		
		putResource(VMAction.POWER_OFF.name(), R.drawable.ic_list_poweroff, R.drawable.ic_list_poweroff_c);		
		putResource(VMAction.PAUSE.name(), R.drawable.ic_list_pause, R.drawable.ic_list_pause_c);		
		putResource(VMAction.RESUME.name(), R.drawable.ic_list_start, R.drawable.ic_list_start_c);		
		putResource(VMAction.RESET.name(), R.drawable.ic_list_reset, R.drawable.ic_list_reset_c);		
		putResource(VMAction.POWER_BUTTON.name(), R.drawable.ic_list_acpi , R.drawable.ic_list_acpi_c );		
		putResource(VMAction.SAVE_STATE.name(), R.drawable.ic_list_save, R.drawable.ic_list_save_c);		
		putResource(VMAction.DISCARD_STATE.name(), R.drawable.ic_list_save, R.drawable.ic_list_save_c);		
		putResource(VMAction.TAKE_SNAPSHOT.name(), R.drawable.ic_list_snapshot_add, R.drawable.ic_list_snapshot_add_c);		
		putResource(VMAction.RESTORE_SNAPSHOT.name(), R.drawable.ic_list_snapshot, R.drawable.ic_list_snapshot_c);		
		putResource(VMAction.DELETE_SNAPSHOT.name(), R.drawable.ic_list_snapshot_del, R.drawable.ic_list_snapshot_del_c);		
		putResource(VMAction.VIEW_METRICS.name(), R.drawable.ic_menu_metrics, R.drawable.ic_menu_metrics);		
		putResource(VMAction.TAKE_SCREENSHOT.name(), R.drawable.ic_list_snapshot_add, R.drawable.ic_list_snapshot_add_c);
		putResource(VMAction.EDIT_SETTINGS.name(), R.drawable.ic_menu_edit, R.drawable.ic_menu_edit);
	}
	
	/**
	 * Populate the drawable cache
	 * @param name                     name of drawable resource
	 * @param bwResource           id of black & white drawable
	 * @param colorResource        id of color drawable
	 */
	private void putResource(String name, int bwResource, int colorResource) {
	    resources.put(name, bwResource);
	    resources_color.put(name,  colorResource);
	}

	/**
	 * @return black/white or colored icons based on Shared Preferences
	 */
	private Map<String,Integer> getDrawables() {
		return Utils.getBooleanPreference(this, PreferencesActivity.ICON_COLORS)	? resources_color :  resources;
	}
	
	/**
	 * Get Drawable Resource based on the name
	 * @param name name of resource
	 * @return address of resource
	 */
	private int getDrawable(String name) {
		if(!getDrawables().containsKey(name)) {
			int id = getResources().getIdentifier(name, "drawable", getPackageName());
			getDrawables().put(name, id!=0 ? id : R.drawable.ic_list_os_other);
		}
		return getDrawables().get(name);
	}
	
	/**
	 * Get {@link Drawable} for an Operating System
	 * @param osTypeId     Operating System type id
	 * @return     Android resource id
	 */
	public int getOSDrawable(String osTypeId) {
	    return getDrawable("ic_list_os_"+osTypeId.toLowerCase());
	}
	
	/**
	 * Get {@link Drawable} for a given {@link VMAction}
	 * @param name     The {@link VMAction}
	 * @return     Android resource id
	 */
	public int getDrawable(VMAction name) {
		return getDrawables().get(name.name());
	}
	
	/**
	 * Get {@link Drawable} for given {@link MachineState}
	 * @param state    The {@link MachineState}
	 * @return     Android resource id
	 */
	public int getDrawable(MachineState state) {
		return getDrawables().get(state.name());	
	}
	
	/**
	 * Get a color resource by name
	 * @param context  Android {@link Context}
	 * @param name     name of color resource
	 * @return     4 byte color value <code>(0xAARRGGBB)</code>
	 */
	public int getColor(Context context, String name) {
		if(!metricColor.containsKey(name)) 
			metricColor.put(name, context.getResources().getColor( context.getResources().getIdentifier(name, "color", context.getPackageName())) );
		return metricColor.get(name);
	}
	
	/**
	 * Is user authorized to edit machine settings?
	 * @return <code>true</code> if authorized, <code>false</code> otherwise
	 */
	public boolean isSettingsEnabled() {
		return true;
	}
	
	/**
	 * Launch activity with custom animations (if API > 16)
	 * @param parent		parent activity
	 * @param intent			intent to launch
	 */
	public static void launchActivity(Activity parent, Intent intent) {
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN)
    		parent.startActivity(intent, ActivityOptions.makeCustomAnimation(parent, R.anim.slide_in_right, R.anim.slide_out_left).toBundle());
    	else
    		parent.startActivity(intent);
	}
	
	/**
	 * Scale a bitmap to fit within the desired size
	 * @param bitmap	input bitmap
	 * @param width		desired width
	 * @param height	desired height
	 * @return	scaled bitmap which will fit in desired size
	 */
	public static Bitmap scale(Bitmap bitmap, int width, int height) {
        int bWidth=bitmap.getWidth(), bHeight=bitmap.getHeight();
        if(bWidth<=width && bHeight<=height) 
        	return bitmap;
        Log.d(TAG, String.format("Scaling bitmap (%1$dx%2$d) --> (%3$dx%4$d)", bWidth, bHeight, width, height));
        float wScale = ((float)width)/bWidth;
        float hScale = ((float)height)/bHeight;
        float scale = Math.min(wScale, hScale);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Log.d(TAG, "Scale factor: " + scale);
        return Bitmap.createBitmap(bitmap, 0, 0, bWidth, bHeight, matrix, true);
	}
}
