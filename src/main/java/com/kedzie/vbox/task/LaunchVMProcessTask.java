package com.kedzie.vbox.task;

import android.os.AsyncTask;
import android.util.Log;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.WebSessionManager;

public class LaunchVMProcessTask extends AsyncTask<IMachine, Void, String> {
	private final static String TAG = LaunchVMProcessTask.class.getName();
	
	private BaseListActivity activity;
	private WebSessionManager vmgr;

	public LaunchVMProcessTask(BaseListActivity activity, WebSessionManager vmgr) {
		this.activity = activity;
		this.vmgr = vmgr;
	}
	
	@Override
	protected void onPreExecute()		{
		activity.showProgress("Launching Machine");
	}

	@Override
	protected String doInBackground(IMachine... params)	{
		try	{
			ISession session = vmgr.getSession();
			IProgress p = vmgr.launchVMInstance(params[0], session, "headless");
			p.waitForCompletion();
			session.unlockMachine();
			return params[0].getState();
		} catch(Exception e)	{
			Log.e(TAG, e.getMessage(), e);
		}
		return "";
	}

	@Override
	protected void onPostExecute(String result)	{
		activity.dismissProgress();
	}
}