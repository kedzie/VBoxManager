package com.kedzie.vbox.task;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.WebSessionManager;

public class PauseTask extends MachineTask {

		public PauseTask(BaseListActivity activity, WebSessionManager vmgr) {
			super(activity, vmgr, "Pausing", true);
		}
		
		@Override
		protected IProgress work(IMachine m, WebSessionManager vmgr, IConsole console)	throws Exception{
				console.discardSavedState(true);
				return null;
		}
}
