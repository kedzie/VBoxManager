package com.kedzie.vbox;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.KSOAPTransport;
import com.kedzie.vbox.api.WebSessionManager;


public class MachineActivity extends Activity {
	protected static final String TAG = MachineActivity.class.getSimpleName();
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
        setContentView(R.layout.machine);
        listView = (ListView)findViewById(R.id.machines_list);
    }
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.context_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.start:
	        return true;
	    case R.id.poweroff:
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
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
		pDialog = new ProgressDialog(MachineActivity.this);
		pDialog.setIndeterminate(true);
		pDialog.setMessage(msg);
		pDialog.show();
	}
	
	public void dismissProgress() {
		this.pDialog.dismiss();
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