package com.kedzie.vbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.serialization.SoapPrimitive;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.KSOAPTransport;
import com.kedzie.vbox.api.WebSessionManager;


public class MachineListActivity extends BaseActivity {
	protected static final String TAG = MachineListActivity.class.getSimpleName();
	public static final String NAMESPACE = "http://www.virtualbox.org/";
	
	private String url;
	KSOAPTransport transport;
	WebSessionManager vmgr = new WebSessionManager();
	private ISession session;
	private MachinesListAdapter listAdapter;
	private ListView listView; 
	
	@Override
    	public void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);
        	setContentView(R.layout.machine_list);
        	listView = (ListView)findViewById(R.id.machines_list);
        	registerForContextMenu(listView);
    	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			vmgr.logoff();
		} catch (Exception e) {
			Log.e(TAG, "error ", e);
		} 
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.machines_list_context_menu, menu);
		IMachine m = ((MachinesListAdapter)listView.getAdapter()).getItem(((AdapterContextMenuInfo)menuInfo).position);
		
		MenuItem poweroffItem = menu.findItem(R.id.machines_context_menu_poweroff);
		MenuItem startItem = menu.findItem(R.id.machines_context_menu_start);
		String state = m.getState();
		boolean poweroff=false, start=false, pause=false, resume=false, reset=false;

		if(state.equals("PoweredOff") || state.equals("Aborted")) {
			start=true;
		} else if(state.equals("Running")) {
			reset=pause=poweroff=true;
		}
		poweroffItem.setEnabled(poweroff);
		startItem.setEnabled(start);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  IMachine m = ((MachinesListAdapter)listView.getAdapter()).getItem( ((AdapterContextMenuInfo) item.getMenuInfo()).position);
	  switch (item.getItemId()) {
	  case R.id.machines_context_menu_start:
	    new LaunchVMProcessTask().execute(m);
	    return true;
	  case R.id.machines_context_menu_poweroff:
		  new PowerDownTask().execute(m);
	    return true;
	  default:
	    return super.onContextItemSelected(item);
	  }
	}
	
	private class LogonTask extends AsyncTask<String, Void, String>	{
		@Override
		protected void onPreExecute()		{ 
			showProgress("Connecting to" + url);
		}

		@Override
		protected String doInBackground(String... params)	{
			try	{
				vmgr.logon(transport, params[0], params[1]);
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
		protected void onPreExecute()		{
			showProgress("Loading Machines");
		}

		@Override
		protected List<IMachine> doInBackground(Void... params)	{
			List<IMachine> machines = new ArrayList<IMachine>();
			try	{
				Object ret = vmgr.getVBox().getMachines();
				for(SoapPrimitive p : (Vector<SoapPrimitive>)ret)
					machines.add(transport.getProxy(IMachine.class, p.toString()));
			} catch(Exception e)	{
				Log.e(TAG, e.getMessage(), e);
			}
			return machines;
		}

		@Override
		protected void onPostExecute(List<IMachine> result)	{
			listView.setAdapter(new MachinesListAdapter(MachineListActivity.this, result));
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					listAdapter = (MachinesListAdapter)listView.getAdapter();
					IMachine m = listAdapter.getItem(position);
					Log.i(TAG, "machine clicked: " + m.getName());
				}
			});
			dismissProgress();
		}
	}
	
	private class LaunchVMProcessTask extends AsyncTask<IMachine, Void, String> {
		@Override
		protected void onPreExecute()		{
			showProgress("Launching Machine");
		}

		@Override
		protected String doInBackground(IMachine... params)	{
			try	{
				ISession session = vmgr.getSession();
				IProgress p = vmgr.launchVMInstance(params[0], session, "headless");
				p.waitForCompletion();
				session.unlockMachine();
			} catch(Exception e)	{
				Log.e(TAG, e.getMessage(), e);
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result)	{
			listAdapter.notifyDataSetChanged();
			dismissProgress();
		}
	}
	
	
	private class PowerDownTask extends AsyncTask<IMachine, Void, String> {
		@Override
		protected void onPreExecute()		{
			showProgress("Powering Down");
		}
		@Override
		protected String doInBackground(IMachine... params)	{
			try	{
				ISession session = vmgr.getSession();
				vmgr.lockMachine(params[0], session, "Shared");
				IProgress p = session.getConsole().powerDown();
				p.waitForCompletion();
				session.unlockMachine();
				return "ret";
			} catch(Exception e)	{
				Log.e(TAG, e.getMessage(), e);
			}
			return "ret";
		}
		@Override
		protected void onPostExecute(String result)	{
			listAdapter.notifyDataSetChanged();
			dismissProgress();
		}
	}
	
}
