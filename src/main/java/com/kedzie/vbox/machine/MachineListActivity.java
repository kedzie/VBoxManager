package com.kedzie.vbox.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.serialization.SoapPrimitive;

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
	protected static final String TAG = MachineListActivity.class.getName();
	public static final String NAMESPACE = "http://www.virtualbox.org/";
	
	private WebSessionManager vmgr = new WebSessionManager();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
       	super.onCreate(savedInstanceState);
       	Log.i(TAG, "onCreate");
       	registerForContextMenu(getListView());
       	new LogonTask().execute(getIntent().getStringExtra("url"),"","");
    }
	
	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		try { vmgr.logoff(); } catch (Exception e) { Log.e(TAG, "error ", e); } 
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		Log.i(TAG, "onStart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "onStop");
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
		String state = m.getState();
		boolean poweroff=false, start=false, pause=false, resume=false, reset=false,acpi=false;
		if(state.equals("PoweredOff") || state.equals("Aborted")) {
			start=true;
		} else if(state.equals("Running")) {
			reset=poweroff=pause=acpi=true;
		} else if(state.equals("Paused")) {
			reset=poweroff=resume=acpi=true;
		}
		menu.findItem(R.id.machines_context_menu_poweroff).setEnabled(poweroff);
		menu.findItem(R.id.machines_context_menu_start).setEnabled(start);
		menu.findItem(R.id.machines_context_menu_reset).setEnabled(reset);
		menu.findItem(R.id.machines_context_menu_resume).setEnabled(resume);
		menu.findItem(R.id.machines_context_menu_pause).setEnabled(pause);
		menu.findItem(R.id.machines_context_menu_acpi).setEnabled(acpi);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  final IMachine m = (IMachine)getListAdapter().getItem( ((AdapterContextMenuInfo) item.getMenuInfo()).position);
	  switch (item.getItemId()) {
	  case R.id.machines_context_menu_start:
		  new LaunchVMProcessTask(this, vmgr).execute(m);
		  break;
	  case R.id.machines_context_menu_poweroff:
		  new PowerDownTask(this, vmgr).execute(m);
		  break;
	  case R.id.machines_context_menu_reset:
		  new ResetTask(this, vmgr).execute(m);
		  break;
	  case R.id.machines_context_menu_pause:
		  new PauseTask(this, vmgr).execute(m);
		  break;
	  case R.id.machines_context_menu_resume:
		  new ResumeTask(this, vmgr).execute(m);
		  break;
	  case R.id.machines_context_menu_acpi:
		  new ACPITask(this, vmgr).execute(m);
		  break;
	  }
	  updateState(m, m.getState());
	  return true;
	}
	
	private void updateState(IMachine m, String state) {
		Log.i(TAG, "update state: " + state);
	}
	
	private class LogonTask extends AsyncTask<String, Void, String>	{
		@Override
		protected void onPreExecute()		{  showProgress("Connecting"); }

		@Override
		protected String doInBackground(String... params)	{
			try	{
				vmgr.logon(params[0], params[1], params[2]);
				return vmgr.getVBox().getVersion();
			} catch(Exception e)	{
				Log.e(TAG, e.getMessage(), e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result)	{
			Log.i(TAG, "Version: " + result);
			dismissProgress();
			new LoadMachinesTask().execute();
		}
	}
	
	private class LoadMachinesTask extends AsyncTask<Void, Void, List<IMachine>>	{
		@Override
		protected void onPreExecute() { showProgress("Loading Machines"); }

		@Override
		@SuppressWarnings("unchecked")
		protected List<IMachine> doInBackground(Void... params)	{
			List<IMachine> machines = new ArrayList<IMachine>();
			try	{
				Object ret = vmgr.getVBox().getMachines();
				for(SoapPrimitive p : (Vector<SoapPrimitive>)ret)
					machines.add(vmgr.getTransport().getProxy(IMachine.class, p.toString()));
			} catch(Exception e)	{
				Log.e(TAG, e.getMessage(), e);
			}
			return machines;
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
