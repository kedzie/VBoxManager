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


public class VBoxMonitorActivity extends Activity {
	protected static final String TAG = VBoxMonitorActivity.class.getSimpleName();
	private static final int REQUEST_CODE_PREFERENCES = 1;
	public static final String NAMESPACE = "http://www.virtualbox.org/";
	
	private String url;
	KSOAPTransport transport;
	WebSessionManager vmgr = new WebSessionManager();
	private ISession session;
	
	private ListView listView; 
	private ProgressDialog pDialog;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.options_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.preferences:
	    	Intent launchPreferencesIntent = new Intent().setClass(this, PreferencesActivity.class);
	        startActivityForResult(launchPreferencesIntent, REQUEST_CODE_PREFERENCES);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PREFERENCES) {
            url=PreferenceManager.getDefaultSharedPreferences(this).getString("url", "http://192.168.1.10:18083");
            transport = new KSOAPTransport(url);
            new LogonTask().execute("","");
        }
    }
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.context_menu, menu);
		IMachine m = ((MachinesListAdapter)listView.getAdapter()).getItem(((AdapterContextMenuInfo)menuInfo).position);
		
		MenuItem poweroffItem = menu.findItem(R.id.poweroff);
		MenuItem startItem = menu.findItem(R.id.start);
		String state = m.getState();
		boolean poweroff=false, start=false, pause=false, resume=false, reset=false;

		if(state.equals("PoweredOff") || state.equals("Aborted")) {
			start=true;
		} else if(state.equals("Running")) {
			reset=pause=poweroff=true;
		}
		poweroffItem.setEnabled(poweroff);
		poweroffItem.setIcon(poweroff ? R.drawable.vm_poweroff : R.drawable.vm_poweroff_disabled);
		startItem.setEnabled(start);
		startItem.setIcon(start ? R.drawable.vm_start_32px : R.drawable.vm_start_disabled_32px);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  IMachine m = ((MachinesListAdapter)listView.getAdapter()).getItem( ((AdapterContextMenuInfo) item.getMenuInfo()).position);
	  switch (item.getItemId()) {
	  case R.id.start:
	    new LaunchVMProcessTask().execute(m);
	    return true;
	  case R.id.poweroff:
		  new PowerDownTask().execute(m);
	    return true;
	  default:
	    return super.onContextItemSelected(item);
	  }
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
	}

	public void showProgress(String msg) {
		pDialog = new ProgressDialog(VBoxMonitorActivity.this);
		pDialog.setIndeterminate(true);
		pDialog.setMessage(msg);
		pDialog.show();
	}
	
	public void dismissProgress() {
		this.pDialog.dismiss();
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
			listView.setAdapter(new MachinesListAdapter(VBoxMonitorActivity.this, result));
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					IMachine m = ((MachinesListAdapter)listView.getAdapter()).getItem(position);
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
				session = vmgr.getSession();
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
			((MachinesListAdapter)listView.getAdapter()).notifyDataSetChanged();
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
				session = vmgr.getSession();
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
			((MachinesListAdapter)listView.getAdapter()).notifyDataSetChanged();
			dismissProgress();
		}
	}
	
}