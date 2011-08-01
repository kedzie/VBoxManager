package com.kedzie.vbox.machine;

import org.virtualbox_4_1.MachineState;
import org.virtualbox_4_1.SessionState;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.ISnapshot;
import com.kedzie.vbox.api.WebSessionManager;
import com.kedzie.vbox.task.ACPITask;
import com.kedzie.vbox.task.DiscardStateTask;
import com.kedzie.vbox.task.LaunchVMProcessTask;
import com.kedzie.vbox.task.PauseTask;
import com.kedzie.vbox.task.PowerDownTask;
import com.kedzie.vbox.task.ResetTask;
import com.kedzie.vbox.task.ResumeTask;
import com.kedzie.vbox.task.SaveStateTask;
import com.kedzie.vbox.task.TakeSnapshotTask;

public class MachineActivity extends BaseListActivity {
	protected static final String TAG = MachineActivity.class.getSimpleName();
	
	private WebSessionManager _vmgr;
	private IMachine _machine;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _vmgr =getIntent().getParcelableExtra("vmgr");
		_machine = _vmgr.getTransport().getProxy(IMachine.class, getIntent().getStringExtra("machine"));
		View _headerView = getLayoutInflater().inflate(R.layout.machine_list_item, getListView(), false);
		((ImageView)_headerView.findViewById(R.id.machine_list_item_ostype)).setImageResource(VBoxApplication.get("os_"+_machine.getOSTypeId().toLowerCase()));
		((TextView) _headerView.findViewById(R.id.machine_list_item_name)).setText(_machine.getName()); 
		ISnapshot s = _machine.getCurrentSnapshot();
		if(s!=null)  ((TextView) _headerView.findViewById(R.id.machine_list_item_snapshot)).setText("("+s.getName() + ")");		
		getListView().addHeaderView(_headerView);
    }
	
	@Override
    public void onDestroy() {
        super.onDestroy();
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		updateState(_machine.getState());
	}
	
	@Override
	protected void onStop() {
		try {
			if(_vmgr.getSession().getState().equals(SessionState.Locked))
				_vmgr.getSession().unlockMachine();
		} catch (Exception e) {
			Log.e(TAG, "Error unlock session", e);
			showAlert(e.toString());
		}
		super.onStop();
	}
	
	private void updateState(MachineState state) {
		Log.i(TAG, "Update state: " + state);
		((ImageView)getListView().findViewById(R.id.machine_list_item_state)).setImageResource( VBoxApplication.get(state) );
		((TextView)getListView().findViewById(R.id.machine_list_item_state_text)).setText(state.name());
		setListAdapter(new MachineActionAdapter(this, R.layout.machine_action_item, R.id.action_item_text, R.id.action_item_icon, VBoxApplication.getActions(state)));
		
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				String action = (String)getListView().getAdapter().getItem(position);
				if(action.equals("Start")) {
					new LaunchVMProcessTask(MachineActivity.this, _vmgr).execute(_machine);
				} else if(action.equals("Power Off")) {
					new PowerDownTask(MachineActivity.this, _vmgr).execute(_machine);
				} else if(action.equals("Reset")) {
					new ResetTask(MachineActivity.this, _vmgr).execute(_machine);
				}else if(action.equals("Pause")) {
					new PauseTask(MachineActivity.this, _vmgr).execute(_machine);
				}else if(action.equals("Resume")) {
					new ResumeTask(MachineActivity.this, _vmgr) .execute(_machine);
				} else if(action.equals("Power Button")) {
					new ACPITask(MachineActivity.this, _vmgr) .execute(_machine);
				}  else if(action.equals("Save State")) {
					new SaveStateTask(MachineActivity.this, _vmgr) .execute(_machine);
				}  else if(action.equals("Discard State")) {
					new DiscardStateTask(MachineActivity.this, _vmgr) .execute(_machine);
				}else if(action.equals("Take Snapshot")) {
					new TakeSnapshotTask(MachineActivity.this, _vmgr).execute(_machine);
				}
			}
		});
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
			updateState(_machine.getState());
			return true;
		default:
			return true;
		}
	}
}