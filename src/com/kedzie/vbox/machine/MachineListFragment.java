package com.kedzie.vbox.machine;

import java.util.Arrays;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.ActionMode;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VMAction;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.event.EventNotificationReceiver;
import com.kedzie.vbox.metrics.MetricActivity;
import com.kedzie.vbox.metrics.MetricPreferencesActivity;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;
import com.kedzie.vbox.task.ConfigureMetricsTask;
import com.kedzie.vbox.task.LaunchVMProcessTask;
import com.kedzie.vbox.task.MachineTask;

/**
 * 
 * @author Marek Kędzierski
 * @apiviz.stereotype fragment
 */
public class MachineListFragment extends MachineListBaseFragment {
	private static final int REQUEST_CODE_PREFERENCES = 6;
	
	/** Whether the  details {@link Fragment} is displayed */
	private boolean _dualPane;
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
			Utils.toastShort(getActivity(), "%s changed State: [%s]", result.getName(), result.getState());
			getAdapter().setNotifyOnChange(false);
			int position = getAdapter().getPosition(result);
			getAdapter().remove(result);
			getAdapter().insert(result, position);
			getAdapter().notifyDataSetChanged();
		}
	}
	
	/**
	 * Action Mode
	 * @author Marek Kędzierski
	 */
	class ActionCallback implements ActionMode.Callback {
        private IMachine _machine;
        
        public ActionCallback(IMachine machine) {
            _machine = machine;
        }
        
        @Override
        public boolean onCreateActionMode(ActionMode mode, com.actionbarsherlock.view.Menu menu) {
            _listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            _listView.setItemChecked(getAdapter().getPosition(_machine), true);
            mode.getMenuInflater().inflate(R.menu.machine_list_context, menu);
            return true;
        }
        
        @Override
        public boolean onPrepareActionMode(ActionMode mode, com.actionbarsherlock.view.Menu menu) {
            List<VMAction> actions = Arrays.asList(VMAction.getVMActions(_machine.getState()));
            if(!actions.contains(VMAction.START))
                menu.removeItem(R.id.machines_context_menu_start);
            if(!actions.contains(VMAction.POWER_OFF))
                menu.removeItem(R.id.machines_context_menu_poweroff);
            if(!actions.contains(VMAction.POWER_BUTTON))    
                menu.removeItem(R.id.machines_context_menu_acpi);
            if(!actions.contains(VMAction.RESET))
                menu.removeItem(R.id.machines_context_menu_reset);
            if(!actions.contains(VMAction.PAUSE))
                menu.removeItem(R.id.machines_context_menu_pause);
            if(!actions.contains(VMAction.RESUME))
                menu.removeItem(R.id.machines_context_menu_resume);
            return true;
        }
        
        @Override
        public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
            switch (item.getItemId()) {
              case R.id.machines_context_menu_start:  
                  new LaunchVMProcessTask(getActivity(), _vmgr).execute(_machine);    
                  mode.finish();
                  return true;
              case R.id.machines_context_menu_poweroff:   
                  new MachineTask<IMachine, Void>("PoweroffTask", getActivity(), _vmgr, "Powering Off", false, _machine) {  
                      protected IProgress workWithProgress(IMachine m,  IConsole console, IMachine...i) throws Exception {  
                          return console.powerDown();
                      }
                  }.execute(_machine);
                  mode.finish();
                  return true;
              case R.id.machines_context_menu_reset:     
                  new MachineTask<IMachine, Void>("ResetTask", getActivity(), _vmgr, "Resetting", true, _machine) { 
                      protected Void work(IMachine m,  IConsole console, IMachine...i) throws Exception {   
                          console.reset(); 
                          return null;
                      }
                      }.execute(_machine);
                      mode.finish();
                      return true;
              case R.id.machines_context_menu_resume:     
                  new MachineTask<IMachine, Void>("ResumeTask", getActivity(), _vmgr, "Resuming", true, _machine) { 
                      protected Void work(IMachine m,  IConsole console, IMachine...i) throws Exception {   
                          console.resume(); 
                          return null;
                      }
                  }.execute(_machine);
                  mode.finish();
                  return true;
              case R.id.machines_context_menu_pause:      
                  new MachineTask<IMachine, Void>("PauseTask", getActivity(), _vmgr, "Pausing", true, _machine) {   
                      protected Void work(IMachine m,  IConsole console, IMachine...i) throws Exception {  
                          console.pause();  
                          return null;
                      }
                  }.execute(_machine);
                  mode.finish();
                  return true;
              case R.id.machines_context_menu_acpi:   
                  new MachineTask<IMachine, Void>("ACPITask", getActivity(), _vmgr, "ACPI Power Down", true, _machine) {
                      protected Void work(IMachine m,  IConsole console,IMachine...i) throws Exception {    
                          console.powerButton();    
                          return null;
                      }
                  }.execute(_machine);
                  mode.finish();
                  return true;
              }
              return false;
        }
        
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            _listView.setChoiceMode(_dualPane ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
        }
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		_listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				getSherlockActivity().startActionMode(new ActionCallback(getAdapter().getItem(position)));
				return true;
			}
		});
       	return _listView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		View detailsFrame = getActivity().findViewById(R.id.details);
		_dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
		_listView.setChoiceMode(_dualPane ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
		
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
			new LoadMachinesTask(_vmgr).execute();
			return true;
		case R.id.machine_list_option_menu_metrics:
			startActivity(new Intent(getActivity(), MetricActivity.class).putExtra(VBoxSvc.BUNDLE, _vmgr)
					.putExtra(MetricActivity.INTENT_TITLE, R.string.host_metrics)
					.putExtra(MetricActivity.INTENT_OBJECT, _vmgr.getVBox().getHost().getIdRef() )
					.putExtra(MetricActivity.INTENT_RAM_AVAILABLE, _vmgr.getVBox().getHost().getMemorySize())
					.putExtra(MetricActivity.INTENT_CPU_METRICS , new String[] { "CPU/Load/User", "CPU/Load/Kernel" } )
					.putExtra(MetricActivity.INTENT_RAM_METRICS , new String[] {  "RAM/Usage/Used" }));
			return true;
		case R.id.option_menu_preferences:
			startActivityForResult(new Intent(getActivity(), PreferencesActivity.class),REQUEST_CODE_PREFERENCES);
			return true;
		}
		return false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_CODE_PREFERENCES) {
			new ConfigureMetricsTask(getActivity(), _vmgr).execute(
					Utils.getIntPreference(getActivity(), MetricPreferencesActivity.PERIOD),	
					Utils.getIntPreference(getActivity(), MetricPreferencesActivity.COUNT) );
		}
	}
}
