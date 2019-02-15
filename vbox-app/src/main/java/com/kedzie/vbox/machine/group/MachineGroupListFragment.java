package com.kedzie.vbox.machine.group;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.event.EventNotificationReceiver;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.BaseTask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * Machine list with option menu and event handler
 * @apiviz.stereotype fragment
 */
public class MachineGroupListFragment extends MachineGroupListBaseFragment {

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
	private class HandleEventTask extends BaseTask<Bundle, IMachine> {
		
		public HandleEventTask(VBoxSvc vmgr) { 
			super((AppCompatActivity)getActivity(), vmgr);
		}

		@Override
		protected IMachine work(Bundle... params) throws Exception {
			IMachine m = BundleBuilder.getProxy(params[0], IMachine.BUNDLE, IMachine.class);
			Utils.cacheProperties(m);
			return m;
		}

		@Override
		protected void onSuccess(IMachine result)	{
		    _listView.update(result);
		}
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.machine_list_actions, menu);
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item) {
		switch(item.getItemId()) {
		case R.id.option_menu_refresh:
			new LoadGroupsTask(_vmgr).execute();
			return false;
		}
		return false;
	}
}
