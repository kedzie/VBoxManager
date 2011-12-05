package com.kedzie.vbox.task;

import android.content.Context;

import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.common.PreferencesActivity;

public class LaunchVMProcessTask extends BaseTask<IMachine, IMachine> {
	
	public LaunchVMProcessTask(Context activity, VBoxSvc vmgr) {
		super(LaunchVMProcessTask.class.getSimpleName(), activity, vmgr, "Launching Machine");
	}
	
	@Override 
	protected IMachine work(IMachine... m) throws Exception {
		handleProgress( m[0].launchVMProcess(_vmgr.getVBox().getSessionObject(), IMachine.LaunchMode.headless) );
		_vmgr.getVBox().getPerformanceCollector().setupMetrics(new String[] { "*:" },  
				context.getSharedPreferences(context.getPackageName(), 0).getInt(PreferencesActivity.PERIOD, PreferencesActivity.PERIOD_DEFAULT), 1, m[0]);
		return m[0];
	}
}