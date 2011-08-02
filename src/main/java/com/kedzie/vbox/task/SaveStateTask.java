package com.kedzie.vbox.task;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.MachineProgressTask;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.WebSessionManager;

public class SaveStateTask extends MachineProgressTask {

		public SaveStateTask(BaseListActivity activity, WebSessionManager vmgr) {
			super(activity, vmgr, "Saving State");
		}
		@Override
		protected IProgress work(IMachine m, WebSessionManager vmgr, IConsole console)	throws Exception{
				return console.saveState();
		}
}
