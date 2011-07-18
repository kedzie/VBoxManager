package com.kedzie.vbox.task;

import org.virtualbox_4_0.LockType;
import org.virtualbox_4_0.MachineState;
import org.virtualbox_4_0.SessionState;

import android.os.AsyncTask;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.WebSessionManager;

public class DiscardStateTask extends AsyncTask<IMachine, Void, MachineState> {
		private BaseListActivity activity;
		private WebSessionManager vmgr;

		public DiscardStateTask(BaseListActivity activity, WebSessionManager vmgr) {
			this.activity = activity;
			this.vmgr = vmgr;
		}
		
		@Override
		protected void onPreExecute()		{
			activity.showProgress("Discarding state");
		}
		
		@Override
		protected MachineState doInBackground(IMachine... params)	{
			try	{
				ISession session = vmgr.getSession();
				SessionState state = session.getState();
				if(state.equals(SessionState.Unlocked))
					params[0].lockMachine(session, LockType.Shared);
				session.getConsole().discardSavedState(true);
			} catch(Exception e)	{
				activity.showAlert(e.getMessage());
			}
			return params[0].getState();
		}
		
		@Override
		protected void onPostExecute(MachineState result)	{
			activity.dismissProgress();
		}
}
