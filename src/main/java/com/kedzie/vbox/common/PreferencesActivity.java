package com.kedzie.vbox.common;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.kedzie.vbox.R;

public class PreferencesActivity extends PreferenceActivity {
	public static final String PERIOD = "metric_period", COUNT = "metric_count", ICON_COLORS="metric_icons";
	public static final int PERIOD_DEFAULT = 1, COUNT_DEFAULT = 25;
	public static final boolean ICON_COLORS_DEFAULT = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
    
}