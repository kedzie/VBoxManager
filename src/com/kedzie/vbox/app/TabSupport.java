package com.kedzie.vbox.app;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;

/**
 * Actionbar tab navigation interface for different possible implementations
 * @author Marek Kędzierski
 * @apiviz.stereotype android
 */
public interface TabSupport {
	
	/**
	 * Add a tab to the {@link ActionBar}
	 * @param name	name & tag of  Tab
	 * @param clazz		type of Fragment
	 * @param args		Arguments
	 */
	public  void addTab(String name, Class<?> clazz, Bundle args) ;

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