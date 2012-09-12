package com.kedzie.vbox.machine;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.EventNotificationReceiver;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.machine.MachineListFragment.SelectMachineListener;
import com.kedzie.vbox.server.ServerListActivity;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.tabs.TabSupport;
import com.kedzie.vbox.tabs.TabSupportFragment;
import com.kedzie.vbox.tabs.TabSupportViewPager;
import com.kedzie.vbox.task.DialogTask;

public class MachineListFragmentActivity extends SherlockFragmentActivity implements SelectMachineListener {
	public final static String INTENT_VERSION = "version";
	
	/** Is the dual Fragment Layout active? */
	private boolean _dualPane;
	/** VirtualBox API */
	private VBoxSvc _vmgr;
	/** {@link ActionBar} tabs */
	private TabSupport _tabSupport;
	

	private class LogoffTask extends DialogTask<Void, Void>	{
		public LogoffTask(VBoxSvc vmgr) { 
			super( "LogoffTask", MachineListFragmentActivity.this, vmgr, "Logging Off");
		}
		@Override
		protected Void work(Void... params) throws Exception {
			_vmgr.logoff();
			return null;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_vmgr = (VBoxSvc)getIntent().getParcelableExtra(VBoxSvc.BUNDLE);
		
		setContentView(R.layout.machine_list);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("VirtualBox v." + getIntent().getStringExtra(INTENT_VERSION));
		
		View detailsFrame = findViewById(R.id.details);
		_dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
		if(_dualPane) {
			_tabSupport = VBoxApplication.VIEW_PAGER_TABS ? new TabSupportViewPager(this, (ViewPager)detailsFrame) : new TabSupportFragment(this, R.id.details);
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			getSupportActionBar().setDisplayShowTitleEnabled(false);
			 if (savedInstanceState != null) 
		            getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
		}
		LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
				new EventNotificationReceiver(_vmgr) , new IntentFilter(VBoxEventType.ON_MACHINE_STATE_CHANGED.name()));
		startService(new Intent(this, EventIntentService.class).putExtras(getIntent()));
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getSupportActionBar().getSelectedNavigationIndex());
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			logoff();
			NavUtils.navigateUpTo(this, new Intent(this, ServerListActivity.class));
			break;
		}
		return false;
	}

	@Override 
	public void onBackPressed() {
		logoff();
	}
	
	public void logoff() {
		stopService(new Intent(this, EventIntentService.class));
		if(_vmgr.getVBox()!=null)  
			new LogoffTask(_vmgr). execute();
	}
}
