package com.kedzie.vbox.task;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.jaxb.SessionState;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.SettingsActivity;
import com.kedzie.vbox.soap.VBoxSvc;

public class LaunchVMProcessTask extends DialogTask<IMachine, IMachine> {
	
	public LaunchVMProcessTask(SherlockFragmentActivity activity, VBoxSvc vmgr) {
		super(activity, vmgr, R.string.progress_starting);
	}
	
	@Override 
	protected IMachine work(IMachine... m) throws Exception {
		if(!m[0].getSessionState().equals(SessionState.UNLOCKED))
			throw new RuntimeException("Machine session state is " + m[0].getSessionState());
		handleProgress( m[0].launchVMProcess(_vmgr.getVBox().getSessionObject(), IMachine.LaunchMode.headless) );
		_vmgr.getVBox().getPerformanceCollector().setupMetrics(new String[] { "*:" },  
				Utils.getIntPreference(getContext(), SettingsActivity.PREF_PERIOD), 
				Utils.getIntPreference(getContext(), SettingsActivity.PREF_COUNT), m[0]);
		return m[0];
	}
}