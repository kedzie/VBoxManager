package com.kedzie.vbox.task;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.WebSessionManager;

public class LaunchVMProcessTask extends MachineTask {
	
	public LaunchVMProcessTask(BaseListActivity activity, WebSessionManager vmgr) {
		super(activity, vmgr, "Launching Machine", false);
	}
	
	@Override
	protected IProgress work(IMachine m, WebSessionManager vmgr, IConsole console)	throws Exception{
			return m.launchVMInstance(vmgr.getSession(), "headless");
	}
}