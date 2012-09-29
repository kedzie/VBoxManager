package com.kedzie.vbox.app;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kedzie.vbox.R;

/**
 * Attaches/Detaches Fragments
 * @author Marek Kędzierski
 */
public class TabSupportFragment implements TabSupport {
	private static final String TAG = "FragmentTabSupport";

	private class TabListener implements ActionBar.TabListener {
		/** Fragment tag */
		private String _tag;
		/** Definition of Fragment before instantiation */
		private TabFragmentInfo<?> _definition;
        /** Already created instance */
        private Fragment _fragment;
        
        public TabListener(TabFragmentInfo<?> info, String tag) {
            _definition = info;
            _tag=tag;
            // if fragment exists remove it.. might be from previous machine
            if ((_fragment=_activity.getSupportFragmentManager().findFragmentByTag(_tag)) != null) {
            	Log.w(TAG, "Detaching existing Fragment for tab: " + _tag);
                FragmentTransaction ft = _activity.getSupportFragmentManager().beginTransaction();
                ft.remove(_fragment);
                ft.commit();
                _fragment=null;
            }
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
        	ft.setCustomAnimations(R.anim.stack_enter, R.anim.stack_exit);
        	if (_fragment==null) {
        		Log.i(TAG, String.format(" Instantiating new fragment [%s]", _tag));
                _fragment = Fragment.instantiate(_activity, _definition.clazz.getName(), _definition.args);
                ft.add(_fragmentContainer, _fragment, _tag);
            } else {
            	Log.i(TAG, String.format("re-Attaching existing fragment [%s]", _tag));
                ft.attach(_fragment);
            }
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        	ft.setCustomAnimations(R.anim.stack_enter, R.anim.stack_exit);
        	ft.detach(_fragment);
        }

        @Override public void onTabReselected(Tab tab, FragmentTransaction ft) { }
    }
	
	/** Keep track of tabs so they can be removed */
	protected Map<String, Tab> _tabs = new HashMap<String, Tab>();
	/** View which will host the Fragments */
	protected int _fragmentContainer;
	protected SherlockFragmentActivity _activity;
	protected FragmentManager _manager;
	
	/**
	 * @param activity  The {@link SherlockFragmentActivity}
	 * @param container target container for {@link Fragment}
	 */
	public TabSupportFragment(SherlockFragmentActivity activity, int container) {
		_activity = activity;
		_fragmentContainer=container;
		_manager = _activity.getSupportFragmentManager();
	}
	
	@Override
	public <T extends Fragment> void addTab(String name, Class<T > clazz, Bundle args)  {
        TabFragmentInfo<T> info = new TabFragmentInfo<T>(name, clazz, args);
        Tab tab =  _activity.getSupportActionBar().newTab().setText(name).setTag(name).setTabListener(new TabListener(info, name));
        _tabs.put(name,tab);
        _activity.getSupportActionBar().addTab(tab);
	}
	
	@Override
	public void removeTab(String name) {
		_activity.getSupportActionBar().removeTab(_tabs.remove(name));
	}
	
	@Override
	public void removeAllTabs() {
		_activity.getSupportActionBar().removeAllTabs();
		_tabs.clear();
	}
}

