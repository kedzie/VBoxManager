package com.kedzie.vbox.app;

import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

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
		private FragmentElement _definition;
        
        public TabListener(FragmentElement info, String tag) {
            _definition = info;
            _tag=tag;
            // if fragment exists remove it.. might be from previous machine
            Fragment fragment=_activity.getSupportFragmentManager().findFragmentByTag(_tag);
            if (fragment != null) {
            	Log.w(TAG, "Removing existing Fragment for tab: " + _tag);
                FragmentTransaction ft = _activity.getSupportFragmentManager().beginTransaction();
                ft.remove(fragment);
                ft.commit();
            }
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
        	ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        	if (_definition.fragment==null) {
        		Log.i(TAG, String.format(" Instantiating new fragment [%s]", _tag));
                ft.add(_fragmentContainer, _definition.instantiate(_activity), _tag);
            } else {
            	Log.i(TAG, String.format("re-Attaching existing fragment [%s]", _tag));
                ft.attach(_definition.fragment);
            }
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        	ft.detach(_definition.fragment);
        }

        @Override public void onTabReselected(Tab tab, FragmentTransaction ft) { }
    }
	
	/** Keep track of tabs so they can be removed */
	protected Map<String, Tab> _tabs = new HashMap<String, Tab>();
	/** View which will host the Fragments */
	protected int _fragmentContainer;
	protected SherlockFragmentActivity _activity;
	protected  ActionBar _actionBar;
	protected FragmentManager _manager;
	
	/**
	 * @param activity  The {@link SherlockFragmentActivity}
	 * @param container target container for {@link Fragment}
	 */
	public TabSupportFragment(SherlockFragmentActivity activity, int container) {
		_activity=activity;
		_actionBar = activity.getSupportActionBar();
		_manager = activity.getSupportFragmentManager();
		_actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		_fragmentContainer=container;
	}
	
	@Override
	public void addTab(FragmentElement info)  {
	    Tab tab = _actionBar.newTab().setTag(info.name).setTabListener(new TabListener(info, info.name));
        if(info.view!=null)
            tab.setCustomView(info.view);
        else if(info.icon!=-1)
            tab.setIcon(info.icon);
        else
            tab.setText(info.name);
        _tabs.put(info.name,tab);
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
	
	@Override
    public void setCurrentTab(int position) {
	    _actionBar.setSelectedNavigationItem(position);
    }   
	
	@Override
    public Fragment getCurrentFragment() {
        return _manager.findFragmentById(_fragmentContainer);
    }
}

