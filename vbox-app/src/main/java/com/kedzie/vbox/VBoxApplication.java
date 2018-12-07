package com.kedzie.vbox;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;

import com.kedzie.vbox.api.jaxb.MachineState;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.dagger.AndroidServicesModule;
import com.kedzie.vbox.dagger.AppComponent;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Stores a resource map storing Operating System, VMAction, and MachineState Icons.
 * @author Marek Kedzierski
 * @apiviz.stereotype application
 */
public class VBoxApplication extends MultiDexApplication implements HasServiceInjector, HasActivityInjector {
	private static final String TAG = "VBoxApplication";
	private static final String APPLICATION_PRO_KEY_PACKAGE = "com.kedzie.vbox.pro";
	private static final String APPLICATION_PACKAGE = "com.kedzie.vbox";

    private Map<String,Integer> resources = new HashMap<String, Integer>();
	private Map<String,Integer> resources_color = new HashMap<String, Integer>();
	private SparseArray<Integer> generalResources = new SparseArray<Integer>();
	private SparseArray<Integer> generalResources_color = new SparseArray<Integer>();
	private Map<String, Integer> metricColor = new HashMap<String, Integer>();

	@Inject
	DispatchingAndroidInjector<Service> dispatchingServiceInjector;

	@Inject
	DispatchingAndroidInjector<Activity> dispatchingActivityInjector;



	@Override
	public AndroidInjector<Activity> activityInjector() {
		return dispatchingActivityInjector;
	}

	@Override
	public AndroidInjector<Service> serviceInjector() {
		return dispatchingServiceInjector;
	}

	private static VBoxApplication _instance;
	
	public static VBoxApplication getInstance() {
	    return _instance;
	}

	private AppComponent appComponent;

	public AppComponent getAppComponent() { return appComponent; }

	public void setAppComponent(AppComponent ap) {
		appComponent = ap;
		appComponent.inject(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		_instance=this;

		if(appComponent == null) {
			setAppComponent(com.kedzie.vbox.dagger.DaggerAppComponent.builder()
					.androidServicesModule(new AndroidServicesModule(this))
					.build());
		}

		PreferenceManager.setDefaultValues(this, R.xml.general_preferences, true);
		PreferenceManager.setDefaultValues(this, R.xml.metric_preferences, true);
		Log.i(TAG, "Period: " + Utils.getIntPreference(this, SettingsActivity.PREF_PERIOD));
		
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
		//TODO make new drawables for following states
		putResource(MachineState.TELEPORTED.name(), R.drawable.ic_list_start, R.drawable.ic_list_start_c);		
		putResource(MachineState.TELEPORTING.name(), R.drawable.ic_list_start, R.drawable.ic_list_start_c);		
		putResource(MachineState.TELEPORTING_IN.name(), R.drawable.ic_list_start, R.drawable.ic_list_start_c);		
		putResource(MachineState.TELEPORTING_PAUSED_VM.name(), R.drawable.ic_list_start, R.drawable.ic_list_start_c);		
		putResource(MachineState.FIRST_ONLINE.name(), R.drawable.ic_list_start, R.drawable.ic_list_start_c);		
		putResource(MachineState.FIRST_TRANSIENT.name(), R.drawable.ic_list_start, R.drawable.ic_list_start_c);		
		putResource(MachineState.LAST_ONLINE.name(), R.drawable.ic_list_start, R.drawable.ic_list_start_c);		
		putResource(MachineState.LAST_TRANSIENT.name(), R.drawable.ic_list_start, R.drawable.ic_list_start_c);		
		putResource(MachineState.SETTING_UP.name(), R.drawable.ic_list_start, R.drawable.ic_list_start_c);
		
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
		putResource(VMAction.EDIT_SETTINGS.name(), R.drawable.ic_menu_settings, R.drawable.ic_menu_edit_settings_c);
		
		putResource(R.drawable.ic_button_hdd, R.drawable.ic_button_hdd, R.drawable.ic_button_hdd_c);
		putResource(R.drawable.ic_menu_hdd_add, R.drawable.ic_menu_hdd_add, R.drawable.ic_menu_hdd_add_c);
		putResource(R.drawable.ic_button_dvd, R.drawable.ic_button_dvd, R.drawable.ic_button_dvd_c);
		putResource(R.drawable.ic_menu_dvd_add, R.drawable.ic_menu_dvd_add, R.drawable.ic_menu_dvd_add_c);
	}
	
	/**
	 * Populate the drawable cache
	 * @param name                     name of drawable resource
	 * @param bwResource			B/W drawable id
	 * @param colorResource		Color drawable id
	 */
	private void putResource(String name, int bwResource, int colorResource) {
	    resources.put(name, bwResource);
	    resources_color.put(name,  colorResource);
	}
	
	/**
	 * Populate the drawable cache
	 * @param id                     name of drawable resource
	 * @param bwResource			B/W drawable id
	 * @param colorResource		Color drawable id
	 */
	private void putResource(int id, int bwResource, int colorResource) {
	    generalResources.put(id, bwResource);
	    generalResources_color.put(id,  colorResource);
	}

	/**
	 * Get correct drawable <code>Map</code> based on user's preferences
	 * @return B/W or Color icons, depending on Shared Preferences
	 */
	private Map<String,Integer> getDrawables() {
		return Utils.getBooleanPreference(this, SettingsActivity.PREF_ICON_COLORS)	? resources_color :  resources;
	}
	
	/**
	 * Get correct drawable <code>Map</code> based on user's preferences
	 * @return B/W or Color icons, depending on Shared Preferences
	 */
	private SparseArray<Integer> getGeneralDrawables() {
		return Utils.getBooleanPreference(this, SettingsActivity.PREF_ICON_COLORS)	? generalResources_color :  generalResources;
	}
	
	/**
	 * Get identifer of a <code>Drawable</code> resource from the name
	 * @param name		resource name
	 * @return				resource identifier
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
	 * Get {@link Drawable}
	 * @param id    The id
	 * @return     Android resource id
	 */
	public int getVDrawable(int id) {
		return getGeneralDrawables().get(id);	
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
	 * Is the Premium Key application installed?
	 * @return <code>true</code> if premium user, <code>false</code> otherwise
	 */
	public boolean isPremiumVersion() {
		return true; //getPackageManager().checkSignatures(APPLICATION_PACKAGE, APPLICATION_PRO_KEY_PACKAGE)==PackageManager.SIGNATURE_MATCH;
	}
	
	/**
	 * Show a dialog offer user option to open Google Play Store to install 'VirtualBox Manager Premium Key'
	 * @param context		Android context
	 */
	public void showPremiumOffer(final Context context) {
		new AlertDialog.Builder(context)
			.setCancelable(true)
			.setTitle(R.string.pro_key_required_dialog_title)
			.setMessage(R.string.pro_key_required_dialog_message)
			.setPositiveButton(R.string.pro_key_required_dialog_ok_button, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					try {
					    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+APPLICATION_PRO_KEY_PACKAGE)));
					} catch (ActivityNotFoundException e) {
					    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+APPLICATION_PRO_KEY_PACKAGE)));
					}
				}
			})
			.setNegativeButton(R.string.pro_key_required_dialog_cancel_button, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.show();
	}


}
