package com.kedzie.vbox.machine;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.R;
import com.kedzie.vbox.SettingsActivity;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.FragmentElement;
import com.kedzie.vbox.app.PagerTabHost;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ConfigureMetricsTask;

public class MachineFragment extends SherlockFragment {
    private static final int REQUEST_CODE_PREFERENCES = 6;

    private PagerTabHost mTabHost;
    private boolean mDualPane;
    private VBoxSvc _vmgr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDualPane = getArguments().getBoolean("dualPane");
        _vmgr = BundleBuilder.getVBoxSvc(getArguments());
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
		if(!mDualPane)
        	inflater.inflate(R.menu.machine_actions, menu);
		((SherlockFragment)mTabHost.getCurrentFragment()).onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.option_menu_preferences:
                Utils.startActivityForResult(getActivity(), new Intent(getActivity(), SettingsActivity.class), REQUEST_CODE_PREFERENCES);
                return true;
        }
		return ((SherlockFragment)mTabHost.getCurrentFragment()).onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_PREFERENCES) {
            new ConfigureMetricsTask(getSherlockActivity(), _vmgr).execute(
                    Utils.getIntPreference(getActivity(), SettingsActivity.PREF_PERIOD),
                    Utils.getIntPreference(getActivity(), SettingsActivity.PREF_COUNT) );
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }
}
