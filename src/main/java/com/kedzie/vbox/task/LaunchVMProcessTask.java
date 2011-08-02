package com.kedzie.vbox.task;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.MachineProgressTask;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.WebSessionManager;

public class LaunchVMProcessTask extends MachineProgressTask {
	
	public LaunchVMProcessTask(BaseListActivity activity, WebSessionManager vmgr) {
		super(activity, vmgr, "Launching Machine");
	}
	
	@Override
	protected IProgress work(IMachine m, WebSessionManager vmgr, IConsole console)	throws Exception{
			IProgress p= m.launchVMInstance(vmgr.getVBox().getSessionObject(), "headless");
			while(p.getCompleted()) {
				publishProgress(p);
				Thread.sleep(500);
			}
			vmgr.setupMetrics( context,  m.getId());
			return null;
	}
}