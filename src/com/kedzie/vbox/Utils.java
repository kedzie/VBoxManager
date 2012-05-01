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
	
	public static boolean getBooleanPreference(Context ctx, String name) {
		return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(name, false);
	}
	
	public static String getStringPreference(Context ctx, String name) {
		return PreferenceManager.getDefaultSharedPreferences(ctx).getString(name, "");
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
