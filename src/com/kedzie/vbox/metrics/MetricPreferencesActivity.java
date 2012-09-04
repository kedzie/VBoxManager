package com.kedzie.vbox.metrics;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kedzie.vbox.R;

public class MetricPreferencesActivity extends SherlockFragmentActivity {
	public static final String PERIOD = "metric_period";
	public static final String COUNT = "metric_count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment() {
        	@Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.metric_preferences);
            }
        }).commit();
    }
}