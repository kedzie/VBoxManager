package com.kedzie.vbox.machine;

import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.kedzie.vbox.R;

/**
 * Backwards compatible preferences with headers.  Also updates summary of current preference values.
 * 
 * @apiviz.stereotype activity
 */
@SuppressWarnings("deprecation")
public class PreferencesActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {
	private static final String TAG = "PreferencesActivity";
	
	public static final String ICON_COLORS="colored_icons";
	public static final String NOTIFICATIONS = "notifications";
	public static final String PREF_WIDGET_INTERVAL = "widget_interval";
	public static final String PERIOD = "metric_period";
	public static final String COUNT = "metric_count";
	
	final static String ACTION_PREFS_GENERAL = "com.kedzie.vbox.prefs.GENERAL";
	final static String ACTION_PREFS_METRIC = "com.kedzie.vbox.prefs.METRIC";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String action = getIntent().getAction();
        if (action != null && action.equals(ACTION_PREFS_GENERAL)) {
            addPreferencesFromResource(R.xml.general_preferences);
        } else if (action != null && action.equals(ACTION_PREFS_METRIC)) {
            addPreferencesFromResource(R.xml.metric_preferences);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            addPreferencesFromResource(R.xml.preference_headers_legacy);
        }
    }
    
    @Override
    public void onBuildHeaders(List<Header> target) {
    	loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    protected void onPause() {
    	PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    	super.onResume();
    }
    
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.d(TAG, "Preference changed: " + key);
		if (key.equals(PREF_WIDGET_INTERVAL)) {
            Preference pref = findPreference(key);
            pref.setSummary(sharedPreferences.getString(key, ""));
        } else if (key.equals(PERIOD)) {
            Preference pref = findPreference(key);
            pref.setSummary(sharedPreferences.getString(key, ""));
        } 
	}
	
	public static class GeneralFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.general_preferences);
		}
	}
	
	public static class MetricFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.metric_preferences);
		}
	}
}