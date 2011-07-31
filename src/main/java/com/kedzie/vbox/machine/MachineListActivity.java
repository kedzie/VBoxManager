package com.kedzie.vbox.machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.virtualbox_4_1.MachineState;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.WebSessionManager;
import com.kedzie.vbox.task.ACPITask;
import com.kedzie.vbox.task.LaunchVMProcessTask;
import com.kedzie.vbox.task.PauseTask;
import com.kedzie.vbox.task.PowerDownTask;
import com.kedzie.vbox.task.ResetTask;
import com.kedzie.vbox.task.ResumeTask;


public class MachineListActivity extends BaseListActivity {
	protected static final String TAG = "vbox."+MachineListActivity.class.getSimpleName();
	
	private WebSessionManager vmgr = new WebSessionManager();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
       	super.onCreate(savedInstanceState);
       	registerForContextMenu(getListView());
       	new LogonTask().execute(getIntent().getStringExtra("url"),getIntent().getStringExtra("username"), getIntent().getStringExtra("password"));
    }
	
	@Override
	protected void onDestroy() {
		try {  vmgr.logoff(); } catch (Exception e) { Log.e(TAG, "error ", e); } 
		super.onDestroy();
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
			new LoadMachinesTask().execute();
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
		List<String> actions = Arrays.asList(getVBoxApplication().getActions(m.getState()));
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
		  new LaunchVMProcessTask(this, vmgr) {
				@Override
				protected void onPostExecute(MachineState result) {
					updateState(result);
					super.onPostExecute(result);
				}
			}.execute(m);
		  break;
	  case R.id.machines_context_menu_poweroff:
		  new PowerDownTask(this, vmgr) {
				@Override
				protected void onPostExecute(MachineState result) {
					updateState(result);
					super.onPostExecute(result);
				}
			}.execute(m);
		  break;
	  case R.id.machines_context_menu_reset:
		  new ResetTask(this, vmgr) {
				@Override
				protected void onPostExecute(MachineState result) {
					updateState(result);
					super.onPostExecute(result);
				}
			}.execute(m);
		  break;
	  case R.id.machines_context_menu_pause:
		  new PauseTask(this, vmgr) {
				@Override
				protected void onPostExecute(MachineState result) {
					updateState(result);
					super.onPostExecute(result);
				}
		  }.execute(m);
		  break;
	  case R.id.machines_context_menu_resume:
		  new ResumeTask(this, vmgr) {
				@Override
				protected void onPostExecute(MachineState result) {
					updateState(result);
					super.onPostExecute(result);
				}
			}.execute(m);
		  break;
	  case R.id.machines_context_menu_acpi:
		  new ACPITask(this, vmgr) {
				@Override
				protected void onPostExecute(MachineState result) {
					updateState(result);
					super.onPostExecute(result);
				}
			}.execute(m);
		  break;
	  }
	  return true;
	}
	
	private void updateState(MachineState state) {
		new LoadMachinesTask().execute();
	}
	
	private class LogonTask extends AsyncTask<String, Object, String>	{
		
		private ProgressDialog pd;
		
		@Override
		protected void onPreExecute()		{
			pd = new ProgressDialog(MachineListActivity.this);
			pd.setTitle("Connecting");
			pd.setMessage("Logging in");
			pd.setMax(2);
			pd.setIndeterminate(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params)	{
			try	{
				vmgr.logon(params[0], params[1], params[2]);
				publishProgress("Getting Version", new Integer(1));
				String version = vmgr.getVBox().getVersion();
				publishProgress("Done", new Integer(2));
				return version;
			} catch(Exception e)	{
				showAlert(e);
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			pd.setMessage((String)values[0]);
			int progress = (Integer)values[1];
			pd.setProgress(progress);
		}

		@Override
		protected void onPostExecute(String version)	{
			pd.dismiss();
			if(version!=null) {
				Log.i(TAG, "Version: " + version);
				new LoadMachinesTask().execute();
			}
		}
	}
	
	private class LoadMachinesTask extends AsyncTask<Void, Void, List<IMachine>>	{
		@Override
		protected void onPreExecute() { showProgress("Loading Machines"); }

		@Override
		protected List<IMachine> doInBackground(Void... params)	{
			try	{
				return vmgr.getVBox().getMachines();
			} catch(Exception e)	{
				showAlert(e);
			}
			return new ArrayList<IMachine>();
		}

		@Override
		protected void onPostExecute(List<IMachine> result)	{
			setListAdapter(new MachineListAdapter(MachineListActivity.this, result));
			getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					IMachine m = (IMachine)getListView().getAdapter().getItem(position);
					Intent intent = new Intent().setClass(MachineListActivity.this, MachineActivity.class);
					intent.putExtra("vbox", vmgr.getVBox().getId());
					intent.putExtra("url", vmgr.getURL());
					intent.putExtra("machine", m.getId());
					startActivity(intent);
				}
			});
			dismissProgress();
		}
	}
}
