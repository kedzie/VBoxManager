package com.kedzie.vbox.metrics;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.R.xml;

public class MetricPreferencesActivity extends PreferenceActivity {
	public static final String PERIOD = "metric_period";
	public static final String COUNT = "metric_count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.metric_preferences);
    }
    
}