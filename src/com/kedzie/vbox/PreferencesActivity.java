package com.kedzie.vbox;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.kedzie.vbox.R;

public class PreferencesActivity extends PreferenceActivity {
	public static final String PERIOD = "metric_period";
	public static final String COUNT = "metric_count";
	public static final String ICON_COLORS="colored_icons";
	public static final String BETA_ENABLED="beta";
	public static final String NOTIFICATIONS = "notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
    
}