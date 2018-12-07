package com.kedzie.vbox.machine;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.kedzie.vbox.R;
import com.kedzie.vbox.SettingsActivity;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.FragmentActivity;
import com.kedzie.vbox.app.FragmentElement;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.event.EventIntentService;
import com.kedzie.vbox.host.HostInfoFragment;
import com.kedzie.vbox.host.HostNetworkListFragment;
import com.kedzie.vbox.machine.group.GroupInfoFragment;
import com.kedzie.vbox.machine.group.TreeNode;
import com.kedzie.vbox.machine.group.VMGroup;
import com.kedzie.vbox.machine.group.VMGroupListView.OnTreeNodeSelectListener;
import com.kedzie.vbox.server.LoginSupport;
import com.kedzie.vbox.server.Server;
import com.kedzie.vbox.server.ServerSQlite;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ConfigureMetricsTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 *
 * @author Marek Kędzierski
 * @apiviz.stereotype activity
 */
public class MachineListActivity extends BaseActivity implements OnTreeNodeSelectListener, HasSupportFragmentInjector {
	private static final Logger log = LoggerFactory.getLogger(MachineListActivity.class);

	private static final int REQUEST_CODE_PREFERENCES = 6;

	/** Is the dual Fragment Layout active? */
	private boolean _dualPane;
	/** VirtualBox API */
	private VBoxSvc _vmgr;

	@BindView(R.id.drawer_layout)
	 DrawerLayout mDrawerLayout;
	@BindView(R.id.nav)
	 NavigationView navView;
	@BindView(R.id.toolbar)
	 Toolbar toolbar;
	private ActionBarDrawerToggle mDrawerToggle;

	@Inject
	DispatchingAndroidInjector<Fragment> dispatchingFragmentInjector;

	@Override
	public AndroidInjector<Fragment> supportFragmentInjector() {
		return dispatchingFragmentInjector;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		_vmgr = BundleBuilder.getVBoxSvc(getIntent());

		startService(new Intent(this, EventIntentService.class).putExtras(getIntent()));

		initUI();
	}

	private void initUI() {
		setContentView(R.layout.machine_list);
		_dualPane = findViewById(R.id.details)!=null;

		ButterKnife.bind(this);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE |ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);

		navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(android.view.MenuItem menuItem) {
				switch(menuItem.getItemId()) {
					case R.id.navigation_home:
						break;
					case R.id.navigation_host_network:
						show(new FragmentElement("Host", HostNetworkListFragment.class,
									new BundleBuilder().putVBoxSvc(_vmgr).create()));
						break;
					case R.id.navigation_prefs:
						Utils.startActivityForResult(MachineListActivity.this, new Intent(MachineListActivity.this, SettingsActivity.class), REQUEST_CODE_PREFERENCES);
						break;
					case R.id.navigation_logoff:
						logoff();
						break;
				}
				return false;
			}
		});
		initNavHeader();

		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				mDrawerLayout,         /* DrawerLayout object */
				toolbar, 				/* action bar */
				R.string.drawer_open,  /* "open drawer" description for accessibility */
				R.string.drawer_closed  		/* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		mDrawerToggle.setDrawerIndicatorEnabled(true);
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
	}

	private void initNavHeader() {
		View header = LayoutInflater.from(this).inflate(R.layout.drawer_header, navView, false);
		Spinner serverSpinner = (Spinner) header.findViewById(R.id.server_spinner);
		ServerSQlite db = new ServerSQlite(this);
		List<Server> servers = db.query();
		ArrayAdapter<Server> serverAdapter = new ArrayAdapter<Server>(this, R.layout.simple_selectable_list_item, android.R.id.text1, servers) {
			@Override
			public boolean hasStableIds() {
				return true;
			}

			@Override
			public long getItemId(int position) {
				return getItem(position).getId();
			}
		};
		db.close();
		serverSpinner.setAdapter(serverAdapter);
		serverSpinner.setSelection(servers.indexOf(_vmgr.getServer()));
		serverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				ArrayAdapter<Server> serverAdapter = (ArrayAdapter<Server>) adapterView.getAdapter();
				Server server = serverAdapter.getItem(position);
				log.info("Server selected from navigation drawer {}", server);
				if(!server.equals(_vmgr.getServer())) {
					setResult(5, new Intent().putExtra("server", (Parcelable) server));
					logoff();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			}
		});
		navView.addHeaderView(header);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_CODE_PREFERENCES) {
			new ConfigureMetricsTask(this, _vmgr).execute(
					Utils.getIntPreference(this, SettingsActivity.PREF_PERIOD),
					Utils.getIntPreference(this, SettingsActivity.PREF_COUNT) );
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
		if(Utils.getScreenSize(newConfig)==Configuration.SCREENLAYOUT_SIZE_LARGE) {
			log.info("Handling orientation change");
//            FragmentManager mgr = getSupportFragmentManager();
//            FragmentTransaction tx = mgr.beginTransaction();
//            for(Fragment fragment : mgr.getFragments()) {
//                tx.detach(fragment);
//            }
//            tx.commit();
			initUI();
		}
	}

	@Override
	public void onTreeNodeSelect(TreeNode node) {
		if(node instanceof IMachine)
			onMachineSelected((IMachine)node);
		else if (node instanceof VMGroup)
			onGroupSelected((VMGroup)node);
		else if (node instanceof IHost)
			onHostSelected((IHost)node);
	}

	private void onMachineSelected(IMachine machine) {
		show(new FragmentElement(machine.getName(), getApp().getOSDrawable(machine.getOSTypeId()), MachineFragment.class,
				new BundleBuilder().putVBoxSvc(_vmgr)
						.putProxy(IMachine.BUNDLE, machine)
						.putBoolean("dualPane", _dualPane).create()));
	}

	private void onGroupSelected(VMGroup group) {
		show(new FragmentElement(group.getName(), GroupInfoFragment.class,
				new BundleBuilder().putVBoxSvc(_vmgr)
						.putParcelable(VMGroup.BUNDLE, group)
						.putBoolean("dualPane", _dualPane).create()));
	}

	private void onHostSelected(IHost host) {
		show(new FragmentElement("Host", HostInfoFragment.class,
				new BundleBuilder().putVBoxSvc(_vmgr)
						.putParcelable(IHost.BUNDLE, host)
						.putBoolean("dualPane", _dualPane).create()));
	}

	private void show(FragmentElement details) {
		if(_dualPane) {
			Utils.setCustomAnimations(getSupportFragmentManager().beginTransaction()).replace(R.id.details, details.instantiate(this)).commit();
		} else {
			Utils.startActivity(this, new Intent(this, FragmentActivity.class).putExtra(FragmentElement.BUNDLE, details));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(mDrawerToggle.onOptionsItemSelected(item))
			return true;
		return false;
	}

	@Override
	public void onBackPressed() {
		logoff();
	}

	@Override
	public void finish() {
		super.finish();
		Utils.overrideBackTransition(this);
	}

	public void logoff() {
		stopService(new Intent(this, EventIntentService.class));
		if(_vmgr.getVBox()!=null)
			new LoginSupport.LogoffTask(this, _vmgr) {
				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					finish();
				}
			}. execute();
	}
}
