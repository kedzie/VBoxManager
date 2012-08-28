package com.kedzie.vbox.machine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.EventNotificationService;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.machine.MachineListFragment.SelectMachineListener;
import com.kedzie.vbox.server.ServerListActivity;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.tabs.TabActivity;
import com.kedzie.vbox.tabs.TabSupport;
import com.kedzie.vbox.tabs.ViewPagerTabSupport;

public class MachineListFragmentActivity extends TabActivity implements SelectMachineListener {

	/** Is the dual Fragment Layout active? */
	private boolean _dualPane;
	/** VirtualBox API */
	private VBoxSvc _vmgr;
	/** {@link ActionBar} tabs */
	private TabSupport _tabSupport;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		_vmgr = (VBoxSvc)getIntent().getParcelableExtra(VBoxSvc.BUNDLE);
		setContentView(R.layout.machine_list);
		View detailsFrame = findViewById(R.id.details);
		_dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
		if(_dualPane) {
			_tabSupport = new ViewPagerTabSupport(this, (ViewPager)detailsFrame);
//			_tabSupport = new FragmentTabSupport(this, R.id.details);
		}
		startService(new Intent(this, EventIntentService.class).putExtra(VBoxSvc.BUNDLE, _vmgr));
		startService(new Intent(this, EventNotificationService.class).putExtra(VBoxSvc.BUNDLE, _vmgr));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpTo(this, new Intent(this, ServerListActivity.class));
			break;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		stopService(new Intent(this, EventNotificationService.class));
		stopService(new Intent(this, EventIntentService.class));
		super.onDestroy();
	}

	@Override
	public void onMachineSelected(IMachine machine) {
		if (_dualPane) {
			_tabSupport.removeAllTabs();
			Bundle b = new BundleBuilder().putParcelable(VBoxSvc.BUNDLE, _vmgr)
															.putProxy(IMachine.BUNDLE, machine)
															.putBoolean("dualPane", true)
															.create();
			_tabSupport.addTab(getString(R.string.tab_actions), ActionsFragment.class, b);
			_tabSupport.addTab(getString(R.string.tab_info), InfoFragment.class, b);
			_tabSupport.addTab(getString(R.string.tab_log), LogFragment.class, b);
			_tabSupport.addTab(getString(R.string.tab_snapshots), SnapshotFragment.class, b);
		} else {
			Intent intent = new Intent(this, MachineFragmentActivity.class).putExtra(VBoxSvc.BUNDLE, _vmgr);
			BundleBuilder.addProxy(intent, IMachine.BUNDLE, machine );
			startActivity(intent);
		}
	}
}
