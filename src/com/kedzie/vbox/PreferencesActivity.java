package com.kedzie.vbox;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class PreferencesActivity extends SherlockPreferenceActivity {
	public static final String ICON_COLORS="colored_icons";
	public static final String NOTIFICATIONS = "notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}