package com.kedzie.vbox;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;

/**
 * Activity with ActionBar Tab navigation
 * @author Marek Kedzierski
 */
public class TabActivity extends SherlockFragmentActivity {
	
	protected Map<String, Tab> _tabs = new HashMap<String, Tab>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}
	
	/**
	 * Add a tab to the {@link ActionBar}
	 * @param containerId	the id of the {@link ViewContainer} which will hold the {@link Fragment}
	 * @param name name (& tag) of {@link Tab}
	 * @param f the {@link Fragment}
	 */
	public void addTab(String name, Fragment f, int containerId) {
		Tab tab = getSupportActionBar().newTab();
        tab.setText(name);
        tab.setTag(name);
        tab.setTabListener(new TabListener(f, name, containerId));
        getSupportActionBar().addTab(tab);
        _tabs.put(name, tab);
	}
	
	/**
	 * Add a tab to the {@link ActionBar}
	 * @param name name (& tag) of {@link Tab}
	 * @param f the {@link Fragment}
	 */
	public void addTab(String name, Fragment f) {
		addTab(name, f, android.R.id.content);
	}
	
	/**
	 * Remove a tab from the {@link ActionBar}
	 * @param name name (& tag) of Tab
	 */
	public void removeTab(String name) {
		getSupportActionBar().removeTab(_tabs.remove(name));
	}
	
	/**
	 * Remove all tabs from the {@link ActionBar}
	 */
	public void removeAllTabs() {
		getSupportActionBar().removeAllTabs();
		_tabs.clear();
	}

	protected class TabListener implements ActionBar.TabListener {
        private Fragment _fragment;
        private String _tag;
        private int _containerId;

        public TabListener(Fragment fragment, String tag, int containerId) {
            _fragment = fragment;
            _tag=tag;
            _containerId=containerId;
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            ft.add(_containerId, _fragment, _tag);
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            ft.remove(_fragment);
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }
    }
}
