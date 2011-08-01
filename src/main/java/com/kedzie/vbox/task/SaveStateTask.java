package com.kedzie.vbox.task;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.WebSessionManager;

public class SaveStateTask extends MachineTask {

		public SaveStateTask(BaseListActivity activity, WebSessionManager vmgr) {
			super(activity, vmgr, "Saving State", false);
		}
		@Override
		protected IProgress work(IMachine m, WebSessionManager vmgr, IConsole console)	throws Exception{
				return console.saveState();
		}
}
