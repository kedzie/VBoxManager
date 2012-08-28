package com.kedzie.vbox.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;

public interface TabSupport {

	/**
	 * Add a tab to the {@link ActionBar}
	 * @param name name (& tag) of {@link Tab}
	 * @param f the {@link Fragment}
	 */
	public void addTab(String name, Class<? extends Fragment> clazz, Bundle args) ;

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