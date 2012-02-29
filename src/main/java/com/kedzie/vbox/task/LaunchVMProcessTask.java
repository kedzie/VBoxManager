package com.kedzie.vbox.task;

import android.content.Context;
import android.preference.PreferenceManager;

import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.jaxb.SessionState;
import com.kedzie.vbox.machine.PreferencesActivity;

public class LaunchVMProcessTask extends BaseTask<IMachine, IMachine> {
	
	public LaunchVMProcessTask(Context activity, VBoxSvc vmgr) {
		super(LaunchVMProcessTask.class.getSimpleName(), activity, vmgr, "Launching Machine");
	}
	
	@Override 
	protected IMachine work(IMachine... m) throws Exception {
		if(!m[0].getSessionState().equals(SessionState.UNLOCKED))
			throw new RuntimeException("Machine session state is " + m[0].getSessionState());
		handleProgress( m[0].launchVMProcess(_vmgr.getVBox().getSessionObject(), IMachine.LaunchMode.headless) );
		_vmgr.getVBox().getPerformanceCollector().setupMetrics(new String[] { "*:" },  
				Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(PreferencesActivity.PERIOD, "")), 1, m[0]);
		return m[0];
	}
}