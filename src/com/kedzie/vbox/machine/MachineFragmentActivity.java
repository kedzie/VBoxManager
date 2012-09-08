package com.kedzie.vbox.machine;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.tabs.TabActivity;
import com.kedzie.vbox.tabs.TabSupport;
import com.kedzie.vbox.tabs.TabSupportFragment;
import com.kedzie.vbox.tabs.TabSupportViewPager;

public class MachineFragmentActivity extends TabActivity {

	private TabSupport _tabSupport;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO make better
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			NavUtils.navigateUpTo(this, new Intent(this, MachineListFragmentActivity.class).putExtras(getIntent()));
            return;
        }
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		IMachine m = BundleBuilder.getProxy(getIntent(), IMachine.BUNDLE, IMachine.class);
		getSupportActionBar().setIcon(((VBoxApplication)getApplication()).getDrawable("ic_list_os_"+m.getOSTypeId().toLowerCase()));
		getSupportActionBar().setTitle(m.getName());
		
		if(savedInstanceState==null) {
			ViewPager pager = new ViewPager(this);
			pager.setId(99);
			setContentView(pager);
			_tabSupport = VBoxApplication.VIEW_PAGER_TABS ? new TabSupportViewPager(this, pager) : new TabSupportFragment(this, android.R.id.content);
			_tabSupport.addTab("Actions", ActionsFragment.class, getIntent().putExtra("dualPane", false).getExtras());
			_tabSupport.addTab("Info", InfoFragment.class,getIntent().getExtras());
			_tabSupport.addTab("Log", LogFragment.class, getIntent().getExtras());
			_tabSupport.addTab("Snapshots", SnapshotFragment.class, getIntent().getExtras());
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			break;
		}
		return false;
	}
}
