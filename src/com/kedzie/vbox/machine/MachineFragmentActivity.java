package com.kedzie.vbox.machine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.tabs.TabActivity;
import com.kedzie.vbox.tabs.TabSupport;
import com.kedzie.vbox.tabs.ViewPagerTabSupport;

public class MachineFragmentActivity extends TabActivity {

	private TabSupport _tabSupport;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		if(savedInstanceState==null) {
			ViewPager pager = new ViewPager(this);
			pager.setId(99);
			setContentView(pager);
			_tabSupport = new ViewPagerTabSupport(this, pager);
//			_tabSupport = new FragmentTabSupport(this);
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
			NavUtils.navigateUpTo(this, new Intent(this, MachineListFragmentActivity.class));
			break;
		}
		return true;
	}
}
