package com.kedzie.vbox.machine.group;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.event.EventNotificationReceiver;
import com.kedzie.vbox.host.HostSettingsActivity;
import com.kedzie.vbox.machine.MachineView;
import com.kedzie.vbox.machine.PreferencesActivity;
import com.kedzie.vbox.metrics.MetricActivity;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;
import com.kedzie.vbox.task.ConfigureMetricsTask;

/**
 * Show Virtual Machines/Groups in a hierarchical layout
 * @apiviz.stereotype fragment
 */
public class MachineGroupListFragment extends MachineGroupListBaseFragment {
	private static final int REQUEST_CODE_PREFERENCES = 6;
	
	private LocalBroadcastManager lbm;
	private EventNotificationReceiver _notificationReceiver = new EventNotificationReceiver();
	private BroadcastReceiver _receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(VBoxEventType.ON_MACHINE_STATE_CHANGED.name()))
				new HandleEventTask(_vmgr).execute(intent.getExtras());
		}
	};
	
	/**
	 * Handle MachineStateChanged event
	 */
	private class HandleEventTask extends ActionBarTask<Bundle, IMachine> {
		
		public HandleEventTask(VBoxSvc vmgr) { 
			super( "HandleEventTask", getSherlockActivity(), vmgr);
		}

		@Override
		protected IMachine work(Bundle... params) throws Exception {
			IMachine m = BundleBuilder.getProxy(params[0], IMachine.BUNDLE, IMachine.class);
			MachineView.cacheProperties(m);
			return m;
		}

		@Override
		protected void onResult(IMachine result)	{
		    _listView.update(result);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return _listView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		lbm = LocalBroadcastManager.getInstance(getActivity().getApplicationContext());
		lbm.registerReceiver(_notificationReceiver, new IntentFilter(VBoxEventType.ON_MACHINE_STATE_CHANGED.name()));
	}
	
	@Override
	public void onStart() {
		super.onStart();
		lbm.registerReceiver(_receiver, new IntentFilter(VBoxEventType.ON_MACHINE_STATE_CHANGED.name()));
	}

	@Override
	public void onStop() {
		super.onStop();
		lbm.unregisterReceiver(_receiver);
	}
	
	@Override
	public void onDestroy() {
		lbm.unregisterReceiver(_notificationReceiver);
		super.onDestroy();
	}

	
	@Override
	public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu, com.actionbarsherlock.view.MenuInflater inflater) {
		inflater.inflate(R.menu.machine_list_actions, menu);
	}

	@Override
	public boolean onOptionsItemSelected( com.actionbarsherlock.view.MenuItem item) {
		switch(item.getItemId()) {
		case R.id.option_menu_refresh:
			new LoadGroupsTask(_vmgr).execute();
			return false;
		case R.id.machine_list_option_menu_metrics:
			startActivity(new Intent(getActivity(), MetricActivity.class).putExtra(VBoxSvc.BUNDLE, (Parcelable)_vmgr)
					.putExtra(MetricActivity.INTENT_TITLE, getResources().getString(R.string.host_metrics))
					.putExtra(MetricActivity.INTENT_OBJECT, _vmgr.getVBox().getHost().getIdRef() )
					.putExtra(MetricActivity.INTENT_RAM_AVAILABLE, _vmgr.getVBox().getHost().getMemorySize())
					.putExtra(MetricActivity.INTENT_CPU_METRICS , new String[] { "CPU/Load/User", "CPU/Load/Kernel" } )
					.putExtra(MetricActivity.INTENT_RAM_METRICS , new String[] {  "RAM/Usage/Used" }));
			return true;
		case R.id.option_menu_preferences:
			startActivityForResult(new Intent(getActivity(), PreferencesActivity.class),REQUEST_CODE_PREFERENCES);
			return true;
		case R.id.machine_list_option_menu_host_settings:
			if(VBoxApplication.getInstance().isPremiumVersion())
				startActivity(new Intent(getActivity(), HostSettingsActivity.class).putExtra(IHost.BUNDLE, _vmgr.getVBox().getHost()));
			else
				VBoxApplication.getInstance().showPremiumOffer(getActivity());
			return true;
		}
		return false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_CODE_PREFERENCES) {
			new ConfigureMetricsTask(getActivity(), _vmgr).execute(
					Utils.getIntPreference(getActivity(), PreferencesActivity.PERIOD),	
					Utils.getIntPreference(getActivity(), PreferencesActivity.COUNT) );
		}
	}
}
