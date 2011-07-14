package com.kedzie.vbox.machine;

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
import com.kedzie.vbox.Resources;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.WebSessionManager;
import com.kedzie.vbox.task.ACPITask;
import com.kedzie.vbox.task.LaunchVMProcessTask;
import com.kedzie.vbox.task.PauseTask;
import com.kedzie.vbox.task.PowerDownTask;
import com.kedzie.vbox.task.ResetTask;
import com.kedzie.vbox.task.ResumeTask;

public class MachineActivity extends BaseListActivity {
	protected static final String TAG = MachineActivity.class.getSimpleName();
	
	private WebSessionManager _vmgr;
	private IMachine _machine;
	private View _headerView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		_vmgr = new WebSessionManager(getIntent().getStringExtra("url"), getIntent().getStringExtra("vbox"));
		_machine = _vmgr.getTransport().getProxy(IMachine.class, getIntent().getStringExtra("machine"));
		_headerView = getLayoutInflater().inflate(R.layout.machine_list_item, getListView(), false);
		((ImageView)_headerView.findViewById(R.id.machine_list_item_ostype)).setImageResource(Resources.get("os_"+_machine.getOSTypeId().toLowerCase()));
		String state = _machine.getState();
		((TextView) _headerView.findViewById(R.id.machine_list_item_name)).setText(_machine.getName()); 
		getListView().addHeaderView(_headerView);
		updateState(state);
	}
	
	private void updateState(String state) {
		Log.i(TAG, "Update state: " + state);
		((ImageView)getListView().findViewById(R.id.machine_list_item_state)).setImageResource( Resources.get("state_"+state.toLowerCase()) );
		((TextView)getListView().findViewById(R.id.machine_list_item_state_text)).setText(state);
		String []actions = new String[] { };
		int []icons = new int[] {  };
		
		if(state.equals("Running")) {
			actions = new String[] { "Pause", "Reset", "Power Off" };
			icons = new int[] { R.drawable.ic_list_pause, R.drawable.ic_list_reset, R.drawable.ic_list_poweroff };
		} else if (state.equals("PoweredOff") || state.equals("Aborted")){
			actions = new String[] { "Start"  };
			icons = new int[] { R.drawable.ic_list_start };
		} else if (state.equals("Paused")){
			actions = new String[] { "Resume", "Reset", "Power Off" };
			icons = new int[] { R.drawable.ic_list_start, R.drawable.ic_list_reset, R.drawable.ic_list_poweroff };
		}
		setListAdapter(new MachineActionAdapter(this, R.layout.machine_action_item, R.id.action_item_text, R.id.action_item_icon, actions, icons));
		
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				String action = (String)getListView().getAdapter().getItem(position);
				if(action.equals("Start")) {
					new LaunchVMProcessTask(MachineActivity.this, _vmgr) {
						@Override
						protected void onPostExecute(String result) {
							updateState(result);
							super.onPostExecute(result);
						}
					}.execute(_machine);
				} else if(action.equals("Power Off")) {
					new PowerDownTask(MachineActivity.this, _vmgr) {
						@Override
						protected void onPostExecute(String result) {
							updateState(result);
							super.onPostExecute(result);
						}
					}.execute(_machine);
				} else if(action.equals("Reset")) {
					new ResetTask(MachineActivity.this, _vmgr) {
						@Override
						protected void onPostExecute(String result) {
							updateState(result);
							super.onPostExecute(result);
						}
					}.execute(_machine);
				}else if(action.equals("Pause")) {
					new PauseTask(MachineActivity.this, _vmgr) {
						@Override
						protected void onPostExecute(String result) {
							updateState(result);
							super.onPostExecute(result);
						}
					}.execute(_machine);
				}else if(action.equals("Resume")) {
					new ResumeTask(MachineActivity.this, _vmgr) {
						@Override
						protected void onPostExecute(String result) {
							updateState(result);
							super.onPostExecute(result);
						}
					}.execute(_machine);
				} else if(action.equals("ACPI Power Down")) {
					new ACPITask(MachineActivity.this, _vmgr) {
						@Override
						protected void onPostExecute(String result) {
							updateState(result);
							super.onPostExecute(result);
						}
					}.execute(_machine);
				}
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
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