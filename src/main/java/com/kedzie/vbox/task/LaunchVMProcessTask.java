package com.kedzie.vbox.task;

import android.content.Context;
import android.content.SharedPreferences;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.server.PreferencesActivity;

public class LaunchVMProcessTask extends BaseTask<IMachine, IMachine> {
	
	public LaunchVMProcessTask(Context activity, VBoxSvc vmgr) {
		super(activity, vmgr, "Launching Machine", false);
	}
	
	@Override 
	protected IMachine work(IMachine... params) throws Exception {
		handleProgress( params[0].launchVMProcess(_vmgr.getVBox().getSessionObject(), IMachine.LaunchMode.headless) );
		SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), 0);
		_vmgr.getVBox().getPerformanceCollector().setupMetrics(
				VBoxSvc.METRICS_MACHINE,  
				new String[] { params[0].getIdRef() }, 
				prefs.getInt(PreferencesActivity.PERIOD, PreferencesActivity.PERIOD_DEFAULT), 
				prefs.getInt(PreferencesActivity.COUNT, PreferencesActivity.COUNT_DEFAULT));
		return null;
	}
}