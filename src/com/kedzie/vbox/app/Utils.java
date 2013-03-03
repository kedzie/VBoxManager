package com.kedzie.vbox.app;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kedzie.vbox.BuildConfig;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMedium;

/**
 * Android Utilities
 * 
 * @apiviz.stereotype utility
 */
@ThreadSafe
public class Utils {
	
	/**
	 * Provides default implementations for {@link AnimationListener}
	 */
	public static class AnimationAdapter implements AnimationListener {
		@Override public void onAnimationStart(Animation animation) {}
		@Override public void onAnimationEnd(Animation animation) {}
		@Override public void onAnimationRepeat(Animation animation) {}
	}
	
	/**
	 * Provides default implementations for {@link TextWatcher}
	 */
	public static abstract class TextAdapter implements TextWatcher {
		@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		@Override public void afterTextChanged(Editable editable) {}
	}
	
	/**
	 * Provides default implementations for {@link OnItemSelectedListener}
	 */
	public static abstract class OnItemSelectedAdapter implements OnItemSelectedListener {
		@Override public void onNothingSelected(AdapterView<?> parent) {}
	}
	
	public static boolean isIceCreamSandwhich() {
		return isVersion(Build.VERSION_CODES.ICE_CREAM_SANDWICH);
	}
	
	public static boolean isJellyBean() {
		return isVersion(Build.VERSION_CODES.JELLY_BEAN);
	}
	
	/**
	 * <code>true</code> if matches minimum API level
	 * 
	 * @param versionCode		minimum API version code
	 * @return	<code>true</code> if API level matches
	 */
	public static boolean isVersion(int versionCode) {
		return Build.VERSION.SDK_INT>=versionCode;
	}

    public static int getIntPreference(Context context, String name) {
		return Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(name, "0"));
	}
	
	public static boolean getBooleanPreference(Context context, String name) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(name, false);
	}
	
	public static String getStringPreference(Context context, String name) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(name, "");
	}
	
	/**
	 * Show {@link Toast} long notification with {@link String#format}
	 * @param context message {@link Context}
	 * @param message Message to show
	 * @param formatting params
	 */
	public static void toastLong(Context context, String message, Object...params) {
		Toast.makeText(context, isEmpty(params) ? message : String.format(message, params), Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Show {@link Toast} short notification
	 * @param context message {@link Context}
	 * @param message Message to show
	 */
	public static void toastShort(Context context, String message, Object...params) {
		Toast.makeText(context, isEmpty(params) ? message : String.format(message, params), Toast.LENGTH_SHORT).show();
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
    
    /**
     * Make a nice printout of map contents.
     * @param title		title to show above contents
     * @param map	the data
     * @return	string representation
     */
    public static String toString(String title, Map<?,?> map) {
    	StringBuffer buf = new StringBuffer(title).append("\n=========================\n{");
    	for(Map.Entry<?,?> entry : map.entrySet())
    		buf.append(entry.getKey()).append("==>").append(entry.getValue()).append("\n");
    	return buf.append("}\n").toString();
    }
	
	/**
	 * Append an element to a comma separated string
	 * @param base			base string
	 * @param append		appended element
	 * @return	base string with appended element, with comma if needed
	 */
	public static StringBuffer appendWithComma(StringBuffer base, String append) {
		if(base.length()>0)
			base.append(", ");
		return base.append(append);
	}
	
	/**
     * Get type parameter of Generic Type
     * @param genericType 			the generic {@link Type}
     * @param index 						which parameter
     * @return type 						parameter
     */
    public static Class<?> getTypeParameter(Type genericType, int index) {
    	if(!(genericType instanceof ParameterizedType))
    		return null;
        return (Class<?>)((ParameterizedType)genericType).getActualTypeArguments()[index];
    }
    
    /**
     * Search an array for a specific type of annotation
     * @param clazz		the class
     * @param a				type of annotation
     * @return				the annotation or <code>null</code> if not found
     */
	public static <T extends Annotation> T getAnnotation(Class<T> clazz, Annotation []annotations) {
		for(Annotation a : annotations)
			if(a.annotationType().equals(clazz))
				return clazz.cast(a);
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
     * Specify fragment transition animations depending on API level
     * @param tx		the transaction
     * @return			the transaction 
     */
    public static FragmentTransaction setCustomAnimations(FragmentTransaction tx) {
    	//if(Utils.isVersion(Build.VERSION_CODES.HONEYCOMB))
    	//	return tx.setCustomAnimations(R.animator.flip_left_in, R.animator.flip_right_out);
    	//else
    		return tx.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    
    /**
     * Show a DialogFragment with Back Stack
     * @param manager		{@link FragmentManager}
     * @param tag					tag for {@link Fragment}
     * @param dialog				the {@link DialogFragment} implementation
     */
    public static void showDialog(FragmentManager manager, String tag, DialogFragment dialog) {
    	FragmentTransaction tx = manager.beginTransaction().addToBackStack(null);
        Fragment prev = manager.findFragmentByTag(tag);
        if(prev!=null)
            tx.remove(prev);
        dialog.show(tx, tag);
    }
    
    /**
     * Detach an existing fragment
     * @param manager		fragment manager
     * @param tx					existing transaction
     * @param tag					<code>tag</code> of existing fragment
     */
    public static void detachExistingFragment(FragmentManager manager, FragmentTransaction tx, String tag) {
        if(isEmpty(tag)) 
        	throw new IllegalArgumentException("tag cannot be null");
        Fragment existing = manager.findFragmentByTag(tag);
        if(existing!=null)
            tx.detach(existing);
    }
    
    /**
     * Detach an existing fragment
     * @param manager		fragment manager
     * @param tx					existing transaction
     * @param id					container id
     */
    public static void detachExistingFragment(FragmentManager manager, FragmentTransaction tx, int id) {
        Fragment existing = manager.findFragmentById(id);
        if(existing!=null)
            tx.detach(existing);
    }
    
    /**
     * Instantiate or reattach existing Fragment
     * @param context          context
     * @param manager         fragment manager
     * @param containerId     container
     * @param info                  fragment definition
     */
    public static void addOrAttachFragment(Context context, FragmentManager manager, int containerId, FragmentElement element) {
    	FragmentTransaction tx = manager.beginTransaction();
    	addOrAttachFragment(context, manager, tx, containerId, element);
    	tx.commit();
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
            if(BuildConfig.DEBUG) Log.v("FragmentManager",  "Instantiated new Fragment: " + element.name);
            tx.add(containerId, element.instantiate(context), element.name);
        } else {
        	if(BuildConfig.DEBUG) Log.v("FragmentManager",  "Reattaching existing Fragment: " + element.name);
            tx.attach(element.fragment);
        }
    }
    
    /**
     * Remove existing fragment with same tag and add new one.
     * @param context          context
     * @param manager         fragment manager
     * @param containerId     container
     * @param info                  fragment definition
     */
    public static void addFragment(Context context, FragmentManager manager, int containerId, FragmentElement element) {
    	FragmentTransaction tx = manager.beginTransaction();
    	addFragment(context, manager, tx, containerId, element);
    	tx.commit();
    }
    
    /**
     * Remove existing fragment with same tag and add new one.
     * @param context          context
     * @param manager         fragment manager
     * @param tx                    existing transaction
     * @param containerId     container
     * @param info                  fragment definition
     */
    public static void addFragment(Context context, FragmentManager manager, FragmentTransaction tx, int containerId, FragmentElement element) {
    	Fragment existing = manager.findFragmentByTag(element.name);
    	if(existing!=null)
    		tx.remove(existing);
    	Log.d("FragmentManager",  "Instantiated new Fragment: " + element.name);
    	tx.add(containerId, element.instantiate(context), element.name);
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
     * Set the contents of a {@link TextView}
     * @param view     parent view
     * @param id           textview id
     * @param text     text contents
     */
    public static void setTextView(View parent, int id, int text) {
        ((TextView)parent.findViewById(id)).setText(text+"");
    }
    
    /**
     * Set the contents of a {@link TextView}
     * @param holder    view holder
     * @param id           textview id
     * @param text     text contents
     */
    public static void setTextView(SparseArray<View> holder, int id, String text) {
        ((TextView)holder.get(id)).setText(text);
    }
    
    /**
     * Set the contents of a {@link TextView}
     * @param holder    view holder
     * @param id           textview id
     * @param text     text contents
     */
    public static void setTextView(SparseArray<View> holder, int id, int text) {
        ((TextView)holder.get(id)).setText(text+"");
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
     * Perform a {@link Thread#sleep} and ignore any {@link InterruptedException}
     */
    public static void sleep(int time) {
    	try {
    		Thread.sleep(time);
    	} catch(InterruptedException e) {}
    }
    
    /**
     * Get screen layout size
     * @param config        the {@link Configuration}
     * @return                  screen size
     */
    public static int getScreenSize(Configuration config) {
        return config.screenLayout&Configuration.SCREENLAYOUT_SIZE_MASK;
    }
    
    /**
     * Find an element within an array
     * @param array			the array
     * @param object		the object to find
     * @return	the index of the object in array, or -1 if not found
     */
    public static int indexOf(Object[] array, Object object) {
    	for(int i=0; i<array.length; i++) {
    		if(array[i].equals(object))
    			return i;
    	}
    	return -1;
    }
    
    /**
     * Remove "NULL" element of a <code>enum</code> array
     * @param array		array of <code>enum.values()</code>
     * @return	array with the "NULL" element removed
     */
    @SuppressWarnings("unchecked")
	public static <T> T[] removeNull(T[] array) {
    	T[] ret = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length-1);
    	int retIndex = 0;
    	for(int i=0; i<array.length; i++) {
    		if(!array[i].toString().equals("Null"))
    			ret[retIndex++] = array[i];
    	}
    	return ret;
    }
    
    /**
	 * Cache commonly used Medium properties
	 * @param m
	 */
    public static void cacheProperties(IMedium medium) {
    	synchronized(medium) {
    		medium.getName();
    		medium.getSize();
    		medium.getType();
			medium.getLocation();
			medium.getLogicalSize();
			medium.getBase().getName();
			medium.getBase().getSize();
			medium.getBase().getType();
			medium.getBase().getLocation();
			medium.getBase().getLogicalSize();
    	}
    }
    
    /**
	 * Cache commonly used Machine properties
	 * @param m
	 */
	public static void cacheProperties(IMachine m) {
		synchronized(m) {
			m.clearCacheNamed("getName", "getState", "getCurrentStateModified", "gotOSTypeId", "getCurrentSnapshot");
			m.getName();
			m.getState();
			m.getCurrentStateModified(); 
			m.getOSTypeId();
			if(m.getCurrentSnapshot()!=null) 
				m.getCurrentSnapshot().getName();
		}
	}
}
