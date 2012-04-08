package com.kedzie.vbox;

import android.content.Context;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Android Utilities
 */
public class Utils {

	public static int getIntPreference(Context ctx, String name) {
		return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(ctx).getString(name, ""));
	}
	
	public static int getPeriodPreference(Context ctx) {
		return getIntPreference(ctx, PreferencesActivity.PERIOD);
	}
	
	public static int getCountPreference(Context ctx) {
		return getIntPreference(ctx, PreferencesActivity.COUNT);
	}
	
	public static boolean getColoredIconsPreference(Context ctx) {
		boolean ret =  PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(PreferencesActivity.ICON_COLORS, false);
		return ret;
	}
	
	public static boolean getNotificationsPreference(Context ctx) {
		boolean ret =  PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(PreferencesActivity.NOTIFICATIONS, false);
		return ret;
	}
	
	public static boolean getBetaEnabledPreference(Context ctx) {
		boolean ret = PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(PreferencesActivity.BETA_ENABLED, false);
		return ret;
	}
	
	/**
	 * Show {@link Toast} notification
	 * @param ctx message {@link Context}
	 * @param msg Message to show
	 */
	public static void toast(Context ctx, String msg) {
		Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
	}
	
	public static boolean isNullString(String s) {
		return s==null || s.equals("");
	}
	
	public static boolean isNullArray(Object []array) {
		return array==null || array.length==0;
	}
}
