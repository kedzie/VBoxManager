package com.kedzie.vbox.app;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import android.content.Context;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Android Utilities
 * @apiviz.stereotype utility
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
	 * Show {@link Toast} long notification with {@link String#format}
	 * @param ctx message {@link Context}
	 * @param msg Message to show
	 * @param formatting params
	 */
	public static void toastLong(Context ctx, String msg, Object...params) {
		Toast.makeText(ctx, isNullArray(params) ? msg : String.format(msg, params), Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Show {@link Toast} short notification
	 * @param ctx message {@link Context}
	 * @param msg Message to show
	 */
	public static void toastShort(Context ctx, String msg, Object...params) {
		Toast.makeText(ctx, isNullArray(params) ? msg : String.format(msg, params), Toast.LENGTH_SHORT).show();
	}
	
	public static boolean isNullString(String s) {
		return s==null || s.equals("");
	}
	
	public static boolean isNullArray(Object []array) {
		return array==null || array.length==0;
	}
	
	public static String arrayToString(Object []array) {
		StringBuilder sb = new StringBuilder("{ ");
		for(int i=0; i<array.length; i++) {
			sb.append(array[i].toString());
			if(i<array.length-1)	sb.append(", ");
		}
		return sb.append(" }").toString();
	}
	
	/**
	 * Get type parameter of Generic Type
	 * @param genericType the generic {@link Type}
	 * @return type parameter
	 */
	public static Class<?> getTypeParameter(Type genericType) {
		return getTypeParameter(genericType, 0);
	}
	
	/**
     * Get type parameter of Generic Type
     * @param genericType the generic {@link Type}
     * @param index which parameter
     * @return type parameter
     */
    public static Class<?> getTypeParameter(Type genericType, int index) {
        return (Class<?>)((ParameterizedType)genericType).getActualTypeArguments()[index];
    }
}
