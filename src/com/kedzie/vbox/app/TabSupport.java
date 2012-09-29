package com.kedzie.vbox.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.ActionBar;

/**
 * Actionbar tab navigation interface for different possible implementations
 * @author Marek Kędzierski
 * @apiviz.stereotype android
 */
public interface TabSupport {
	
	/**
	 * Definition of a {@link Fragment} which can be instantiated.
	 * @author Marek Kedzierski
	 */
	public static class TabFragmentInfo<T extends Fragment> {
		public final String _name;
		public final Class<T>  clazz;
	    public final Bundle args;

	    public TabFragmentInfo(String name, Class<T> _class, Bundle _args) {
	    	_name=name;
	        clazz = _class;
	        args = _args;
	    }
	    
	    /**
	     * Instantiate the {@link Fragment}
	     * @param context Android {@ Context}
	     * @return the instantiated {@link Fragment}
	     */
	    public T instantiate(Context context) {
	    	return clazz.cast(Fragment.instantiate(context, clazz.getName(), args));
	    }
	    
	    public boolean equals(Object that) {
	    	return  (that instanceof TabFragmentInfo)  && ((TabFragmentInfo<?>)that)._name.equals(_name);
	    }
	}

	/**
	 * Add a tab to the {@link ActionBar}
	 * @param name	name & tag of  Tab
	 * @param clazz		type of Fragment
	 * @param args		Arguments
	 */
	public <T extends Fragment> void addTab(String name, Class<T> clazz, Bundle args) ;

	/**
	 * Remove a tab from the {@link ActionBar}
	 * @param name name (& tag) of Tab
	 */
	public void removeTab(String name);

	/**
	 * Remove all tabs from the {@link ActionBar}
	 */
	public void removeAllTabs();
}