package com.kedzie.vbox;

import java.util.HashMap;
import java.util.Map;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Activity with ActionBar Tab navigation
 * @author Marek Kedzierski
 */
public class TabActivity extends SherlockFragmentActivity {
	
	protected Map<String, Tab> _tabs = new HashMap<String, Tab>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}
	
	protected void addTab(String name, Fragment f) {
		com.actionbarsherlock.app.ActionBar.Tab tab = getSupportActionBar().newTab();
        tab.setText(name);
        tab.setTag(name);
        tab.setTabListener(new TabListener(f, name));
        getSupportActionBar().addTab(tab);
        _tabs.put(name, tab);
	}
	
	protected void removeTab(String name) {
		getSupportActionBar().removeTab(_tabs.remove(name));
	}
	
	protected void removeAllTabs() {
		getSupportActionBar().removeAllTabs();
		_tabs.clear();
	}
	

	private class TabListener implements com.actionbarsherlock.app.ActionBar.TabListener {
        private Fragment _fragment;
        private String _tag;
        private int _containerId;

        public TabListener(Fragment fragment, String tag, int containerId) {
            _fragment = fragment;
            _tag=tag;
            _containerId=containerId;
        }
        
        public TabListener(Fragment fragment, String tag) {
            this(fragment, tag, android.R.id.content);
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
