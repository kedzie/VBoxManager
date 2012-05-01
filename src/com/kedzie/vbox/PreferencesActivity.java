package com.kedzie.vbox;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.actionbarsherlock.app.SherlockActivity;

public class PreferencesActivity extends SherlockActivity {
	public static final String PERIOD = "metric_period";
	public static final String COUNT = "metric_count";
	public static final String METRIC_IMPLEMENTATION="metric_implementation";
	public static final String ICON_COLORS="colored_icons";
	public static final String NOTIFICATIONS = "notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
    }
    
    public static class PrefsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}