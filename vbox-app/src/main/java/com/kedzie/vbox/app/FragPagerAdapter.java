package com.kedzie.vbox.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class FragPagerAdapter extends PagerAdapter {
    private static final String TAG = "FragPagerAdapter";
    
    private List<FragmentElement> mTabs = new ArrayList<FragmentElement>();
    private Context mContext;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction;
    private Fragment mCurrentPrimaryItem;
    
    public void setup(Context ctx, FragmentManager manager) {
        mContext=ctx;
        mFragmentManager=manager;
    }
    
    @Override
    public void startUpdate(ViewGroup container) {
        mCurTransaction = mFragmentManager.beginTransaction();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        FragmentElement info = mTabs.get(position);
        Utils.addOrAttachFragment(mContext, mFragmentManager, mCurTransaction, container.getId(), info);
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
    public Parcelable saveState() {
        return null; 
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {

    }
    
    public List<FragmentElement> getTabs() {
        return mTabs;
    }

    public void add(FragmentElement tab) {
        mTabs.add(tab);
        notifyDataSetChanged();
    }
    
    public void remove(FragmentElement tab) {
        mTabs.remove(tab);
        notifyDataSetChanged();
    }
    
    public void clear() {
        mTabs.clear();
        notifyDataSetChanged();
    }
    
    public Fragment getCurrentTab() {
        return mCurrentPrimaryItem;
    }
}
