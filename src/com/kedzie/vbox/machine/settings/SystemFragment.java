package com.kedzie.vbox.machine.settings;


import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.kedzie.vbox.R;

public class SystemFragment extends SherlockFragment {
    private FragmentTabHost mTabHost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabs, container, false);
        mTabHost = (FragmentTabHost)view.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("motherboard").setIndicator("Motherboard"), SystemMotherboardFragment.class, getArguments());
        mTabHost.addTab(mTabHost.newTabSpec("processors").setIndicator("Processors"), SystemProcessorsFragment.class, getArguments());
        mTabHost.addTab(mTabHost.newTabSpec("acceleration").setIndicator("Acceleration"), SystemAccelerationFragment.class, getArguments());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }
}

