package com.kedzie.vbox.task;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.WebSessionManager;

public class TakeSnapshotTask extends MachineTask {
		public TakeSnapshotTask(BaseListActivity activity, WebSessionManager vmgr) {
			super(activity, vmgr, "Taking Snapshot", false);
		}
		
		@Override
		protected IProgress work(IMachine m, WebSessionManager vmgr, IConsole console)	throws Exception{
				return console.takeSnapshot("New Snapshot", "Description");
		}		
}
