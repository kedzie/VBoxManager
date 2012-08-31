package com.kedzie.vbox.tabs;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;

public class FragmentTabSupport implements TabSupport {
	private static final String TAG = "FragmentTabSupport";

	/** Keep track of tabs so they can be removed */
	protected Map<String, Tab> _tabs = new HashMap<String, Tab>();
	protected SherlockFragmentActivity _activity;
	protected ActionBar _actionBar;
	protected int _containerId;
	
	/**
	 * @param activity  The {@link SherlockFragmentActivity}
	 * @param container target container for {@link Fragment}
	 */
	public FragmentTabSupport(SherlockFragmentActivity activity) {
		this(activity, android.R.id.content);
	}
	
	/**
	 * @param activity  The {@link SherlockFragmentActivity}
	 * @param container target container for {@link Fragment}
	 */
	public FragmentTabSupport(SherlockFragmentActivity activity, int container) {
		_activity = activity;
		_containerId=container;
		_actionBar=activity.getSupportActionBar();
	}
	
	@Override
	public <T extends Fragment> void addTab(String name, Class<T > clazz, Bundle args)  {
        TabFragmentInfo<T> info = new TabFragmentInfo<T>(name, clazz, args);
        Tab tab =  _actionBar.newTab().setText(name).setTag(name).setTabListener(new TabListener(info, name));
        _tabs.put(name,tab);
        _actionBar.addTab(tab);
	}
	
	@Override
	public void removeTab(String name) {
		_actionBar.removeTab(_tabs.remove(name));
	}
	
	@Override
	public void removeAllTabs() {
		_actionBar.removeAllTabs();
		_tabs.clear();
	}

	private class TabListener implements ActionBar.TabListener {
		private TabFragmentInfo<?> _info;
        private String _tag;
        /** Already created {@link Fragment} */
        private Fragment _fragment;
        
        public TabListener(TabFragmentInfo<?> info, String tag) {
            _info = info;
            _tag=tag;
            // if already fragment, probably from saved state, deactivate it because initial state tab isn't shown.
            _fragment = _activity.getSupportFragmentManager().findFragmentByTag(_tag);
            if (_fragment != null && !_fragment.isDetached()) {
            	Log.w(TAG, "Detaching existing Fragment for tab: " + _tag);
                FragmentTransaction ft = _activity.getSupportFragmentManager().beginTransaction();
                ft.detach(_fragment);
                ft.commit();
            }
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
        	if (_fragment == null) {
        		Log.i(TAG, String.format("onTabSelected(%s) - Instantiating new fragment", _tag));
                _fragment = Fragment.instantiate(_activity, _info.clazz.getName(), _info.args);
                ft.add(_containerId, _fragment, _tag);
            } else {
            	Log.i(TAG, String.format("onTabSelected(%s) - Attaching existing fragment", _tag));
                ft.attach(_fragment);
            }
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        	if (_fragment != null) 
                ft.detach(_fragment);
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) { }
    }
}
