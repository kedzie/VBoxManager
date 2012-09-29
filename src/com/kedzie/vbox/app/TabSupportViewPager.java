package com.kedzie.vbox.app;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * {@link FragmentPagerAdapter} which is integrated with {@link ActionBar} tab navigation.
 * @author Marek Kedzierski
 */
public class TabSupportViewPager extends FragmentPagerAdapter  implements TabSupport, ActionBar.TabListener, ViewPager.OnPageChangeListener  {
	private static final String TAG = "TabSupportViewPager";

	private SherlockFragmentActivity _activity;
	private ActionBar _actionBar;
    private ViewPager _viewPager;
    private List<TabFragmentInfo<?>> _tabs = new ArrayList<TabFragmentInfo<?>>();
	
    public TabSupportViewPager(SherlockFragmentActivity activity, ViewPager pager) {
        super(activity.getSupportFragmentManager());
        _activity=activity;
        _actionBar=activity.getSupportActionBar();
        _viewPager=pager;
        _viewPager.setAdapter(this);
        _viewPager.setOnPageChangeListener(this);
    }
    
    @Override
    public int getCount() {
        return _tabs.size(); 
    }

    @Override
    public Fragment getItem(int position) {
        Log.i(TAG, "getItem #"+position);
        TabFragmentInfo<?> info = _tabs.get(position);
        return Fragment.instantiate(_activity, info.clazz.getName(), info.args);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.i(TAG, "detach fragment #"+position);
        super.destroyItem(container, position, object);
    }
    
    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    @Override
    public <T extends Fragment> void addTab(String name, Class<T> clazz, Bundle args)  {
        TabFragmentInfo<T> info = new TabFragmentInfo<T>(name, clazz, args);
       _actionBar.addTab( _actionBar.newTab().setText(name).setTag(info).setTabListener(this) );
        _tabs.add(info);
        notifyDataSetChanged();
    }
    
    @Override
	public void removeTab(String name) {
    	TabFragmentInfo<?> info = new TabFragmentInfo<Fragment>(name, null, null);
    	int index = _tabs.indexOf(info);
    	_activity.getSupportActionBar().removeTabAt(index);
    	_tabs.remove(info);
    	notifyDataSetChanged();
    	Fragment f = _activity.getSupportFragmentManager().findFragmentByTag(makeFragmentName(_viewPager.getId(), index));
    	Log.i(TAG, "Existing fragment: " + f);
	}

	@Override
	public void removeAllTabs() {
		for(int i=0; i<_tabs.size(); i++) {
	    	Fragment f = _activity.getSupportFragmentManager().findFragmentByTag(makeFragmentName(_viewPager.getId(),i));
	    	if(f!=null) {
	    		FragmentTransaction t = _activity.getSupportFragmentManager().beginTransaction();
				t.remove(f);
				t.commit();
				Log.i(TAG, "removed Fragment from Manager: " + f);
	    	}
		}
		_activity.getSupportActionBar().removeAllTabs();
		_tabs.clear();
		notifyDataSetChanged();
	}
	
	@Override
    public void onPageSelected(int position) {
        _activity.getSupportActionBar().setSelectedNavigationItem(position);
    }
	
	@Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        for (int i=0; i<_tabs.size(); i++) 
            if (_tabs.get(i) == tab.getTag()) 
                _viewPager.setCurrentItem(i);
    }
	
    @Override public void onPageScrollStateChanged(int state) {}
    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    @Override public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
    @Override public void onTabReselected(Tab tab, FragmentTransaction ft) {}
}
