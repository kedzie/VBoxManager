package com.kedzie.vbox.app;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.kedzie.vbox.SettingsActivity;

/**
 * {@link FragmentPagerAdapter} which is integrated with {@link ActionBar} tab navigation.
 * 
 * @author Marek Kędzierski
 */
public class TabSupportActionBarViewPager  implements TabSupport, ActionBar.TabListener, ViewPager.OnPageChangeListener  {

	private AppCompatActivity mActivity;
	private ActionBar mActionBar;
    private ViewPager mViewPager;
    private FragPagerAdapter mAdapter;
    
    public TabSupportActionBarViewPager(AppCompatActivity activity, int container) {
        mActivity=activity;
        mActionBar=activity.getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
      
        mViewPager=new ViewPager(mActivity);
        mViewPager.setId(99);
        mViewPager.setOffscreenPageLimit(4);
        String transition = Utils.getStringPreference(mActivity, SettingsActivity.PREF_TAB_TRANSITION);
        if(transition.equals("Flip"))
            mViewPager.setPageTransformer(false, new FlipPageTransformer());
        else if(transition.equals("Slide"))
            mViewPager.setPageTransformer(false, new ZoomOutPageTransformer());
        else if(transition.equals("Accordian"))
            mViewPager.setPageTransformer(false, new AccordianPageTransformer());
        if(container==android.R.id.content)
            mActivity.setContentView(mViewPager);
        else
            ((ViewGroup)mActivity.findViewById(container)).addView(mViewPager);

        mAdapter = new FragPagerAdapter();
        mAdapter.setup(mActivity, activity.getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
    }

    @Override
    public void addTab(FragmentElement info)  {
        ActionBar.Tab tab = mActionBar.newTab().setTag(info.name).setTabListener(this);
        if(info.icon!=-1)
            tab.setIcon(info.icon);
        else
            tab.setText(info.name);
       mActionBar.addTab( tab );
       mAdapter.add(info);
    }
    
    @Override
	public void removeTab(String name) {
        FragmentElement info = new FragmentElement(name, null, null);
        mActionBar.removeTabAt(mAdapter.getTabs().indexOf(info));
        mAdapter.remove(info);
	}

	@Override
	public void removeAllTabs() {
		mActionBar.removeAllTabs();
		mAdapter.clear();
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
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
//	    mViewPager.setCurrentItem(mAdapter.getTabs().indexOf(new FragmentElement((String)tab.getTag(), null, null)));
        for (int i=0; i<mAdapter.getTabs().size(); i++) 
            if (mAdapter.getTabs().get(i).name == tab.getTag()) 
                mViewPager.setCurrentItem(i);
    }
	
    @Override public void onPageScrollStateChanged(int state) {}
    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    @Override public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {}
    @Override public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {}
}
