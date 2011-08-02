package com.kedzie.vbox.machine;

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

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.BaseTask;
import com.kedzie.vbox.MachineTask;
import com.kedzie.vbox.PreferencesActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IPerformanceMetric;
import com.kedzie.vbox.api.ISnapshot;
import com.kedzie.vbox.api.WebSessionManager;
import com.kedzie.vbox.event.EventThread;
import com.kedzie.vbox.event.IEvent;
import com.kedzie.vbox.task.LaunchVMProcessTask;
import com.kedzie.vbox.task.PowerDownTask;
import com.kedzie.vbox.task.ResetTask;
import com.kedzie.vbox.task.ResumeTask;


public class MachineListActivity extends BaseListActivity<IMachine> {
	protected static final String TAG = "vbox."+MachineListActivity.class.getSimpleName();
	
	private WebSessionManager vmgr = new WebSessionManager();
	private List<IPerformanceMetric> baseMetrics;
	private EventThread eventThread;
	private Handler _eventHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "EVENT HANDLER REcieved message");
//			IEvent event = vmgr.getProxy(IEvent.class, msg.getData().getString("evt"));
//			Log.i(TAG, "EVENT HANDLER REcieved message: " + event.getType());
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
				intent.putExtra("machine", m.getId());
				startActivity(intent);
			}
		});
       	new BaseTask<String, String>(this, vmgr, "Connecting", true) {
			@Override
			protected String work(String... params) throws Exception {
				_vmgr.logon(params[0], params[1], params[2]);
				baseMetrics = _vmgr.setupMetrics(MachineListActivity.this, _vmgr.getVBox().getHost().getId());
				return _vmgr.getVBox().getVersion();
			}
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				Log.i(TAG, "Logged in " + result);
				new LoadMachinesTask(MachineListActivity.this, vmgr).execute();
			}
       	}.execute(getIntent().getStringExtra("url"),getIntent().getStringExtra("username"), getIntent().getStringExtra("password"));
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		eventThread = new EventThread(this, _eventHandler);
		eventThread.start();
	}

	@Override
	protected void onStop() {
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
			intent.putExtra("object", vmgr.getVBox().getHost().getId() );
			String []bMetrics = new String[baseMetrics.size()];
			for(int i=0; i<baseMetrics.size(); i++) bMetrics[i] = baseMetrics.get(i).getId();
			intent.putExtra("baseMetrics", bMetrics);
			intent.putExtra("title", "Host Metrics");
			intent.putExtra("ram_available", vmgr.getVBox().getHost().getMemorySize());
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
	  case R.id.machines_context_menu_start:  new LaunchVMProcessTask(this, vmgr).execute(m);	  break;
	  case R.id.machines_context_menu_poweroff:   new PowerDownTask(this, vmgr).execute(m);  break;
	  case R.id.machines_context_menu_reset:	  new ResetTask(this, vmgr).execute(m);	  break;
	  case R.id.machines_context_menu_resume:	  new ResumeTask(this, vmgr).execute(m);	  break;
	  case R.id.machines_context_menu_pause:	  
		  new MachineTask(this, vmgr, "Pausing") {	
				@Override
				protected void work(IMachine m, WebSessionManager vmgr, IConsole console) throws Exception { 
					console.pause();
				}}.execute(m);
		  break;
	  case R.id.machines_context_menu_acpi:	  
		  new MachineTask(this, vmgr, "ACPI Power Down") {
				@Override
				protected void work(IMachine m, WebSessionManager vmgr, IConsole console) throws Exception {
					console.powerButton();
				}}.execute(m);
		  break;
	  }
	  return true;
	}
	
	class LoadMachinesTask extends BaseTask<Void, List<IMachine>>	{
		public LoadMachinesTask(Context ctx, WebSessionManager vmgr) { super( ctx, vmgr,  "Loading Machines", true); 	}

		@Override
		protected List<IMachine> work(Void... params) throws Exception {
			List<IMachine> machines = _vmgr.getVBox().getMachines();
			Collection<String> running = new ArrayList<String>();
			for(IMachine m : machines) if(MachineState.Running.equals(m.getState()))	running.add(m.getId());
			_vmgr.setupMetrics(context, running.toArray(new String[running.size()]) );
			return machines;
		}

		@Override
		protected void onPostExecute(List<IMachine> result)	{
			setListAdapter(new MachineListAdapter(MachineListActivity.this, result));
			super.onPostExecute(result);
		}
	}
	
	class MachineListAdapter extends ArrayAdapter<IMachine> {
		private final LayoutInflater _layoutInflater;
		
		public MachineListAdapter(MachineListActivity context, List<IMachine> machines) {
			super(context, 0, machines);
			_layoutInflater = LayoutInflater.from(context);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View view, ViewGroup parent) {
			IMachine m = getItem(position);
			if (view == null) {
				view = _layoutInflater.inflate(R.layout.machine_list_item, parent, false);
				((ImageView)view.findViewById(R.id.machine_list_item_ostype)).setImageResource(VBoxApplication.get("os_"+m.getOSTypeId().toLowerCase()));
				((TextView) view.findViewById(R.id.machine_list_item_name)).setText(m.getName());
			}
			MachineState state = m.getState();
			((ImageView)view.findViewById(R.id.machine_list_item_state)).setImageResource( VBoxApplication.get(state) );
			((TextView)view.findViewById(R.id.machine_list_item_state_text)).setText(state.name());
			ISnapshot s = m.getCurrentSnapshot();
			if(s!=null)  ((TextView) view.findViewById(R.id.machine_list_item_snapshot)).setText("("+s.getName() + ")");		
			return view;
		}
	}
	
}
