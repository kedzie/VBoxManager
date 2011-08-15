package com.kedzie.vbox.machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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
import android.widget.Toast;
import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMachineStateChangedEvent;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.jaxb.MachineState;
import com.kedzie.vbox.server.PreferencesActivity;
import com.kedzie.vbox.task.BaseTask;
import com.kedzie.vbox.task.LaunchVMProcessTask;
import com.kedzie.vbox.task.MachineTask;


public class MachineListActivity extends BaseListActivity<IMachine> {
	protected static final String TAG = "vbox."+MachineListActivity.class.getSimpleName();
	
	private VBoxSvc vmgr;
	private List<IMachine> _machines;
	private Handler _eventHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case EventService.WHAT_EVENT:
					IEvent event = vmgr.getEventProxy(msg.getData().getString("evt"));
					if(event instanceof IMachineStateChangedEvent) {
						IMachineStateChangedEvent mEvent = (IMachineStateChangedEvent)event;
						IMachine eventMachine = vmgr.getProxy(IMachine.class, msg.getData().getString("machine"));
						getAdapter().setNotifyOnChange(false);
						int pos = getAdapter().getPosition(eventMachine);
						getAdapter().remove(eventMachine);
						getAdapter().insert(eventMachine, pos);
						getAdapter().notifyDataSetChanged();
						Toast.makeText(MachineListActivity.this, "MachineStateChangedEvent : " + mEvent.getState(), Toast.LENGTH_SHORT).show();
					} 
				break;
			case WHAT_ERROR:
				showAlert(msg.getData().getString("exception"));
				break;
			}
		}
	};
	private Messenger _messenger = new Messenger(_eventHandler);
	private EventService eventService;
	private ServiceConnection localConnection = new ServiceConnection() {
		@Override public void onServiceConnected(ComponentName name, IBinder service) {	
			eventService=((EventService.LocalBinder)service).getLocalBinder();
			eventService.setMessenger(_messenger);
		}
		@Override public void onServiceDisconnected(ComponentName name) { eventService=null;	}
	};
	
	@SuppressWarnings("unchecked") @Override
    public void onCreate(Bundle savedInstanceState) {
       	super.onCreate(savedInstanceState);
       	registerForContextMenu(getListView());
       	getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivity(new Intent().setClass(MachineListActivity.this, MachineActivity.class).putExtra("vmgr", vmgr).putExtra("machine", getAdapter().getItem(position).getIdRef()));
			}
		});
       	vmgr = getIntent().getParcelableExtra("vmgr");
		setTitle("VirtualBox v." + vmgr.getVBox().getVersion());
		if(getLastNonConfigurationInstance()==null)
			new LoadMachinesTask(MachineListActivity.this, vmgr).execute();
		else {
			_machines = (List<IMachine>)getLastNonConfigurationInstance();
			setListAdapter(new MachineListAdapter(MachineListActivity.this, _machines));
		}
    }
	
	/* @see android.app.Activity#onRetainNonConfigurationInstance() */
	@Override public Object onRetainNonConfigurationInstance() {
		return _machines;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		bindService(new Intent(this, EventService.class).putExtra("vmgr", vmgr).putExtra("listener", _messenger), localConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		unbindService(localConnection);
        super.onStop();
	}

	@Override
	protected void onDestroy() {
		try {  
			if(vmgr.getVBox()!=null)  vmgr.getVBox().logoff(); 
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.machine_list_option_menu_refresh:
			new LoadMachinesTask(this, vmgr).execute();
			return true;
		case R.id.machine_list_option_menu_metrics:
			startActivity(new Intent(this, MetricActivity.class).putExtra("vmgr", vmgr).putExtra("title", "Host Metrics")
				.putExtra(MetricActivity.INTENT_OBJECT, vmgr.getVBox().getHost().getIdRef() )
				.putExtra(MetricActivity.INTENT_RAM_AVAILABLE, vmgr.getVBox().getHost().getMemorySize())
				.putExtra("cpuMetrics" , new String[] { "CPU/Load/User", "CPU/Load/Kernel" } )
			.	putExtra("ramMetrics" , new String[] {  "RAM/Usage/Used" } ));
			return true;
		case R.id.machine_list_option_menu_preferences:
			startActivity(new Intent(this, PreferencesActivity.class));
			return true;
		default:
			return true;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.machines_list_context_menu, menu);
		IMachine m = (IMachine)getListAdapter().getItem(((AdapterContextMenuInfo)menuInfo).position);
		List<String> actions = Arrays.asList(VBoxApplication.getActions(m.getState()));
		menu.findItem(R.id.machines_context_menu_start).setEnabled(actions.contains("Start"));
		menu.findItem(R.id.machines_context_menu_poweroff).setEnabled(actions.contains("Power Off"));
		menu.findItem(R.id.machines_context_menu_acpi).setEnabled(actions.contains("Power Button"));
		menu.findItem(R.id.machines_context_menu_reset).setEnabled(actions.contains("Reset"));
		menu.findItem(R.id.machines_context_menu_pause).setEnabled(actions.contains("Pause"));
		menu.findItem(R.id.machines_context_menu_resume).setEnabled(actions.contains("Resume"));
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  IMachine m = (IMachine)getListAdapter().getItem( ((AdapterContextMenuInfo) item.getMenuInfo()).position);
	  switch (item. getItemId()) {
	  case R.id.machines_context_menu_start:  
		  new LaunchVMProcessTask(this, vmgr).execute(m);	  
		  break;
	  case R.id.machines_context_menu_poweroff:   
		  new MachineTask(this, vmgr, "Powering Off", false) {	protected IProgress workWithProgress(IMachine m,  IConsole console) throws Exception { 	return console.powerDown(); }}.execute(m);
		  break;
	  case R.id.machines_context_menu_reset:	 
		  new MachineTask(this, vmgr, "Resetting", true) {	protected void work(IMachine m,  IConsole console) throws Exception { 	console.reset(); }}.execute(m);
		  break;
	  case R.id.machines_context_menu_resume:	  
		  new MachineTask(this, vmgr, "Resuming", true) {	protected void work(IMachine m,  IConsole console) throws Exception { 	console.resume(); }}.execute(m);
		  break;
	  case R.id.machines_context_menu_pause:	  
		  new MachineTask(this, vmgr, "Pausing", true) {	protected void work(IMachine m,  IConsole console) throws Exception {  console.pause();	}}.execute(m);
		  break;
	  case R.id.machines_context_menu_acpi:	  
		  new MachineTask(this, vmgr, "ACPI Power Down", true) {protected void work(IMachine m,  IConsole console) throws Exception {	console.powerButton(); 	}}.execute(m);
		  break;
	  }
	  return true;
	}
	
	/** Load the Machines */
	class LoadMachinesTask extends BaseTask<Void, List<IMachine>>	{
		public LoadMachinesTask(Context ctx, VBoxSvc vmgr) { super( ctx, vmgr, "Loading Machines", true); 	}

		@Override
		protected List<IMachine> work(Void... params) throws Exception {
			List<IMachine> machines =_vmgr.getVBox().getMachines(); 
			if(machines==null || machines.size()==0) return new ArrayList<IMachine>();
			Collection<String> running = new ArrayList<String>();
			for(IMachine m :  machines) {
				m.getName();  m.getOSTypeId(); m.getState(); if(m.getCurrentSnapshot()!=null) m.getCurrentSnapshot().getName(); //cache the values
				if(MachineState.RUNNING.equals(m.getState()))	running.add(m.getIdRef());
			}
			_vmgr.getVBox().getPerformanceCollector().setupMetrics( running.toArray(new String[running.size()]), new String [] { "*:" }, getApp().getPeriod(), getApp().getCount()  );
			return machines;
		}

		@Override
		protected void onPostExecute(List<IMachine> result)	{
			super.onPostExecute(result);
			_machines = result;
			setListAdapter(new MachineListAdapter(MachineListActivity.this, result));
		}
	}
	
	class MachineListAdapter extends ArrayAdapter<IMachine> {
		
		public MachineListAdapter(MachineListActivity context, List<IMachine> machines) {
			super(context, 0, machines);
		}

		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)	view = new MachineView(MachineListActivity.this);
			((MachineView)view).update(getItem(position));
			return view;
		}
	}
	
}
