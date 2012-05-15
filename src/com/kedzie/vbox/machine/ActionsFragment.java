package com.kedzie.vbox.machine;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.CachedArrayAdapter;
import com.kedzie.vbox.PreferencesActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.Utils;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.VMAction;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.ISessionStateChangedEvent;
import com.kedzie.vbox.api.jaxb.SessionState;
import com.kedzie.vbox.metrics.MetricActivity;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.BaseTask;
import com.kedzie.vbox.task.ConfigureMetricsTask;
import com.kedzie.vbox.task.LaunchVMProcessTask;
import com.kedzie.vbox.task.MachineTask;

public class ActionsFragment extends SherlockFragment implements OnItemClickListener {
	protected static final String TAG = ActionsFragment.class.getSimpleName();
	private static final int REQUEST_CODE_PREFERENCES = 6;
	
	/** VirtualBox API */
	private VBoxSvc _vmgr;
	
	/** The Virtual Machine */
	private IMachine _machine;
	
	private MachineView _headerView;
	private ListView _listView;
	
	/** Local Broadcast Manager */
	private LocalBroadcastManager lbm;
	
	private boolean _dualPane;
	
	/** Event-handling local broadcasts */
	private BroadcastReceiver _receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(EventService.com_virtualbox_EVENT)) {
				Log.i(TAG, "Recieved Broadcast");
				new UpdateMachineViewTask(_vmgr).execute(_machine);
			}
		}
	};
	
	public static ActionsFragment getInstance(Bundle arguments) {
		ActionsFragment f = new ActionsFragment();
		f.setArguments(arguments);
		return f;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		 lbm = LocalBroadcastManager.getInstance(getActivity().getApplicationContext());
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _vmgr = getArguments().getParcelable(VBoxSvc.BUNDLE);
        _machine = BundleBuilder.getProxy(savedInstanceState!=null ? savedInstanceState : getArguments(), IMachine.BUNDLE, IMachine.class);
        _dualPane = getArguments().getBoolean("dualPane");
        setHasOptionsMenu(!_dualPane);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_headerView = new MachineView(getApp(), getActivity());
		_listView = new ListView(getActivity());
		_listView.addHeaderView(_headerView);
		_listView.setOnItemClickListener(this);
		return _listView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		lbm.registerReceiver(_receiver, new IntentFilter(EventService.com_virtualbox_EVENT));
		new UpdateMachineViewTask(_vmgr).execute(_machine);
	}

	@Override
	public void onStop() {
		new Thread() {
			@Override
			public void run() {
				try {
					if(_vmgr.getVBox().getSessionObject().getState().equals(SessionState.LOCKED)) 
						_vmgr.getVBox().getSessionObject().unlockMachine();
				} catch (IOException e) {
					Log.e(TAG, "Exception unlocking machine", e);
				}
			}
		}.start();
		super.onStop();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		BundleBuilder.putProxy(outState, IMachine.BUNDLE, _machine);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.machine_options_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.machine_option_menu_refresh:
			new UpdateMachineViewTask(_vmgr).execute(_machine);
			return true;
		case R.id.machine_option_menu_preferences:
			startActivityForResult(new Intent(getActivity(), PreferencesActivity.class), REQUEST_CODE_PREFERENCES);
			return true;
		case R.id.machine_option_menu_metrics:
			startActivity(new Intent(getActivity(), MetricActivity.class).putExtra(VBoxSvc.BUNDLE, _vmgr)
				.putExtra(MetricActivity.INTENT_IMPLEMENTATION, Utils.getStringPreference(getActivity(), PreferencesActivity.METRIC_IMPLEMENTATION))
				.putExtra(MetricActivity.INTENT_TITLE, _machine.getName() + " Metrics")
				.putExtra(MetricActivity.INTENT_OBJECT, _machine.getIdRef() )
				.putExtra(MetricActivity.INTENT_RAM_AVAILABLE, _machine.getMemorySize() )
				.putExtra(MetricActivity.INTENT_CPU_METRICS , new String[] { "CPU/Load/User",  "CPU/Load/Kernel"  } )
				.putExtra(MetricActivity.INTENT_RAM_METRICS , new String[] {  "RAM/Usage/Used" } ));
			return true;
		default:
			return true;
		}
	}
	
	@Override 
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		VMAction action = (VMAction)_listView.getAdapter().getItem(position);
		if(action.equals(VMAction.START))	
			new LaunchVMProcessTask(getActivity(), _vmgr).execute(_machine);
		else if(action.equals(VMAction.POWER_OFF))	
			new MachineTask<IMachine>("PoweroffTask", getActivity(), _vmgr, "Powering Off", false, _machine) {	
			  protected IProgress workWithProgress(IMachine m,  IConsole console, IMachine...i) throws Exception { 	
				  return console.powerDown();
			  }
		  }.execute(_machine);
		else if(action.equals(VMAction.RESET))
			new MachineTask<IMachine>("ResetTask", getActivity(), _vmgr, "Resetting", true, _machine) {	
			  protected void work(IMachine m,  IConsole console, IMachine...i) throws Exception { 	
				  console.reset(); 
			  }
			  }.execute(_machine);
		else if(action.equals(VMAction.PAUSE)) 	
			new MachineTask<IMachine>("PauseTask", getActivity(), _vmgr, "Pausing", true, _machine) {	
			  protected void work(IMachine m,  IConsole console, IMachine...i) throws Exception {  
				  console.pause();	
			  }
		  }.execute(_machine);
		else if(action.equals(VMAction.RESUME)) 
			new MachineTask<IMachine>("ResumeTask", getActivity(), _vmgr, "Resuming", true, _machine) {	
			  protected void work(IMachine m,  IConsole console, IMachine...i) throws Exception { 	
				  console.resume(); 
			  }
		  }.execute(_machine);
		else if(action.equals(VMAction.POWER_BUTTON)) 	
			new MachineTask<IMachine>("ACPITask", getActivity(), _vmgr, "ACPI Power Down", true, _machine) {
			  protected void work(IMachine m,  IConsole console,IMachine...i) throws Exception {	
				  console.powerButton(); 	
			  }
		  }.execute(_machine);
		else if(action.equals(VMAction.SAVE_STATE)) 	
			new MachineTask<IMachine>("SaveStateTask", getActivity(), _vmgr, "Saving State", false, _machine) { 
				protected IProgress workWithProgress(IMachine m, IConsole console, IMachine...i) throws Exception { 	
					return console.saveState(); 
				}
			}.execute(_machine);
		else if(action.equals(VMAction.DISCARD_STATE)) 	
			new MachineTask<IMachine>("DiscardStateTask", getActivity(), _vmgr, "Discarding State", true, _machine) { 
				protected void work(IMachine m, IConsole console, IMachine...i) throws Exception { 	
					console.discardSavedState(true); 
				}
			}.execute(_machine);
		else if(action.equals(VMAction.TAKE_SNAPSHOT)) 	{
			Bundle b = new BundleBuilder()
								.putParcelable(VBoxSvc.BUNDLE, _vmgr)
								.putProxy(IMachine.BUNDLE, _machine)
								.create();
			TakeSnapshotFragment.getInstance(b)
				.show(getSherlockActivity().getSupportFragmentManager(), "dialog");
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CODE_PREFERENCES) 
			new ConfigureMetricsTask(getActivity(), _vmgr).execute();
	}
	
	public VBoxApplication getApp() { 
		return (VBoxApplication)getActivity().getApplication(); 
	}
	
	/**
	 * Load Machine properties from web server
	 */
	class UpdateMachineViewTask extends BaseTask<IMachine, IMachine> {
		
		public UpdateMachineViewTask(VBoxSvc vmgr) {
			super(UpdateMachineViewTask.class.getSimpleName(), getSherlockActivity(), vmgr, "Loading Machine");
		}
		
		@Override 
		protected IMachine work(IMachine... m) throws Exception {
			_machine.clearCache();
			m[0].getCurrentStateModified(); m[0].getOSTypeId(); m[0].getName(); m[0].getState();
			if(m[0].getCurrentSnapshot()!=null)   
				m[0].getCurrentSnapshot().getName(); 
			m[0].getMemorySize();
			return m[0];
		}

		@Override
		protected void onPostExecute(IMachine result) {
			super.onPostExecute(result);
			_headerView.update(result);
			_listView.setAdapter(new MachineActionAdapter(VMAction.getVMActions(_machine.getState())));
		}
	}
	
	/**
	 * Handle MachineStateChanged event
	 */
	class HandleEventTask extends BaseTask<Bundle, ISessionStateChangedEvent> {
		
		public HandleEventTask(VBoxSvc vmgr) {  
			super( "HandleEventTask", getSherlockActivity(), vmgr, "Handling Event");
		}

		@Override
		protected ISessionStateChangedEvent work(Bundle... params) throws Exception {
			IEvent event = BundleBuilder.getProxy(params[0], EventThread.BUNDLE_EVENT, IEvent.class);
			if(event instanceof ISessionStateChangedEvent)
				return (ISessionStateChangedEvent) event;
			return null;
		}

		@Override
		protected void onPostExecute(ISessionStateChangedEvent result)	{
			super.onPostExecute(result);
			if(result!=null)	{
				Utils.toast(getActivity(), "Session changed State: "+result.getState());
			}
		}
	}
	
	/**
	 * List Adapter for Virtual Machine Actions 
	 */
	class MachineActionAdapter extends CachedArrayAdapter<VMAction> {
		private final LayoutInflater _layoutInflater;
		
		public MachineActionAdapter(VMAction []actions) {
			super(getActivity(), actions);
			_layoutInflater = LayoutInflater.from(getActivity());
		}

		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) 
				view = _layoutInflater.inflate(R.layout.machine_action_item, parent, false);
			((TextView)findViewById(view, R.id.action_item_text)).setText(getItem(position).toString());
			((ImageView)findViewById(view, R.id.action_item_icon)).setImageResource( getApp().getDrawable(getItem(position)));
			return view;
		}
	}
}
