package com.kedzie.vbox.machine;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.PreferencesActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.tabs.TabSupport;
import com.kedzie.vbox.tabs.TabSupportFragment;
import com.kedzie.vbox.tabs.TabSupportViewPager;

public class MachineFragmentActivity extends SherlockFragmentActivity {
	private static final int REQUEST_CODE_PREFERENCES = 6;
	
	private TabSupport _tabSupport;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO make better
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			NavUtils.navigateUpTo(this, new Intent(this, MachineListFragmentActivity.class).putExtras(getIntent()));
            return;
        }
		IMachine m = BundleBuilder.getProxy(getIntent(), IMachine.BUNDLE, IMachine.class);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);		
		getSupportActionBar().setIcon(((VBoxApplication)getApplication()).getDrawable("ic_list_os_"+m.getOSTypeId().toLowerCase()));
		getSupportActionBar().setTitle(m.getName());
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		if(savedInstanceState==null) {
			ViewPager pager = new ViewPager(this);
			pager.setId(99);
			setContentView(pager);
			_tabSupport = VBoxApplication.VIEW_PAGER_TABS ? new TabSupportViewPager(this, pager) : new TabSupportFragment(this, android.R.id.content);
			_tabSupport.addTab("Actions", ActionsFragment.class, getIntent().putExtra("dualPane", false).getExtras());
			_tabSupport.addTab("Info", InfoFragment.class,getIntent().getExtras());
			_tabSupport.addTab("Log", LogFragment.class, getIntent().getExtras());
			_tabSupport.addTab("Snapshots", SnapshotFragment.class, getIntent().getExtras());
		} else {
			getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
		}
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getSupportActionBar().getSelectedNavigationIndex());
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.machine_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			break;
		case R.id.option_menu_preferences:
			startActivityForResult(new Intent(this, PreferencesActivity.class), REQUEST_CODE_PREFERENCES);
			return true;
		}
		return false;
	}
}
