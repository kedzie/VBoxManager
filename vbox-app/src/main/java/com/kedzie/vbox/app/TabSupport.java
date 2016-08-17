package com.kedzie.vbox.app;



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
	public abstract void addTab(FragmentElement info) ;

	/**
	 * Remove a tab from the {@link ActionBar}
	 * @param name name (& tag) of Tab
	 */
	public abstract void removeTab(String name);

	/**
	 * Remove all tabs from the {@link ActionBar}
	 */
	public abstract void removeAllTabs();
	
	public void setCurrentTab(int position);
}