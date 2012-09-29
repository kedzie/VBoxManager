package com.kedzie.vbox.machine;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.kedzie.vbox.R;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype activity
 */
public class PreferencesActivity extends SherlockPreferenceActivity {
	public static final String ICON_COLORS="colored_icons";
	public static final String NOTIFICATIONS = "notifications";

    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}