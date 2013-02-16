package com.kedzie.vbox.app;

import java.util.ArrayList;
import java.util.List;

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
public class TabSupportActionBarViewPager extends PagerAdapter  implements TabSupport, ActionBar.TabListener, ViewPager.OnPageChangeListener  {
	private static final String TAG = "TabSupportViewPager";

	private SherlockFragmentActivity _activity;
	private ActionBar _actionBar;
    private ViewPager _viewPager;
    private List<FragmentElement> _tabs = new ArrayList<FragmentElement>();
    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction;
    private Fragment mCurrentPrimaryItem;
    
    public TabSupportActionBarViewPager(SherlockFragmentActivity activity, int container) {
        mFragmentManager = activity.getSupportFragmentManager();
        _activity=activity;
        _actionBar=activity.getSupportActionBar();
        _actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        _viewPager=new ViewPager(_activity);
        _viewPager.setId(99);
        _viewPager.setOffscreenPageLimit(4);
        if(container==android.R.id.content)
            _activity.setContentView(_viewPager);
        else
            ((ViewGroup)_activity.findViewById(container)).addView(_viewPager);
        _viewPager.setAdapter(this);
        _viewPager.setOnPageChangeListener(this);
    }

    @Override
    public void startUpdate(ViewGroup container) {
        mCurTransaction = mFragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        FragmentElement info = _tabs.get(position);
        Utils.addOrAttachFragment(_activity, mFragmentManager, mCurTransaction, container.getId(), info);
        if (info.fragment != mCurrentPrimaryItem) {
            info.fragment.setMenuVisibility(false);
            info.fragment.setUserVisibleHint(false);
        }
        return info.fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.d(TAG, "Removing item #" + position);
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
    public int getItemPosition(Object object) {
        for(FragmentElement info : _tabs){
            if(info.fragment==object)
                return POSITION_UNCHANGED;
        }
        return POSITION_NONE;
    }
    
    @Override
    public int getCount() {
        return _tabs.size(); 
    }

    @Override
    public void addTab(FragmentElement info)  {
        Tab tab = _actionBar.newTab().setTag(info.name).setTabListener(this);
        if(info.icon!=-1)
            tab.setIcon(info.icon);
        else
            tab.setText(info.name);
       _actionBar.addTab( tab );
        _tabs.add(info);
        notifyDataSetChanged();
    }
    
    @Override
	public void removeTab(String name) {
        FragmentElement info = new FragmentElement(name, null, null);
    	_actionBar.removeTabAt(_tabs.indexOf(info));
    	_tabs.remove(info);
    	notifyDataSetChanged();
	}

	@Override
	public void removeAllTabs() {
		_actionBar.removeAllTabs();
		_tabs.clear();
		notifyDataSetChanged();
	}
	
	@Override
    public void setCurrentTab(int position) {
	    _actionBar.setSelectedNavigationItem(position);
    }
	
	@Override
    public void onPageSelected(int position) {
        _activity.getSupportActionBar().setSelectedNavigationItem(position);
    }
	
	@Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        for (int i=0; i<_tabs.size(); i++) 
            if (_tabs.get(i).name == tab.getTag()) 
                _viewPager.setCurrentItem(i);
    }
	
	@Override
    public Fragment getCurrentFragment() {
	    return mCurrentPrimaryItem;
    }
	
	@Override public Parcelable saveState() {return null; }
    @Override public void restoreState(Parcelable state, ClassLoader loader) {}
    @Override public void onPageScrollStateChanged(int state) {}
    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    @Override public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
    @Override public void onTabReselected(Tab tab, FragmentTransaction ft) {}
}
