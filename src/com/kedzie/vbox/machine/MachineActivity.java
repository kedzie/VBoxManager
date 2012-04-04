package com.kedzie.vbox.machine;

import java.io.IOException;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.PreferencesActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMachineStateChangedEvent;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.jaxb.MachineState;
import com.kedzie.vbox.api.jaxb.SessionState;
import com.kedzie.vbox.metrics.MetricActivity;
import com.kedzie.vbox.metrics.MetricView;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.BaseTask;
import com.kedzie.vbox.task.LaunchVMProcessTask;
import com.kedzie.vbox.task.MachineTask;

public class MachineActivity extends Activity  implements AdapterView.OnItemClickListener {
	protected static final String TAG = MachineActivity.class.getSimpleName();
	private static final int REQUEST_CODE_PREFERENCES = 6;
	
	private VBoxSvc _vmgr;
	private IMachine _machine;
	private MachineView _headerView;
	private ListView _listView;
	private EventThread _thread;
	private EventService _eventService;
	private ServiceConnection localConnection = new ServiceConnection() {
		@Override public void onServiceConnected(ComponentName name, IBinder service) {	
			_eventService = ((EventService.LocalBinder)service).getLocalBinder();
		}
		@Override public void onServiceDisconnected(ComponentName name) {
			_eventService=null;
		}
	};
	private Messenger _messenger = new Messenger( new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case EventThread.WHAT_EVENT:
				IEvent event = BundleBuilder.getProxy(msg.getData(), EventThread.BUNDLE_EVENT, IEvent.class);
				if(event instanceof IMachineStateChangedEvent) {
					VBoxApplication.toast(MachineActivity.this, _machine.getName()+"  changed State: "+_machine.getState());
					updateState();
				}
				break;
			}
		}
	});
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _vmgr = getIntent().getParcelableExtra(VBoxSvc.BUNDLE);
        _machine = BundleBuilder.getProxy(getIntent(), EventThread.BUNDLE_MACHINE, IMachine.class);
		setContentView(R.layout.server_list);
		_headerView = new MachineView(getApp(), this);
		_listView = (ListView)findViewById(R.id.list);
		_listView.addHeaderView(_headerView);
		_listView.setOnItemClickListener(this);
    }
	
	@Override 
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String action = (String)_listView.getAdapter().getItem(position);
		if(action.equals("Start"))	
			new LaunchVMProcessTask(MachineActivity.this, _vmgr).execute(_machine);
		else if(action.equals("Power Off"))	
			new MachineTask<IMachine>("PoweroffTask", this, _vmgr, "Powering Off", false, _machine) {	
			  protected IProgress workWithProgress(IMachine m,  IConsole console, IMachine...i) throws Exception { 	
				  return console.powerDown();
			  }
		  }.execute(_machine);
		else if(action.equals("Reset"))
			new MachineTask<IMachine>("ResetTask", this, _vmgr, "Resetting", true, _machine) {	
			  protected void work(IMachine m,  IConsole console, IMachine...i) throws Exception { 	
				  console.reset(); 
			  }
			  }.execute(_machine);
		else if(action.equals("Pause")) 	
			new MachineTask<IMachine>("PauseTask", this, _vmgr, "Pausing", true, _machine) {	
			  protected void work(IMachine m,  IConsole console, IMachine...i) throws Exception {  
				  console.pause();	
			  }
		  }.execute(_machine);
		else if(action.equals("Resume")) 
			new MachineTask<IMachine>("ResumeTask", this, _vmgr, "Resuming", true, _machine) {	
			  protected void work(IMachine m,  IConsole console, IMachine...i) throws Exception { 	
				  console.resume(); 
			  }
		  }.execute(_machine);
		else if(action.equals("Power Button")) 	
			new MachineTask<IMachine>("ACPITask", this, _vmgr, "ACPI Power Down", true, _machine) {
			  protected void work(IMachine m,  IConsole console,IMachine...i) throws Exception {	
				  console.powerButton(); 	
			  }
		  }.execute(_machine);
		else if(action.equals("Save State")) 	
			new MachineTask<IMachine>("SaveStateTask", this, _vmgr, "Saving State", false, _machine) { 
				protected IProgress workWithProgress(IMachine m, IConsole console, IMachine...i) throws Exception { 	
					return console.saveState(); 
				}
			}.execute(_machine);
		else if(action.equals("Discard State")) 	
			new MachineTask<IMachine>("DiscardStateTask", this, _vmgr, "Discarding State", true, _machine) { 
				protected void work(IMachine m, IConsole console, IMachine...i) throws Exception { 	
					console.discardSavedState(true); 
				}
			}.execute(_machine);
		else if(action.equals("Take Snapshot")) 	{
			new TakeSnapshotDialog(this, _vmgr, _machine).show();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateState();
		_thread = new EventThread(TAG , _vmgr);
		_thread.addListener(_messenger);
		_thread.start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CODE_PREFERENCES) {
			if(_eventService==null && VBoxApplication.getNotificationsPreference(this))
				bindService(new Intent(MachineActivity.this, EventService.class).putExtra(VBoxSvc.BUNDLE, _vmgr), localConnection, Service.BIND_AUTO_CREATE);
			else if(_eventService!=null && !VBoxApplication.getNotificationsPreference(this)) {
				unbindService(localConnection);
			}
		}
	}

	@Override
	protected void onPause() {
		_thread.quit();
		try {
			if(_vmgr.getVBox().getSessionObject().getState().equals(SessionState.LOCKED)) 
				_vmgr.getVBox().getSessionObject().unlockMachine();
		} catch (IOException e) {
			Log.e(TAG, "Exception unlocking machine", e);
		}
		super.onPause();
	}
	
	private void updateState() {
		new UpdateMachineViewTask(this, _vmgr).execute(_machine);
	}
	
	public VBoxApplication getApp() { 
		return (VBoxApplication)getApplication(); 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.machine_options_menu, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.removeItem(R.id.machine_option_menu_refresh);
		menu.removeItem(R.id.machine_option_menu_preferences);
		menu.removeItem(R.id.machine_option_menu_metrics);
		menu.removeItem(R.id.machine_option_menu_glmetrics);
		getMenuInflater().inflate(R.menu.machine_options_menu, menu);
		if(!_machine.getState().equals(MachineState.RUNNING)) {
			menu.removeItem(R.id.machine_option_menu_metrics);
			menu.removeItem(R.id.machine_option_menu_glmetrics);
		}
		if(!VBoxApplication.getBetaEnabledPreference(this))
			menu.removeItem(R.id.machine_option_menu_glmetrics);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.machine_option_menu_refresh:
			updateState();
			return true;
		case R.id.machine_option_menu_preferences:
			startActivityForResult(new Intent(this, PreferencesActivity.class), REQUEST_CODE_PREFERENCES);
			return true;
		case R.id.machine_option_menu_metrics:
			startActivity(new Intent(this, MetricActivity.class).putExtra(VBoxSvc.BUNDLE, _vmgr)
				.putExtra(MetricActivity.INTENT_IMPLEMENTATION, MetricView.Implementation.SURFACEVIEW.name())
				.putExtra(MetricActivity.INTENT_TITLE, _machine.getName() + " Metrics")
				.putExtra(MetricActivity.INTENT_OBJECT, _machine.getIdRef() )
				.putExtra(MetricActivity.INTENT_RAM_AVAILABLE, _machine.getMemorySize() )
				.putExtra(MetricActivity.INTENT_CPU_METRICS , new String[] { "CPU/Load/User",  "CPU/Load/Kernel"  } )
				.	putExtra(MetricActivity.INTENT_RAM_METRICS , new String[] {  "RAM/Usage/Used" } ));
			return true;
		case R.id.machine_option_menu_glmetrics:
			startActivity(new Intent(this, MetricActivity.class).putExtra(VBoxSvc.BUNDLE, _vmgr)
					.putExtra(MetricActivity.INTENT_IMPLEMENTATION, MetricView.Implementation.OPENGL.name())
					.putExtra(MetricActivity.INTENT_TITLE, _machine.getName() + " Metrics")
					.putExtra(MetricActivity.INTENT_OBJECT, _machine.getIdRef() )
					.putExtra(MetricActivity.INTENT_RAM_AVAILABLE, _machine.getMemorySize())
					.putExtra(MetricActivity.INTENT_CPU_METRICS , new String[] { "CPU/Load/User", "CPU/Load/Kernel" } )
				.	putExtra(MetricActivity.INTENT_RAM_METRICS , new String[] {  "RAM/Usage/Used" } ));
			return true;
		default:
			return true;
		}
	}
	
	/**
	 * Load Machine properties from web server
	 */
	class UpdateMachineViewTask extends BaseTask<IMachine, IMachine> {
		
		public UpdateMachineViewTask(Context activity, VBoxSvc vmgr) {
			super(UpdateMachineViewTask.class.getSimpleName(), activity, vmgr, "Loading Machine");
		}
		
		@Override 
		protected IMachine work(IMachine... m) throws Exception {
			_machine.clearCache();
			m[0].getCurrentStateModified(); m[0].getOSTypeId(); m[0].getName(); m[0].getState();
			if(m[0].getCurrentSnapshot()!=null)   
				m[0].getCurrentSnapshot().getName(); 
			return m[0];
		}

		@Override
		protected void onPostExecute(IMachine result) {
			super.onPostExecute(result);
			_headerView.update(result);
			_listView.setAdapter(new MachineActionAdapter(MachineActivity.this, getApp().getActions(_machine.getState())));
		}
	}
	
	/**
	 * List Adapter for Virtual Machine Actions 
	 */
	class MachineActionAdapter extends ArrayAdapter<String> {
		private final LayoutInflater _layoutInflater;
		
		public MachineActionAdapter(Context context, String []strings) {
			super(context, 0, strings);
			_layoutInflater = LayoutInflater.from(context);
		}

		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) 
				view = _layoutInflater.inflate(R.layout.machine_action_item, parent, false);
			((TextView)view.findViewById(R.id.action_item_text)).setText(getItem(position));
			((ImageView)view.findViewById(R.id.action_item_icon)).setImageResource( getApp().getDrawableResource(getItem(position)));
			return view;
		}
	}
}
