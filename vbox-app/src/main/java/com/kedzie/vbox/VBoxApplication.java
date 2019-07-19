package com.kedzie.vbox;


import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseArray;

import androidx.fragment.app.Fragment;
import androidx.multidex.MultiDexApplication;
import androidx.preference.PreferenceManager;

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
import timber.log.Timber;

/**
 * Stores a resource map storing Operating System, VMAction, and MachineState Icons.
 * @author Marek Kedzierski
 * @apiviz.stereotype application
 */
public class VBoxApplication extends MultiDexApplication implements HasServiceInjector, HasActivityInjector, HasSupportFragmentInjector {

	private Map<String, Integer> metricColor = new HashMap<String, Integer>();

	@Inject
	DispatchingAndroidInjector<Service> dispatchingServiceInjector;

	@Inject
	DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

	@Inject
	DispatchingAndroidInjector<Fragment> dispatchingFragmentInjector;

	@Override
	public AndroidInjector<Activity> activityInjector() {
		return dispatchingActivityInjector;
	}

	@Override
	public AndroidInjector<Service> serviceInjector() {
		return dispatchingServiceInjector;
	}

	@Override
	public AndroidInjector<Fragment> supportFragmentInjector() {
		return dispatchingFragmentInjector;
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

		if(BuildConfig.DEBUG) {
			Timber.plant(new Timber.DebugTree());
		}

		if(appComponent == null) {
			setAppComponent(com.kedzie.vbox.dagger.DaggerAppComponent.builder()
					.androidServicesModule(new AndroidServicesModule(this))
					.build());
		}

		PreferenceManager.setDefaultValues(this, R.xml.general_preferences, true);
		PreferenceManager.setDefaultValues(this, R.xml.metric_preferences, true);
		Timber.d( "Period: %d", Utils.getIntPreference(this, SettingsActivity.PREF_PERIOD));
	}

	/**
	 * Get {@link Drawable} for an Operating System
	 * @param osTypeId     Operating System type id
	 * @return     Android resource id
	 */
	public static int getOSDrawable(Context context, String osTypeId) {
		int id = context.getResources().getIdentifier("ic_list_os_" + osTypeId.toLowerCase(), "drawable", context.getPackageName());
		return id != 0 ? id : R.drawable.ic_list_os_other;
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
}
