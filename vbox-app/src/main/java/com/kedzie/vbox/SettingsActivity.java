package com.kedzie.vbox;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.view.MenuItem;

import com.kedzie.vbox.app.FragmentElement;
import com.kedzie.vbox.app.Utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

/**
 * Backwards compatible preferences with headers.  Also updates summary of current preference values.
 * 
 * @apiviz.stereotype activity
 */
public class SettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    public static final String PREF_ICON_COLORS="colored_icons";
    public static final String PREF_TAB_TRANSITION="tab_transition";
    public static final String PREF_NOTIFICATIONS = "notifications";
    public static final String PREF_WIDGET_INTERVAL = "widget_interval";
    public static final String PREF_PERIOD = "metric_period";
    public static final String PREF_COUNT = "metric_count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

        if(savedInstanceState == null) {
            Utils.replaceFragment(this, getSupportFragmentManager(), android.R.id.content,
                    new FragmentElement("headers", CategoryFragment.class, null));
        }
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
    public void finish() {
        super.finish();
        Utils.overrideBackTransition(this);
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        final Fragment fragment = Fragment.instantiate(this, pref.getFragment(), pref.getExtras());
        fragment.setTargetFragment(caller, 0);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
        return true;
    }

    public static class CategoryFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preference_headers, rootKey);
        }
    }

    public static class GeneralFragment extends SummaryPreferenceFragment {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.general_preferences, rootKey);
            updateSummary(getPreferenceScreen().getSharedPreferences(), PREF_TAB_TRANSITION);
            updateSummary(getPreferenceScreen().getSharedPreferences(), PREF_WIDGET_INTERVAL);
        }
    }

    public static class MetricFragment extends SummaryPreferenceFragment {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.metric_preferences, rootKey);
            updateSummary(getPreferenceScreen().getSharedPreferences(), PREF_PERIOD);
            updateSummary(getPreferenceScreen().getSharedPreferences(), PREF_COUNT);
        }
    }

    static abstract class SummaryPreferenceFragment extends PreferenceFragmentCompat implements OnSharedPreferenceChangeListener {

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

