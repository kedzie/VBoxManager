package com.kedzie.vbox.task;

import org.virtualbox_4_0.MachineState;
import org.virtualbox_4_0.SessionState;

import android.os.AsyncTask;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.WebSessionManager;

public class LaunchVMProcessTask extends AsyncTask<IMachine, Void, MachineState> {
	
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
	protected MachineState doInBackground(IMachine... params)	{
		try	{
			ISession session = vmgr.getSession();
			SessionState state = session.getState();
			if(state.equals(SessionState.Locked))
				session.unlockMachine();
			IProgress p = params[0].launchVMInstance(session, "headless");
			p.waitForCompletion(5000);
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