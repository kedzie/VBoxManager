package com.kedzie.vbox.app;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kedzie.vbox.VBoxApplication;

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
		Toast.makeText(ctx, isEmpty(params) ? msg : String.format(msg, params), Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Show {@link Toast} short notification
	 * @param ctx message {@link Context}
	 * @param msg Message to show
	 */
	public static void toastShort(Context ctx, String msg, Object...params) {
		Toast.makeText(ctx, isEmpty(params) ? msg : String.format(msg, params), Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Check if String is <code>null</code> or empty string
	 * @param s    the string
	 * @return     <code>true</code> if empty string or null, <code>false</code> otherwise
	 */
	public static boolean isEmpty(String s) {
		return s==null || s.equals("");
	}
	
	/**
     * Check if array is <code>null</code> or empty
     * @param array    the array
     * @return              <code>true</code> if empty or null, <code>false</code> otherwise
     */
	public static boolean isEmpty(Object []array) {
		return array==null || array.length==0;
	}
	
	/**
     * Check if list is <code>null</code> or empty
     * @param list    the array
     * @return              <code>true</code> if empty or null, <code>false</code> otherwise
     */
    public static boolean isEmpty(List<?> list) {
        return list==null || list.isEmpty();
    }
	
	public static String arrayToString(Object []array) {
		StringBuilder sb = new StringBuilder("{ ");
		for(int i=0; i<array.length; i++) {
			sb.append(array[i].toString());
			if(i<array.length-1)	sb.append(", ");
		}
		return sb.append(" }").toString();
	}
	
	public static String arrayToString(int []array) {
        StringBuilder sb = new StringBuilder("{ ");
        for(int i=0; i<array.length; i++) {
            sb.append(array[i]+"");
            if(i<array.length-1)    sb.append(", ");
        }
        return sb.append(" }").toString();
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
	
    /**
     * Create an {@link IntentFilter} for multiple actions
     * @param actions   accepted actions
     * @return      the {@link IntentFilter}
     */
    public static IntentFilter createIntentFilter(String...actions) {
        IntentFilter filter = new IntentFilter();
        for(String action : actions)
            filter.addAction(action);
        return filter;
    }
    
    /**
     * Convert DPI to pixels
     * @param dpi  dpi value
     * @return equivilent pixel value
     */
    public static int dpiToPixels(int dpi) {
        return (int) (VBoxApplication.getInstance().getResources().getDisplayMetrics().density*dpi+.5f);
    }
    
    /**
     * Show a DialogFragment with Back Stack
     * @param manager    {@link FragmentManager}
     * @param tag           tag for {@link Fragment}
     * @param dialog        the {@link DialogFragment} implementation
     */
    public static void showDialog(FragmentManager manager, String tag, DialogFragment dialog) {
        FragmentTransaction tx = manager.beginTransaction();
        Fragment prev = manager.findFragmentByTag(tag);
        if(prev!=null)
            tx.remove(prev);
        tx.addToBackStack(tag);
        dialog.show(tx, tag);
    }
    
    /**
     * Instantiate or re-attach existing Fragment
     * @param context          context
     * @param manager         fragment manager
     * @param containerId     container
     * @param info                  fragment definition
     * @return      true of fragment was instantiated, false if reattached existing fragment
     */
    public static boolean  addOrAttachFragment(Context context, FragmentManager manager, int containerId, TabFragmentInfo info) {
        FragmentTransaction tx = manager.beginTransaction();
        boolean result = addOrAttachFragment(context, manager, tx, containerId, info);
        tx.commit();
        return result;
    }
    
    /**
     * Instantiate or reattach existing Fragment
     * @param context          context
     * @param manager         fragment manager
     * @param tx                    existing transaction
     * @param containerId     container
     * @param info                  fragment definition
     * @return      true of fragment was instantiated, false if reattached existing fragment
     */
    public static boolean addOrAttachFragment(Context context, FragmentManager manager, FragmentTransaction tx, int containerId, TabFragmentInfo info) {
        if(info.fragment==null)
            info.fragment = manager.findFragmentByTag(info.name);
        if(info.fragment==null) {
            tx.add(containerId, info.instantiate(context), info.name);
            return true;
        } else {
            tx.attach(info.fragment);
            return false;
        }
    }
    
    /**
     * Set the contents of a {@link TextView}
     * @param view     parent view
     * @param id           textview id
     * @param text     text contents
     */
    public static void setTextView(View parent, int id, String text) {
        ((TextView)parent.findViewById(id)).setText(text);
    }
    
    /**
     * Set the contents of a {@link ImageView}
     * @param view     parent view
     * @param id           textview id
     * @param image     contents
     */
    public static void setImageView(View parent, int id, Drawable image) {
        ((ImageView)parent.findViewById(id)).setImageDrawable(image);
    }
    
    /**
     * Set the contents of a {@link ImageView}
     * @param view     parent view
     * @param id           textview id
     * @param image     contents
     */
    public static void setImageView(View parent, int id, Bitmap image) {
        ((ImageView)parent.findViewById(id)).setImageBitmap(image);
    }
    
    /**
     * Is this a large screen in landscape configuration?
     * @param config    the {@link Configuration}
     * @return      true if is large screen in landscape orientation, false otherwise
     */
    public static boolean isLargeLand(Configuration config) {
        return config.orientation==Configuration.ORIENTATION_LANDSCAPE &&
                (config.screenLayout&Configuration.SCREENLAYOUT_SIZE_MASK)>=Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
