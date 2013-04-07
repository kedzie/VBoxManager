package com.kedzie.vbox.machine.settings;


import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;

public class GeneralFragment extends SherlockFragment {
    private FragmentTabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
        mTabHost.setCustomAnimations(R.animator.flip_left_in, R.animator.flip_left_out);
        mTabHost.addTab(mTabHost.newTabSpec("basic").setIndicator("Basic"), GeneralBasicFragment.class, getArguments());
        mTabHost.addTab(mTabHost.newTabSpec("description").setIndicator("Description"), GeneralDescriptionFragment.class, getArguments());
        return mTabHost;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }
}

