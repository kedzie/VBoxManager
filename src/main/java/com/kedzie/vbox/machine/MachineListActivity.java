package com.kedzie.vbox.machine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.virtualbox_4_1.MachineState;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.kedzie.vbox.api.ISessionStateChangedEvent;
import com.kedzie.vbox.api.ISnapshot;
import com.kedzie.vbox.task.BaseTask;
import com.kedzie.vbox.task.LaunchVMProcessTask;
import com.kedzie.vbox.task.MachineTask;


public class MachineListActivity extends BaseListActivity<IMachine> {
	protected static final String TAG = "vbox."+MachineListActivity.class.getSimpleName();
	
	private VBoxSvc vmgr = new VBoxSvc();
	private EventThread eventThread;
	private Messenger eventServiceMessenger;
	
	private Handler _eventHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case EventThread.WHAT_EVENT:
				try {
					IEvent event = vmgr.getEventProxy(msg.getData().getString("evt"));
					Log.i(TAG, "EVENT HANDLER REcieved message: " + event.getType());
					if(event instanceof IMachineStateChangedEvent) {
						IMachineStateChangedEvent mEvent = (IMachineStateChangedEvent)event;
						IMachine eventMachine = vmgr.getVBox().findMachine(mEvent.getMachineId());
						Log.i(TAG, "MAchine event: " + eventMachine.getName());
						int pos = getAdapter().getPosition(eventMachine);
						getAdapter().remove(eventMachine);
						getAdapter().insert(eventMachine, pos);
						Toast.makeText(MachineListActivity.this, "MachineStateChangedEvent : " + mEvent.getState(), Toast.LENGTH_SHORT).show();
					} else if(event instanceof ISessionStateChangedEvent) {
						ISessionStateChangedEvent sEvent = (ISessionStateChangedEvent)event;
						Toast.makeText(MachineListActivity.this, "SessionStateChangedEvent : " + sEvent.getState(), Toast.LENGTH_SHORT).show();
					}
				} catch (IOException e) {
					Log.e(TAG, e.getMessage(), e);
				}
				break;
			case EventThread.WHAT_ERROR:
				showAlert(msg.getData().getString("exception"));
				break;
			}
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
       	super.onCreate(savedInstanceState);
       	registerForContextMenu(getListView());
       	getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				IMachine m = (IMachine)getListView().getAdapter().getItem(position);
				Intent intent = new Intent().setClass(MachineListActivity.this, MachineActivity.class);
				intent.putExtra("vmgr", vmgr);
				intent.putExtra("machine", m.getIdRef());
				startActivity(intent);
			}
		});
       	vmgr = getIntent().getParcelableExtra("vmgr");
       	try {
			setTitle("VirtualBox v." + vmgr.getVBox().getVersion());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		new LoadMachinesTask(MachineListActivity.this, vmgr).execute();
    }
	
	@Override
	protected void onStart() {
		super.onStart();
//		Intent intent = new Intent(this, EventService.class);
//		bindService(intent, new ServiceConnection() {
//			@Override public void onServiceConnected(ComponentName name, IBinder service) {
//				eventServiceMessenger = new Messenger(service);
//				try {
//					eventServiceMessenger.send(new Message());
//				} catch (RemoteException e) {
//					Log.e(TAG, e.getMessage(), e);
//				}
//			}
//			@Override public void onServiceDisconnected(ComponentName name) {
//				eventServiceMessenger = null;
//			}
//		}, 0);
		Log.i(TAG, "Start");
		eventThread = new EventThread( _eventHandler, vmgr);
		eventThread.start();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "Stop");
		boolean retry = true;
        eventThread.postStop();
        while (retry) {
            try {
                eventThread.join();
                retry = false;
            } catch (InterruptedException e) { }
        }
        super.onStop();
	}

	@Override
	protected void onDestroy() {
		try {  
			if(vmgr.getVBox()!=null) 
				vmgr.getVBox().logoff(); 
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
			Intent intent = new Intent(this, MetricActivity.class);
			intent.putExtra("vmgr", vmgr);
			intent.putExtra(MetricActivity.INTENT_OBJECT, vmgr.getVBox().getHost().getIdRef() );
			intent.putExtra("title", "Host Metrics");
			intent.putExtra(MetricActivity.INTENT_RAM_AVAILABLE, vmgr.getVBox().getHost().getMemorySize());
			intent.putExtra("cpuMetrics" , new String[] { "CPU/Load/User", "CPU/Load/Kernel" } );
			intent.putExtra("ramMetrics" , new String[] {  "RAM/Usage/Used" } );
			startActivity(intent);
			return true;
		case R.id.machine_list_option_menu_preferences:
			Intent in = new Intent(this, PreferencesActivity.class);
			startActivity(in);
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
	  final IMachine m = (IMachine)getListAdapter().getItem( ((AdapterContextMenuInfo) item.getMenuInfo()).position);
	  switch (item. getItemId()) {
	  case R.id.machines_context_menu_start:  
		  new LaunchVMProcessTask(this, vmgr).execute(m);	  break;
	  case R.id.machines_context_menu_poweroff:   
		  new MachineTask(this, vmgr, "Powering Off", false) {	
				@Override
				protected IProgress workWithProgress(IMachine m, VBoxSvc vmgr, IConsole console) throws Exception { 	return console.powerDown(); }}.execute(m);
		  break;
	  case R.id.machines_context_menu_reset:	 
	  new MachineTask(this, vmgr, "Resetting", true) {	
			@Override
			protected void work(IMachine m, VBoxSvc vmgr, IConsole console) throws Exception { 	console.reset(); }}.execute(m);
	  break;
	  case R.id.machines_context_menu_resume:	  
	  new MachineTask(this, vmgr, "Resuming", true) {	
			@Override
			protected void work(IMachine m, VBoxSvc vmgr, IConsole console) throws Exception { 	console.resume(); }}.execute(m);
	  break;
	  case R.id.machines_context_menu_pause:	  
		  new MachineTask(this, vmgr, "Pausing", true) {	
				@Override
				protected void work(IMachine m, VBoxSvc vmgr, IConsole console) throws Exception {  console.pause();	}}.execute(m);
		  break;
	  case R.id.machines_context_menu_acpi:	  
		  new MachineTask(this, vmgr, "ACPI Power Down", true) {
				@Override
				protected void work(IMachine m, VBoxSvc vmgr, IConsole console) throws Exception {	console.powerButton(); 	}}.execute(m);
		  break;
	  }
	  return true;
	}
	
	/**
	 * Load the Machines
	 */
	class LoadMachinesTask extends BaseTask<Void, List<IMachine>>	{
		public LoadMachinesTask(Context ctx, VBoxSvc vmgr) { super( ctx, vmgr,  "Loading Machines", true); 	}

		@Override
		protected List<IMachine> work(Void... params) throws Exception {
			List<IMachine> machines = _vmgr.getVBox().getMachines();
			Collection<String> running = new ArrayList<String>();
			for(IMachine m : machines) if(MachineState.Running.equals(m.getState()))	running.add(m.getIdRef());
			_vmgr.setupMetrics(context, running.toArray(new String[running.size()]), "*:" );
			return machines;
		}

		@Override
		protected void onPostExecute(List<IMachine> result)	{
			setListAdapter(new MachineListAdapter(MachineListActivity.this, result));
			super.onPostExecute(result);
		}
	}
	
	/**
	 * Machine List Adapter
	 */
	class MachineListAdapter extends ArrayAdapter<IMachine> {
		private final LayoutInflater _layoutInflater;
		
		public MachineListAdapter(MachineListActivity context, List<IMachine> machines) {
			super(context, 0, machines);
			_layoutInflater = LayoutInflater.from(context);
		}

		public long getItemId(int position) {
			return position;
		}
		
		public int getPosition(IMachine m) {
			for(int i=0; i<getCount(); i++) 
				if(m.getIdRef().equals(getItem(i).getIdRef())) 
					return i;
			return -1;
		}

		public View getView(int position, View view, ViewGroup parent) {
			IMachine m = getItem(position);
			if (view == null) {
				Log.i(TAG, "Creating view: " + m.getName());
				view = _layoutInflater.inflate(R.layout.machine_list_item, parent, false);
				((ImageView)view.findViewById(R.id.machine_list_item_ostype)).setImageResource(VBoxApplication.get("os_"+m.getOSTypeId().toLowerCase()));
				((TextView) view.findViewById(R.id.machine_list_item_name)).setText(m.getName());
			}
			Log.i(TAG, "Updating view: " + m.getName());
			MachineState state = m.getState();
			((ImageView)view.findViewById(R.id.machine_list_item_state)).setImageResource( VBoxApplication.get(state) );
			((TextView)view.findViewById(R.id.machine_list_item_state_text)).setText(state.name());
			ISnapshot s = m.getCurrentSnapshot();
			if(s!=null)  ((TextView) view.findViewById(R.id.machine_list_item_snapshot)).setText("("+s.getName() + ")");		
			return view;
		}
	}
	
}