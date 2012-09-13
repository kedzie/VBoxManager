package com.kedzie.vbox;

import java.io.IOException;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.kedzie.vbox.api.IManagedObjectRef;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Handles preference changes and reconfigures metrics and/or event handlers
 * @author Marek Kędzierski
 */
public class PreferenceHandler implements OnSharedPreferenceChangeListener {
	private static final String TAG = "PreferenceHandler";
	VBoxSvc _vmgr;
	private LocalBroadcastManager lbm;
	private EventNotificationReceiver eventNotifier;
	
	public PreferenceHandler(VBoxSvc api, Context c) {
		_vmgr=api;
		lbm = LocalBroadcastManager.getInstance(c);
		eventNotifier = new EventNotificationReceiver();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if(key.equals(MetricPreferencesActivity.COUNT) || key.equals(MetricPreferencesActivity.PERIOD)) {
			new ConfigureMetricsTask().execute(
					prefs.getInt(MetricPreferencesActivity.PERIOD, 2),
					prefs.getInt(MetricPreferencesActivity.COUNT, 20));
		} else if(key.equals(PreferencesActivity.NOTIFICATIONS)) {
			if(prefs.getBoolean(PreferencesActivity.NOTIFICATIONS, false))
				lbm.registerReceiver(eventNotifier, new IntentFilter(VBoxEventType.ON_MACHINE_STATE_CHANGED.name()));
			else
				lbm.unregisterReceiver(eventNotifier);
		}
	}

	class ConfigureMetricsTask extends AsyncTask<Integer, Void, Void> {
		@Override
		protected Void doInBackground(Integer... params) {
			int period=params[0], count=params[1];
			Log.i(TAG, String.format("Configuring metrics: period = %d, count = %d", period, count));
			try {
				_vmgr.getVBox().getPerformanceCollector().setupMetrics(new String[] { "*:" }, period, count, (IManagedObjectRef)null);
			} catch (IOException e) {
				Log.e(TAG, "Exception configuring metrics", e);
			}
			return null;
		}
	}
}
