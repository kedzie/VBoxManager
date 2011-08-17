package com.kedzie.vbox.machine;

import java.io.IOException;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.kedzie.vbox.api.jaxb.SessionState;
import com.kedzie.vbox.server.PreferencesActivity;
import com.kedzie.vbox.task.LaunchVMProcessTask;
import com.kedzie.vbox.task.MachineTask;

public class MachineActivity extends BaseListActivity<String>  implements AdapterView.OnItemClickListener {
	protected static final String TAG = MachineActivity.class.getSimpleName();
	
	private VBoxSvc _vmgr;
	private IMachine _machine;
	private EventService eventService;
	private MachineView _headerView;
	private Messenger _messenger = new Messenger( new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case EventService.WHAT_EVENT:
					IEvent event = _vmgr.getProxy(IEvent.class, msg.getData().getString("evt"));
					if(event instanceof IMachineStateChangedEvent)
						updateState();
				break;
			}
		}
	});
	private ServiceConnection localConnection = new ServiceConnection() {
		@Override public void onServiceConnected(ComponentName name, IBinder service) {	
			eventService=((EventService.LocalBinder)service).getLocalBinder();
			eventService.setMessenger(_messenger);
		}
		@Override public void onServiceDisconnected(ComponentName name) {
			eventService=null;	
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _vmgr =getIntent().getParcelableExtra("vmgr");
		_machine = _vmgr.getProxy(IMachine.class, getIntent().getStringExtra("machine"));
		
		_headerView = new MachineView(this);
//		View _headerView = getLayoutInflater().inflate(R.layout.machine_list_item, getListView(), false);
//		((ImageView)_headerView.findViewById(R.id.machine_list_item_ostype)).setImageResource(VBoxApplication.get("os_"+_machine.getOSTypeId().toLowerCase()));
//		((TextView) _headerView.findViewById(R.id.machine_list_item_name)).setText(_machine.getName()); 
		getListView().addHeaderView(_headerView);
		
		getListView().setOnItemClickListener(this);
    }
	
	@Override 
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String action = (String)getListView().getAdapter().getItem(position);
		if(action.equals("Start"))	
			new LaunchVMProcessTask(MachineActivity.this, _vmgr).execute(_machine);
		else if(action.equals("Power Off"))	
			new MachineTask(this, _vmgr, "Powering Off", false) { protected IProgress workWithProgress(IMachine m, IConsole console) throws Exception { 	return console.powerDown(); }}.execute(_machine);
		else if(action.equals("Reset"))
			new MachineTask(this, _vmgr, "Resetting", true) { protected void work(IMachine m, IConsole console) throws Exception { 	console.reset(); }}.execute(_machine);
		else if(action.equals("Pause")) 	
			new MachineTask(this, _vmgr, "Pausing", true) { protected void work(IMachine m, IConsole console) throws Exception { console.pause(); }}.execute(_machine);
		else if(action.equals("Resume")) 
			new MachineTask(this, _vmgr, "Resuming", true) { protected void work(IMachine m, IConsole console) throws Exception { 	console.resume(); }}.execute(_machine);
		else if(action.equals("Power Button")) 	
			new MachineTask(this, _vmgr, "ACPI Power Down", true) { protected void work(IMachine m, IConsole console) throws Exception { console.powerButton(); }}.execute(_machine);
		else if(action.equals("Save State")) 	
			new MachineTask(this, _vmgr, "Saving State", false) { protected IProgress workWithProgress(IMachine m, IConsole console) throws Exception { 	return console.saveState(); }}.execute(_machine);
		else if(action.equals("Discard State")) 	
			new MachineTask(this, _vmgr, "Discarding State", true) { protected void work(IMachine m, IConsole console) throws Exception { 	console.discardSavedState(true); }}.execute(_machine);
		else if(action.equals("Take Snapshot")) 	{
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.snapshot_dialog);
			((Button)dialog.findViewById(R.id.button_save)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new MachineTask(MachineActivity.this, _vmgr, "Taking Snapshot", false) {	
						protected IProgress workWithProgress(IMachine m, IConsole console) throws Exception { 	
							return console.takeSnapshot( ((TextView)dialog.findViewById(R.id.snapshot_name)).getText().toString(),  ((TextView)dialog.findViewById(R.id.snapshot_description)).getText().toString()); 
						}}.execute(_machine);							
				}
			});
			((Button)dialog.findViewById(R.id.button_cancel)).setOnClickListener(new View.OnClickListener() { public void onClick(View v) { dialog.dismiss(); } });
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		updateState();
		bindService(new Intent(this, EventService.class), localConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStop() {
		eventService.setMessenger(null);
		unbindService(localConnection);
		try {
			if(_vmgr.getVBox().getSessionObject().getState().equals(SessionState.LOCKED)) 
				_vmgr.getVBox().getSessionObject().unlockMachine();
		} catch (IOException e) {
			showAlert(e);
		}
		super.onStop();
	}
	
	private void updateState() {
		_machine.clearCache();
		MachineState state = _machine.getState();
		Log.i(TAG, "Update state: " + state);
		_headerView.update(_machine);
//		((ImageView)getListView().findViewById(R.id.machine_list_item_state)).setImageResource( VBoxApplication.get(state) );
//		((TextView)getListView().findViewById(R.id.machine_list_item_state_text)).setText(state.name());
//		if(_machine.getCurrentSnapshot()!=null)  ((TextView) getListView().findViewById(R.id.machine_list_item_snapshot)).setText("("+_machine.getCurrentSnapshot().getName() + ")");		
		setListAdapter(new MachineActionAdapter(this, VBoxApplication.getActions(state)));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.machine_options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.machine_option_menu_refresh:
			updateState();
			return true;
		case R.id.machine_option_menu_metrics:
			startActivity(new Intent(this, MetricActivity.class).putExtra("vmgr", _vmgr)
				.putExtra(MetricActivity.INTENT_RAM_AVAILABLE, _machine.getMemorySize())
				.putExtra(MetricActivity.INTENT_OBJECT, _machine.getIdRef() )
				.putExtra("title", _machine.getName() + " Metrics")
				.putExtra("cpuMetrics" , new String[] { "Guest/CPU/Load/User", "Guest/CPU/Load/Kernel" } )
				.putExtra("ramMetrics" , new String[] {  "Guest/RAM/Usage/Shared", "Guest/RAM/Usage/Cache" } ) );
			return true;
		case R.id.machine_option_menu_preferences:
			startActivity(new Intent(this, PreferencesActivity.class));
			return true;
		default:
			return true;
		}
	}
	
	class MachineActionAdapter extends ArrayAdapter<String> {
		private final LayoutInflater _layoutInflater;
		
		public MachineActionAdapter(Context context, String []strings) {
			super(context, 0, strings);
			_layoutInflater = LayoutInflater.from(context);
		}

		public View getView(int position, View view, ViewGroup parent) {
			if (view == null) { 
				view = _layoutInflater.inflate(R.layout.machine_action_item, parent, false);
				((TextView)view.findViewById(R.id.action_item_text)).setText(getItem(position));
				((ImageView)view.findViewById(R.id.action_item_icon)).setImageResource( VBoxApplication.get(getItem(position)));
			}
			return view;
		}
	}
}