package com.kedzie.vbox

import android.os.Bundle

import com.kedzie.vbox.app.FragmentElement
import com.kedzie.vbox.app.Utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsCategoryFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        setPreferencesFromResource(R.xml.preference_headers, rootKey)
    }
}

class SettingsGeneralFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        setPreferencesFromResource(R.xml.general_preferences, rootKey)
    }

    companion object {
        const val PREF_NOTIFICATIONS = "notifications"
        const val PREF_WIDGET_INTERVAL = "widget_interval"
    }
}

class SettingsMetricFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        setPreferencesFromResource(R.xml.metric_preferences, rootKey)
    }

    companion object {
        const val PREF_METRICS_ENABLED = "metric_enabled"
        const val PREF_METRIC_PERIOD = "metric_period"
        const val PREF_METRIC_COUNT = "metric_count"
    }
}

