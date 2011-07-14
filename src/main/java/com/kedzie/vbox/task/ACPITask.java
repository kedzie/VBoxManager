package com.kedzie.vbox.task;

import android.os.AsyncTask;
import android.util.Log;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.WebSessionManager;

public class ACPITask extends AsyncTask<IMachine, Void, String> {
		private final static String TAG = ACPITask.class.getName();
		
		private BaseListActivity activity;
		private WebSessionManager vmgr;

		public ACPITask(BaseListActivity activity, WebSessionManager vmgr) {
			this.activity = activity;
			this.vmgr = vmgr;
		}
		
		@Override
		protected void onPreExecute()		{
			activity.showProgress("ACPI Shutdown");
		}
		
		@Override
		protected String doInBackground(IMachine... params)	{
			try	{
				ISession session = vmgr.getSession();
				vmgr.lockMachine(params[0], session, "Shared");
				session.getConsole().powerButton();
				session.unlockMachine();
				return params[0].getState();
			} catch(Exception e)	{
				Log.e(TAG, e.getMessage(), e);
			}
			return "ret";
		}
		
		@Override
		protected void onPostExecute(String result)	{
			activity.dismissProgress();
		}
}
