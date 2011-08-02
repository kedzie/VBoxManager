package com.kedzie.vbox.task;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.MachineTask;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.WebSessionManager;

public class DiscardStateTask extends MachineTask {

		public DiscardStateTask(BaseListActivity activity, WebSessionManager vmgr) {
			super(activity, vmgr, "Discarding State");
		}
		
		@Override
		protected void work(IMachine m, WebSessionManager vmgr, IConsole console)	throws Exception{
				console.discardSavedState(true);
		}
}
