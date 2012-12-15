package com.kedzie.vbox.app;

import java.lang.annotation.Annotation;
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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kedzie.vbox.app.FragmentInfo.FragmentElement;

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
     * Get Class-level annotation
     * @param clazz	the class
     * @param a		type of annotation
     * @return		the annotation if found
     */
    @SuppressWarnings("unchecked")
	public static <T extends Annotation> T getAnnotation(Class<T> clazz, Annotation []a) {
		for(Annotation at : a)
			if(at.annotationType().equals(clazz))
				return (T)at;
		return null;
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
    public static int dpiToPixels(Context context, int dpi) {
        return (int) (context.getResources().getDisplayMetrics().density*dpi+.5f);
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
     * Detach an existing fragment
     * @param manager         fragment manager
     * @param tx                    existing transaction
     * @param tag                   tag of existing fragment
     */
    public static void detachExistingFragment(FragmentManager manager, FragmentTransaction tx, String tag) {
        if(isEmpty(tag)) return;
        Fragment existing = manager.findFragmentByTag(tag);
        if(existing!=null)
            tx.detach(existing);
    }
    
    /**
     * Instantiate or reattach existing Fragment
     * @param context          context
     * @param manager         fragment manager
     * @param tx                    existing transaction
     * @param containerId     container
     * @param info                  fragment definition
     */
    public static void addOrAttachFragment(Context context, FragmentManager manager, FragmentTransaction tx, int containerId, FragmentElement element) {
        if(element.fragment==null)
            element.fragment = manager.findFragmentByTag(element.name);
        if(element.fragment==null) {
            Log.i("FragmentManager",  "Instantiated new Fragment: " + element.name);
            tx.add(containerId, element.instantiate(context), element.name);
        } else {
            Log.i("FragmentManager",  "Reattaching existing Fragment: " + element.name);
            tx.attach(element.fragment);
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
     * Get screen layout size
     * @param config        the {@link Configuration}
     * @return                  screen size
     */
    public static int getScreenSize(Configuration config) {
        return config.screenLayout&Configuration.SCREENLAYOUT_SIZE_MASK;
    }
}
