package com.kedzie.vbox.machine;

import java.util.Arrays;
import java.util.List;

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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.PreferencesActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.Utils;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMachineStateChangedEvent;
import com.kedzie.vbox.api.IManagedObjectRef;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.metrics.MetricActivity;
import com.kedzie.vbox.metrics.MetricView;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.BaseTask;
import com.kedzie.vbox.task.ConfigureMetricsTask;
import com.kedzie.vbox.task.LaunchVMProcessTask;
import com.kedzie.vbox.task.MachineTask;


public class MachineListActivity extends Activity implements AdapterView.OnItemClickListener {
	private static final int REQUEST_CODE_PREFERENCES = 6;
	protected static final String TAG = MachineListActivity.class.getSimpleName();
	
	private VBoxSvc _vmgr;
	private List<IMachine> _machines;
	private ListView _listView;
	private EventThread _eventThread;
	private Messenger _messenger = new Messenger(new Handler() {
		@Override
		public void handleMessage(Message msg) {
			IEvent event = BundleBuilder.getProxy(msg.getData(), EventThread.BUNDLE_EVENT, IEvent.class);
			if(event instanceof IMachineStateChangedEvent) {
				IMachine eventMachine = BundleBuilder.getProxy(msg.getData(), EventThread.BUNDLE_MACHINE, IMachine.class);
				int pos = getAdapter().getPosition(eventMachine);
				getAdapter().remove(eventMachine);
				getAdapter().insert(eventMachine, pos);
				getAdapter().notifyDataSetChanged();
				Toast.makeText(MachineListActivity.this, eventMachine.getName() + "  changed State: " + eventMachine.getState(), Toast.LENGTH_LONG).show();
			} 
		} });
	private EventService _eventService;
	private ServiceConnection localConnection = new ServiceConnection() {
		@Override public void onServiceConnected(ComponentName name, IBinder service) {	
			_eventService = ((EventService.LocalBinder)service).getLocalBinder();
		}
		@Override public void onServiceDisconnected(ComponentName name) {
			_eventService=null;
		}
	};
	
	@SuppressWarnings("unchecked") @Override
    public void onCreate(Bundle savedInstanceState) {
       	super.onCreate(savedInstanceState);
       	setContentView(R.layout.server_list);
       	_listView = (ListView)findViewById(R.id.list);
       	registerForContextMenu(_listView);
       	_listView.setOnItemClickListener(this);
        _vmgr = getIntent().getParcelableExtra(VBoxSvc.BUNDLE);
    	setTitle("VirtualBox v." + _vmgr.getVBox().getVersion());
    	
    	if(getLastNonConfigurationInstance()!=null) { 
    		_machines = (List<IMachine>)getLastNonConfigurationInstance();
    		_listView.setAdapter(new MachineListAdapter(MachineListActivity.this, _machines));
    		startEventListener();
    	} else
    		new LoadMachinesTask(this, _vmgr).execute();
    }
	
	protected void startEventListener() {
		if(_eventService==null && Utils.getNotificationsPreference(this))
			bindService(new Intent(MachineListActivity.this, EventService.class).putExtra(VBoxSvc.BUNDLE, _vmgr), localConnection, Service.BIND_AUTO_CREATE);
		_eventThread = new EventThread(TAG , _vmgr, VBoxEventType.MACHINE_EVENT);
		_eventThread.addListener(_messenger);
		_eventThread.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==REQUEST_CODE_PREFERENCES) {
			new ConfigureMetricsTask(this, _vmgr).execute();
			if(_eventService==null && Utils.getNotificationsPreference(this))
				bindService(new Intent(MachineListActivity.this, EventService.class).putExtra(VBoxSvc.BUNDLE, _vmgr), localConnection, Service.BIND_AUTO_CREATE);
			else if(_eventService!=null && !Utils.getNotificationsPreference(this)) {
				unbindService(localConnection);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected ArrayAdapter<IMachine> getAdapter() {
		return (ArrayAdapter<IMachine>)_listView.getAdapter();
	}

	public VBoxApplication getApp() { 
		return (VBoxApplication)getApplication(); 
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(MachineListActivity.this, MachineTabActivity.class).putExtra(VBoxSvc.BUNDLE, _vmgr);
		BundleBuilder.addProxy(intent, "machine", getAdapter().getItem(position) );
		startActivity(intent);
	}
	
	@Override 
	public Object onRetainNonConfigurationInstance() {
		return _machines;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(_machines!=null)
			startEventListener();
	}

	@Override 
	protected void onPause() {
		if(_eventThread!=null)
			_eventThread.quit();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		try {  
			if(_eventService!=null)
				unbindService(localConnection);
			if(_vmgr.getVBox()!=null)  
				_vmgr.getVBox().logoff(); 
		} catch (Exception e) { 
			Log.e(TAG, "error ", e); 
		} 
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.machine_list_options_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.removeItem(R.id.machine_list_option_menu_refresh);
		menu.removeItem(R.id.machine_list_option_menu_preferences);
		menu.removeItem(R.id.machine_list_option_menu_metrics);
		menu.removeItem(R.id.machine_list_option_menu_glmetrics);
		getMenuInflater().inflate(R.menu.machine_list_options_menu, menu);
		if(!Utils.getBetaEnabledPreference(this))
			menu.removeItem(R.id.machine_list_option_menu_glmetrics);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.machine_list_option_menu_refresh:
			new LoadMachinesTask(this, _vmgr).execute();
			return true;
		case R.id.machine_list_option_menu_glmetrics:
		case R.id.machine_list_option_menu_metrics:
			Intent intent = new Intent(this, MetricActivity.class).putExtra(VBoxSvc.BUNDLE, _vmgr)
					.putExtra(MetricActivity.INTENT_TITLE, "Host Metrics")
					.putExtra(MetricActivity.INTENT_OBJECT, _vmgr.getVBox().getHost().getIdRef() )
					.putExtra(MetricActivity.INTENT_RAM_AVAILABLE, _vmgr.getVBox().getHost().getMemorySize())
					.putExtra(MetricActivity.INTENT_CPU_METRICS , new String[] { "CPU/Load/User", "CPU/Load/Kernel" } )
					.putExtra(MetricActivity.INTENT_RAM_METRICS , new String[] {  "RAM/Usage/Used" })
					.putExtra(MetricActivity.INTENT_IMPLEMENTATION, item.getItemId()==R.id.machine_list_option_menu_metrics ? 
									MetricView.Implementation.SURFACEVIEW.name() : MetricView.Implementation.OPENGL.name());
			startActivity(intent);
			return true;
		case R.id.machine_list_option_menu_preferences:
			startActivityForResult(new Intent(this, PreferencesActivity.class),REQUEST_CODE_PREFERENCES);
			return true;
		default:
			return true;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.machines_list_context_menu, menu);
		menu.setHeaderTitle("Machine Operations");
		IMachine m = getAdapter().getItem(((AdapterContextMenuInfo)menuInfo).position);
		List<String> actions = Arrays.asList(getApp().getActions(m.getState()));
		if(!actions.contains("Start"))
			menu.removeItem(R.id.machines_context_menu_start);
		if(!actions.contains("Power Off"))
			menu.removeItem(R.id.machines_context_menu_poweroff);
		if(!actions.contains("Power Button"))	
			menu.removeItem(R.id.machines_context_menu_acpi);
		if(!actions.contains("Reset"))
			menu.removeItem(R.id.machines_context_menu_reset);
		if(!actions.contains("Pause"))
			menu.removeItem(R.id.machines_context_menu_pause);
		if(!actions.contains("Resume"))
			menu.removeItem(R.id.machines_context_menu_resume);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  IMachine m = getAdapter().getItem( ((AdapterContextMenuInfo) item.getMenuInfo()).position);
	  switch (item. getItemId()) {
	  case R.id.machines_context_menu_start:  
		  new LaunchVMProcessTask(this, _vmgr).execute(m);	  
		  break;
	  case R.id.machines_context_menu_poweroff:   
		  new MachineTask<IMachine>("PoweroffTask", this, _vmgr, "Powering Off", false, m) {	
			  protected IProgress workWithProgress(IMachine m,  IConsole console, IMachine...i) throws Exception { 	
				  return console.powerDown();
			  }
		  }.execute(m);
		  break;
	  case R.id.machines_context_menu_reset:	 
		  new MachineTask<IMachine>("ResetTask", this, _vmgr, "Resetting", true, m) {	
			  protected void work(IMachine m,  IConsole console, IMachine...i) throws Exception { 	
				  console.reset(); 
			  }
			  }.execute(m);
		  break;
	  case R.id.machines_context_menu_resume:	  
		  new MachineTask<IMachine>("ResumeTask", this, _vmgr, "Resuming", true, m) {	
			  protected void work(IMachine m,  IConsole console, IMachine...i) throws Exception { 	
				  console.resume(); 
			  }
		  }.execute(m);
		  break;
	  case R.id.machines_context_menu_pause:	  
		  new MachineTask<IMachine>("PauseTask", this, _vmgr, "Pausing", true, m) {	
			  protected void work(IMachine m,  IConsole console, IMachine...i) throws Exception {  
				  console.pause();	
			  }
		  }.execute(m);
		  break;
	  case R.id.machines_context_menu_acpi:	  
		  new MachineTask<IMachine>("ACPITask", this, _vmgr, "ACPI Power Down", true, m) {
			  protected void work(IMachine m,  IConsole console,IMachine...i) throws Exception {	
				  console.powerButton(); 	
			  }
		  }.execute(m);
		  break;
	  }
	  return true;
	}
	
	/** 
	 * Load the Machines 
	 */
	class LoadMachinesTask extends BaseTask<Void, List<IMachine>>	{
		public LoadMachinesTask(Context ctx, VBoxSvc vmgr) { 
			super( "LoadMachinesTask", ctx, vmgr, "Loading Machines");
		}

		@Override
		protected List<IMachine> work(Void... params) throws Exception {
			List<IMachine> machines =_vmgr.getVBox().getMachines(); 
			for(IMachine m :  machines) {
				//cache property values to avoid remote calls
				m.getName();  m.getOSTypeId(); m.getCurrentStateModified(); if(m.getCurrentSnapshot()!=null) m.getCurrentSnapshot().getName();
			}
			_vmgr.getVBox().getPerformanceCollector().setupMetrics(new String[] { "*:" }, 
					Utils.getPeriodPreference(context), 
					1, 
					(IManagedObjectRef)null);
			return machines;
		}

		@Override
		protected void onPostExecute(List<IMachine> result)	{
			super.onPostExecute(result);
			_machines = result;
			if(result!=null)	{
				_listView.setAdapter(new MachineListAdapter(MachineListActivity.this, result));
				getAdapter().setNotifyOnChange(false);
				startEventListener();
			}
		}
	}
	
	/**
	 * List adapter for Virtual Machines
	 */
	class MachineListAdapter extends ArrayAdapter<IMachine> {
		public MachineListAdapter(MachineListActivity context, List<IMachine> machines) {
			super(context, 0, machines);
		}
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) view = new MachineView(getApp(), MachineListActivity.this);
			((MachineView)view).update(getItem(position));
			return view;
		}
	}
}
