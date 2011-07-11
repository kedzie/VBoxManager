package com.kedzie.vbox;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.KSOAPTransport;
import com.kedzie.vbox.api.WebSessionManager;


public class MachineActivity extends BaseActivity {
	protected static final String TAG = MachineActivity.class.getSimpleName();
	
	KSOAPTransport transport;
	WebSessionManager vmgr = new WebSessionManager();
	private ISession session;
	
	private ListView listView; 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.machine);
        listView = (ListView)findViewById(R.id.machine_name);
    }
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
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