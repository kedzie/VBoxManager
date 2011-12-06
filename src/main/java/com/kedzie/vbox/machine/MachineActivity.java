package com.kedzie.vbox.machine;

import java.io.IOException;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.TextView;

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
import com.kedzie.vbox.common.EventService;
import com.kedzie.vbox.common.EventThread;
import com.kedzie.vbox.common.MetricActivity;
import com.kedzie.vbox.common.PreferencesActivity;
import com.kedzie.vbox.task.LaunchVMProcessTask;
import com.kedzie.vbox.task.MachineTask;

public class MachineActivity extends Activity  implements AdapterView.OnItemClickListener {
	protected static final String TAG = MachineActivity.class.getSimpleName();
	
	private VBoxSvc _vmgr;
	private IMachine _machine;
	private EventService eventService;
	private MachineView _headerView;
	private ListView _listView;
	private EventThread _thread;
	private Messenger _messenger = new Messenger( new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case EventThread.WHAT_EVENT:
				Log.d(TAG, "Got Event");
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
		setContentView(R.layout.server_list);
		_headerView = new MachineView(getApp(), this);
		_listView = (ListView)findViewById(R.id.list);
		_listView.addHeaderView(_headerView);
		_listView.setOnItemClickListener(this);
		_thread = new EventThread(_vmgr);
    }
	
	@Override 
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String action = (String)_listView.getAdapter().getItem(position);
		if(action.equals("Start"))	
			new LaunchVMProcessTask(MachineActivity.this, _vmgr).execute(_machine);
		else if(action.equals("Power Off"))	
			new MachineTask("LaunchVMProcessTask", this, _vmgr, "Powering Off", false) { 
				protected IProgress workWithProgress(IMachine m, IConsole console) throws Exception { 	
					return console.powerDown(); 
				}
			}.execute(_machine);
		else if(action.equals("Reset"))
			new MachineTask("ResetTask", this, _vmgr, "Resetting", true) { 
				protected void work(IMachine m, IConsole console) throws Exception { 	
					console.reset(); 
				}
			}.execute(_machine);
		else if(action.equals("Pause")) 	
			new MachineTask("PauseTask", this, _vmgr, "Pausing", true) { 
				protected void work(IMachine m, IConsole console) throws Exception { 
					console.pause();
				}
			}.execute(_machine);
		else if(action.equals("Resume")) 
			new MachineTask("ResumeTask", this, _vmgr, "Resuming", true) { 
				protected void work(IMachine m, IConsole console) throws Exception { 	
					console.resume(); 
				}
			}.execute(_machine);
		else if(action.equals("Power Button")) 	
			new MachineTask("PowerButtonTask", this, _vmgr, "ACPI Power Down", true) { 
				protected void work(IMachine m, IConsole console) throws Exception { 
					console.powerButton(); 
				}
			}.execute(_machine);
		else if(action.equals("Save State")) 	
			new MachineTask("SaveStateTask", this, _vmgr, "Saving State", false) { 
				protected IProgress workWithProgress(IMachine m, IConsole console) throws Exception { 	
					return console.saveState(); 
				}
			}.execute(_machine);
		else if(action.equals("Discard State")) 	
			new MachineTask("DiscardStateTask", this, _vmgr, "Discarding State", true) { 
				protected void work(IMachine m, IConsole console) throws Exception { 	
					console.discardSavedState(true); 
				}
			}.execute(_machine);
		else if(action.equals("Take Snapshot")) 	{
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.snapshot_dialog);
			((Button)dialog.findViewById(R.id.button_save)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					new MachineTask("TaskSnapshotTask", MachineActivity.this, _vmgr, "Taking Snapshot", false) {	
						protected IProgress workWithProgress(IMachine m, IConsole console) throws Exception { 	
							return console.takeSnapshot( ((TextView)dialog.findViewById(R.id.snapshot_name)).getText().toString(),  ((TextView)dialog.findViewById(R.id.snapshot_description)).getText().toString()); 
						}
					}.execute(_machine);							
				}
			});
			((Button)dialog.findViewById(R.id.button_cancel)).setOnClickListener(new View.OnClickListener() { public void onClick(View v) { dialog.dismiss(); } });
			dialog.show();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateState();
//		_thread.start();
//		_thread.addListener(_messenger);
		bindService(new Intent(this, EventService.class), localConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onPause() {
		if(eventService!=null) eventService.setMessenger(null);
		unbindService(localConnection);
//		boolean done = false;
//        _thread._running= false;
//        while (!done) {
////            try {
//                _thread.join();
//                done = true;
//            } catch (InterruptedException e) { }
//        }
		try {
			if(_vmgr.getVBox().getSessionObject().getState().equals(SessionState.LOCKED)) 
				_vmgr.getVBox().getSessionObject().unlockMachine();
		} catch (IOException e) {
			Log.e(TAG, "Exception unlocking machine", e);
		}
		super.onPause();
	}
	
	private void updateState() {
		_machine.clearCache();
		_headerView.update(_machine);
		_listView.setAdapter(new MachineActionAdapter(this, getApp().getActions(_machine.getState())));
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
		menu.findItem(R.id.machine_option_menu_metrics).setEnabled(_machine.getState().equals(MachineState.RUNNING));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.machine_option_menu_refresh:
			updateState();
			return true;
		case R.id.machine_option_menu_info:
			startActivity(new Intent(this, MachineInfoActivity.class).putExtra("vmgr", _vmgr).putExtra("machine", _machine.getIdRef()));
			return true;
		case R.id.machine_option_menu_snapshots:
			startActivity(new Intent(this, SnapshotActivity.class).putExtra("vmgr", _vmgr).putExtra("machine", _machine.getIdRef()));
			return true;
		case R.id.machine_option_menu_metrics:
			startActivity(new Intent(this, MetricActivity.class).putExtra("vmgr", _vmgr).putExtra("title", _machine.getName() + " Metrics")
				.putExtra(MetricActivity.INTENT_RAM_AVAILABLE, _machine.getMemorySize())
				.putExtra(MetricActivity.INTENT_OBJECT, _machine.getIdRef() )
				.putExtra(MetricActivity.INTENT_CPU_METRICS , new String[] { "Guest/CPU/Load/User", "Guest/CPU/Load/Kernel" } )
				.putExtra(MetricActivity.INTENT_RAM_METRICS , new String[] {  "Guest/RAM/Usage/Shared", "Guest/RAM/Usage/Cache" } ) );
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
				((ImageView)view.findViewById(R.id.action_item_icon)).setImageResource( getApp().get(getItem(position)));
			}
			return view;
		}
	}
}
