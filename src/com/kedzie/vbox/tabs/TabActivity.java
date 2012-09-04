package com.kedzie.vbox.tabs;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;

/**
 * Activity with ActionBar Tab navigation
 * @author Marek Kedzierski
 */
public class TabActivity extends SherlockFragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		 if (savedInstanceState != null) 
	            getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getSupportActionBar().getSelectedNavigationIndex());
    }
}
