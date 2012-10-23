package com.kedzie.vbox.app;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * {@link FragmentPagerAdapter} which is integrated with {@link ActionBar} tab navigation.
 * @author Marek Kedzierski
 */
public class TabSupportViewPager extends PagerAdapter  implements TabSupport, ActionBar.TabListener, ViewPager.OnPageChangeListener  {
	private static final String TAG = "TabSupportViewPager";

	private SherlockFragmentActivity _activity;
	private ActionBar _actionBar;
    private ViewPager _viewPager;
    private List<TabFragmentInfo> _tabs = new ArrayList<TabFragmentInfo>();
    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction;
    private Fragment mCurrentPrimaryItem;
    
    public TabSupportViewPager(SherlockFragmentActivity activity, ViewPager pager) {
        mFragmentManager = activity.getSupportFragmentManager();
        _activity=activity;
        _actionBar=activity.getSupportActionBar();
        _viewPager=pager;
        _viewPager.setAdapter(this);
        _viewPager.setOnPageChangeListener(this);
    }

    @Override
    public void startUpdate(ViewGroup container) {
        mCurTransaction = mFragmentManager.beginTransaction();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String tag = makeFragmentName(container.getId(), position);
        Fragment fragment = mFragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            Log.i(TAG, "Attaching item #" + position);
            mCurTransaction.attach(fragment);
        } else {
            fragment = _tabs.get(position).instantiate(_activity);
            Log.i(TAG, "Adding item #" + position);
            mCurTransaction.add(container.getId(), fragment, tag);
        }
        if (fragment != mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.i(TAG, "Detaching item #" + position);
        mCurTransaction.remove((Fragment)object);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }
    
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment)object).getView() == view;
    }

    @Override
    public int getCount() {
        return _tabs.size(); 
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    @Override
    public void addTab(String name, Class<?> clazz, Bundle args)  {
        TabFragmentInfo info = new TabFragmentInfo(name, clazz, args);
       _actionBar.addTab( _actionBar.newTab().setText(name).setTag(info).setTabListener(this) );
        _tabs.add(info);
        notifyDataSetChanged();
    }
    
    @Override
	public void removeTab(String name) {
    	TabFragmentInfo info = new TabFragmentInfo(name, null, null);
    	int index = _tabs.indexOf(info);
    	_activity.getSupportActionBar().removeTabAt(index);
    	_tabs.remove(info);
    	notifyDataSetChanged();
    	Fragment f = _activity.getSupportFragmentManager().findFragmentByTag(makeFragmentName(_viewPager.getId(), index));
    	Log.i(TAG, "Existing fragment: " + f);
	}

	@Override
	public void removeAllTabs() {
//		for(int i=0; i<_tabs.size(); i++) {
//	    	Fragment f = _activity.getSupportFragmentManager().findFragmentByTag(makeFragmentName(_viewPager.getId(),i));
//	    	if(f!=null) {
//	    		FragmentTransaction t = _activity.getSupportFragmentManager().beginTransaction();
//				t.remove(f);
//				t.commit();
//				Log.i(TAG, "removed Fragment from Manager: " + f);
//	    	}
//		}
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
	
	@Override public Parcelable saveState() {return null; }
    @Override public void restoreState(Parcelable state, ClassLoader loader) {}
    @Override public void onPageScrollStateChanged(int state) {}
    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    @Override public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
    @Override public void onTabReselected(Tab tab, FragmentTransaction ft) {}
}
