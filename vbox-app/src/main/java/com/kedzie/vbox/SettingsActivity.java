package com.kedzie.vbox;

import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import android.view.MenuItem;
import com.kedzie.vbox.app.AppCompatPreferenceActivity;
import com.kedzie.vbox.app.Utils;

/**
 * Backwards compatible preferences with headers.  Also updates summary of current preference values.
 * 
 * @apiviz.stereotype activity
 */
@SuppressWarnings("deprecation")
public class SettingsActivity extends AppCompatPreferenceActivity implements OnSharedPreferenceChangeListener {

    public static final String PREF_ICON_COLORS="colored_icons";
    public static final String PREF_TAB_TRANSITION="tab_transition";
    public static final String PREF_NOTIFICATIONS = "notifications";
    public static final String PREF_WIDGET_INTERVAL = "widget_interval";
    public static final String PREF_PERIOD = "metric_period";
    public static final String PREF_COUNT = "metric_count";

    final static String ACTION_PREFS_GENERAL = "com.kedzie.vbox.prefs.GENERAL";
    final static String ACTION_PREFS_METRIC = "com.kedzie.vbox.prefs.METRIC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String action = getIntent().getAction();
        if (action != null && action.equals(ACTION_PREFS_GENERAL)) {
            addPreferencesFromResource(R.xml.general_preferences);
            if(getPreferenceScreen()!=null) {
                updateSummary(getPreferenceScreen().getSharedPreferences(), PREF_ICON_COLORS);
                updateSummary(getPreferenceScreen().getSharedPreferences(), PREF_TAB_TRANSITION);
                updateSummary(getPreferenceScreen().getSharedPreferences(), PREF_NOTIFICATIONS);
                updateSummary(getPreferenceScreen().getSharedPreferences(), PREF_WIDGET_INTERVAL);
            }
        } else if (action != null && action.equals(ACTION_PREFS_METRIC)) {
            addPreferencesFromResource(R.xml.metric_preferences);
            if(getPreferenceScreen()!=null) {
                updateSummary(getPreferenceScreen().getSharedPreferences(), PREF_PERIOD);
                updateSummary(getPreferenceScreen().getSharedPreferences(), PREF_COUNT);
            }
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            addPreferencesFromResource(R.xml.preference_headers_legacy);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    		case android.R.id.home:
    			finish();
    			return true;
    	}
    	return false;
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    protected void onPause() {
        if(getPreferenceScreen()!=null)
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(getPreferenceScreen()!=null)
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateSummary(sharedPreferences, key);
    }
    
    public void updateSummary(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SettingsActivity.PREF_PERIOD))
            findPreference(key).setSummary(sharedPreferences.getString(key, "") + " seconds");
        else if (key.equals(SettingsActivity.PREF_COUNT))
            findPreference(key).setSummary(sharedPreferences.getString(key, "") + " samples");
        else if (key.equals(SettingsActivity.PREF_WIDGET_INTERVAL))
            findPreference(key).setSummary(sharedPreferences.getString(key, "") + " ms");
        else if (key.equals(SettingsActivity.PREF_TAB_TRANSITION))
            findPreference(key).setSummary(sharedPreferences.getString(key, ""));
    }
    
    @Override
    public void finish() {
        super.finish();
        Utils.overrideBackTransition(this);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }

    public static class GeneralFragment extends SummaryPreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.general_preferences);
            updateSummary(getPreferenceScreen().getSharedPreferences(), PREF_ICON_COLORS);
            updateSummary(getPreferenceScreen().getSharedPreferences(), PREF_TAB_TRANSITION);
            updateSummary(getPreferenceScreen().getSharedPreferences(), PREF_NOTIFICATIONS);
            updateSummary(getPreferenceScreen().getSharedPreferences(), PREF_WIDGET_INTERVAL);
        }
    }

    public static class MetricFragment extends SummaryPreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.metric_preferences);
            updateSummary(getPreferenceScreen().getSharedPreferences(), PREF_PERIOD);
            updateSummary(getPreferenceScreen().getSharedPreferences(), PREF_COUNT);
        }
    }

    public static class SummaryPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updateSummary(sharedPreferences, key);
        }
        
        public void updateSummary(SharedPreferences sharedPreferences, String key) {
            if (key.equals(SettingsActivity.PREF_PERIOD))
                findPreference(key).setSummary(sharedPreferences.getString(key, "") + " seconds");
            else if (key.equals(SettingsActivity.PREF_COUNT))
                findPreference(key).setSummary(sharedPreferences.getString(key, "") + " samples");
            else if (key.equals(SettingsActivity.PREF_WIDGET_INTERVAL))
                findPreference(key).setSummary(sharedPreferences.getString(key, "") + " ms");
            else if (key.equals(SettingsActivity.PREF_TAB_TRANSITION))
                findPreference(key).setSummary(sharedPreferences.getString(key, ""));
        }
    }

}

