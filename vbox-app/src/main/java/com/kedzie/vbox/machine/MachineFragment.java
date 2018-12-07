package com.kedzie.vbox.machine;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kedzie.vbox.R;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.FragmentElement;
import com.kedzie.vbox.app.PagerTabHost;
import com.kedzie.vbox.soap.VBoxSvc;

public class MachineFragment extends Fragment {

    private VBoxSvc mVmgr;
    private PagerTabHost mTabHost;
    private boolean mDualPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVmgr = BundleBuilder.getVBoxSvc(getArguments());
        mDualPane = getArguments().getBoolean("dualPane");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTabHost = new PagerTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);
        Bundle args =  getArguments();
        args.putBoolean("dualPane", mDualPane);
        mTabHost.addTab(new FragmentElement("Details", R.drawable.ic_menu_info_details, InfoFragment.class, args));
        mTabHost.addTab(new FragmentElement("Actions", ActionsFragment.class, args));
        mTabHost.addTab(new FragmentElement("Log", LogFragment.class, args));
        mTabHost.addTab(new FragmentElement("Snapshots", R.drawable.ic_menu_camera, SnapshotFragment.class, args));
        return mTabHost;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.machine_actions, menu);
        if(mTabHost.getCurrentFragment()!=null)
            (mTabHost.getCurrentFragment()).onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return (mTabHost.getCurrentFragment()).onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }
}
