package com.kedzie.vbox;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.kedzie.vbox.R;

public class MetricPreferencesActivity extends SherlockPreferenceActivity {
	public static final String PERIOD = "metric_period";
	public static final String COUNT = "metric_count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.metric_preferences);
    }
}