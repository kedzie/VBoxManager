package com.kedzie.vbox.tabs;

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
public class TabSupportViewPager implements TabSupport  {
	private static final String TAG = "TabSupportViewPager";
	
	private final SherlockFragmentActivity _activity;
    private final ViewPager _viewPager;
    final List<TabFragmentInfo<?>> _tabs = new ArrayList<TabFragmentInfo<?>>();
    private FragmentPagerAdapter _adapter;

    public TabSupportViewPager(SherlockFragmentActivity activity, ViewPager pager) {
        _activity=activity;
        _viewPager=pager;
        _adapter =  new FragmentPagerAdapter(_activity.getSupportFragmentManager()) {
    		@Override
        	public int getCount() {
    			return _tabs.size(); 
    		}
        	
    		@Override
        	public Fragment getItem(int position) {
        		TabFragmentInfo<?> info = _tabs.get(position);
        		return Fragment.instantiate(_activity, info.clazz.getName(), info.args);
        	}
        	
    		@Override
        	public void destroyItem(ViewGroup container, int position, Object object) {
        		super.destroyItem(container, position, object);
        		Log.i(TAG, "destroyItem #"+position);
        		Fragment f = (Fragment)object; 
        		FragmentTransaction ft = _activity.getSupportFragmentManager().beginTransaction();
        		ft.remove(f);
        		ft.commit();
        	}
    	};
        _viewPager.setAdapter(_adapter);
        _viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        	@Override
        	public void onPageSelected(int position) {
        		_activity.getSupportActionBar().setSelectedNavigationItem(position);
        	}
        	@Override public void onPageScrollStateChanged(int state) {}
        	@Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        });
    }

    @Override
    public <T extends Fragment> void addTab(String name, Class<T> clazz, Bundle args)  {
        TabFragmentInfo<T> info = new TabFragmentInfo<T>(name, clazz, args);
       _activity.getSupportActionBar().addTab( _activity.getSupportActionBar().newTab().setText(name).setTag(info)
    		   .setTabListener(new ActionBar.TabListener() {
    			   @Override
    				public void onTabSelected(Tab tab, FragmentTransaction ft) {
    			        for (int i=0; i<_tabs.size(); i++) {
    			            if (_tabs.get(i) == tab.getTag()) 
    			                _viewPager.setCurrentItem(i);
    			        }
    				}
    				@Override public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
    				@Override public void onTabReselected(Tab tab, FragmentTransaction ft) {}
			}) );
        _tabs.add(info);
        _adapter.notifyDataSetChanged();
    }
    
    @Override
	public void removeTab(String name) {
    	TabFragmentInfo<?> info = new TabFragmentInfo<Fragment>(name, null, null);
    	_activity.getSupportActionBar().removeTabAt( _tabs.indexOf(info));
    	_tabs.remove(info);
    	_adapter.notifyDataSetChanged();
	}

	@Override
	public void removeAllTabs() {
		_activity.getSupportActionBar().removeAllTabs();
		_tabs.clear();
		_adapter.notifyDataSetChanged();
	}
}
