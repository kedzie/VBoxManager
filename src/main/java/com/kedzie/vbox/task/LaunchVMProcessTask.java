package com.kedzie.vbox.task;

import android.content.Context;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IMachine;

public class LaunchVMProcessTask extends BaseTask<IMachine, IMachine> {
	
	public LaunchVMProcessTask(Context activity, VBoxSvc vmgr) {
		super(activity, vmgr, "Launching Machine", false);
	}
	
//	@Override
//	protected IProgress workWithProgress(IMachine m, VBoxSvc vmgr, IConsole console)	throws Exception {
//			_vmgr.getVBox().getSessionObject().clearCache();
//			if( _vmgr.getVBox().getSessionObject().getState().equals(SessionState.Locked)) _vmgr.getVBox().getSessionObject().unlockMachine();
//	}

	@Override protected IMachine work(IMachine... params) throws Exception {
		handleProgress( params[0].launchVMProcess(_vmgr.getVBox().getSessionObject(), IMachine.LaunchMode.headless.toString()) );
		_vmgr.setupMetrics( context,  params[0].getIdRef(), "*:");
		return null;
	}
}