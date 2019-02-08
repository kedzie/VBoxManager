package com.kedzie.vbox;

import android.os.Bundle;

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

        if(savedInstanceState == null) {
            Utils.replaceFragment(this, getSupportFragmentManager(), android.R.id.content,
                    new FragmentElement("headers", CategoryFragment.class, null));
        }
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

    public static class GeneralFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.general_preferences, rootKey);
        }
    }

    public static class MetricFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.metric_preferences, rootKey);
        }
    }

}

