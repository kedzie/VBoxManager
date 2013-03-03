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
 * 
 * @author Marek Kędzierski
 */
public class TabSupportActionBarViewPager extends PagerAdapter  implements TabSupport, ActionBar.TabListener, ViewPager.OnPageChangeListener  {
	private static final String TAG = "TabSupportViewPager";

	private SherlockFragmentActivity mActivity;
	private ActionBar mActionBar;
    private ViewPager mViewPager;
    private List<FragmentElement> mTabs = new ArrayList<FragmentElement>();
    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction;
    private Fragment mCurrentPrimaryItem;
    
    public TabSupportActionBarViewPager(SherlockFragmentActivity activity, int container) {
        mFragmentManager = activity.getSupportFragmentManager();
        mActivity=activity;
        mActionBar=activity.getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        mViewPager=new ViewPager(mActivity);
        mViewPager.setId(99);
        mViewPager.setOffscreenPageLimit(4);
        if(container==android.R.id.content)
            mActivity.setContentView(mViewPager);
        else
            ((ViewGroup)mActivity.findViewById(container)).addView(mViewPager);
        mViewPager.setAdapter(this);
        mViewPager.setOnPageChangeListener(this);
    }

    @Override
    public void startUpdate(ViewGroup container) {
        mCurTransaction = Utils.setCustomAnimations(mFragmentManager.beginTransaction());
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        FragmentElement info = mTabs.get(position);
        Utils.addOrAttachFragment(mActivity, mFragmentManager, mCurTransaction, container.getId(), info);
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
        for(FragmentElement info : mTabs){
            if(info.fragment==object)
                return POSITION_UNCHANGED;
        }
        return POSITION_NONE;
    }
    
    @Override
    public int getCount() {
        return mTabs.size(); 
    }

    @Override
    public void addTab(FragmentElement info)  {
        Tab tab = mActionBar.newTab().setTag(info.name).setTabListener(this);
        if(info.icon!=-1)
            tab.setIcon(info.icon);
        else
            tab.setText(info.name);
       mActionBar.addTab( tab );
        mTabs.add(info);
        notifyDataSetChanged();
    }
    
    @Override
	public void removeTab(String name) {
        FragmentElement info = new FragmentElement(name, null, null);
    	mActionBar.removeTabAt(mTabs.indexOf(info));
    	mTabs.remove(info);
    	notifyDataSetChanged();
	}

	@Override
	public void removeAllTabs() {
		mActionBar.removeAllTabs();
		mTabs.clear();
		notifyDataSetChanged();
	}
	
	@Override
    public void setCurrentTab(int position) {
	    mActionBar.setSelectedNavigationItem(position);
    }
	
	@Override
    public void onPageSelected(int position) {
        mActivity.getSupportActionBar().setSelectedNavigationItem(position);
    }
	
	@Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        for (int i=0; i<mTabs.size(); i++) 
            if (mTabs.get(i).name == tab.getTag()) 
                mViewPager.setCurrentItem(i);
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
