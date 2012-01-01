package com.kedzie.vbox.machine;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.VBoxApplication.BundleBuilder;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMachineStateChangedEvent;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.jaxb.SessionState;
import com.kedzie.vbox.task.BaseTask;
import com.kedzie.vbox.task.LaunchVMProcessTask;
import com.kedzie.vbox.task.MachineTask;

public class MachineActivity extends Activity  implements AdapterView.OnItemClickListener {
	protected static final String TAG = MachineActivity.class.getSimpleName();
	
	private VBoxSvc _vmgr;
	private IMachine _machine;
	private MachineView _headerView;
	private ListView _listView;
	private EventThread _thread;
	private Messenger _messenger = new Messenger( new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case EventThread.WHAT_EVENT:
				IEvent event = BundleBuilder.getProxy(msg.getData(), EventThread.BUNDLE_EVENT, IEvent.class);
				if(event instanceof IMachineStateChangedEvent) {
					Toast.makeText(MachineActivity.this, _machine.getName() + "  changed State: " + _machine.getState(), Toast.LENGTH_LONG).show();
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
			new MachineTask<IMachine>("LaunchVMProcessTask", this, _vmgr, "Powering Off", false, _machine) { 
				protected IProgress workWithProgress(IMachine m, IConsole console, IMachine...i) throws Exception { 	
					return console.powerDown(); 
				}
			}.execute(_machine);
		else if(action.equals("Reset"))
			new MachineTask<IMachine>("ResetTask", this, _vmgr, "Resetting", true, _machine) { 
				protected void work(IMachine m, IConsole console, IMachine...i) throws Exception { 	
					console.reset(); 
				}
			}.execute(_machine);
		else if(action.equals("Pause")) 	
			new MachineTask<IMachine>("PauseTask", this, _vmgr, "Pausing", true, _machine) { 
				protected void work(IMachine m, IConsole console, IMachine...i) throws Exception { 
					console.pause();
				}
			}.execute(_machine);
		else if(action.equals("Resume")) 
			new MachineTask<IMachine>("ResumeTask", this, _vmgr, "Resuming", true, _machine) { 
				protected void work(IMachine m, IConsole console, IMachine...i) throws Exception { 	
					console.resume(); 
				}
			}.execute(_machine);
		else if(action.equals("Power Button")) 	
			new MachineTask<IMachine>("PowerButtonTask", this, _vmgr, "ACPI Power Down", true, _machine) { 
				protected void work(IMachine m, IConsole console, IMachine...i) throws Exception { 
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.machine_option_menu_refresh:
			updateState();
			return true;
		case R.id.machine_option_menu_preferences:
			startActivity(new Intent(this, PreferencesActivity.class));
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
			((ImageView)view.findViewById(R.id.action_item_icon)).setImageResource( getApp().get(getItem(position)));
			return view;
		}
	}
}
