package com.kedzie.vbox.task;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.MachineProgressTask;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.WebSessionManager;

public class PowerDownTask extends MachineProgressTask {

		public PowerDownTask(BaseListActivity activity, WebSessionManager vmgr) {
			super(activity, vmgr, "Powering Down");
		}
		
		@Override
		protected IProgress work(IMachine m, WebSessionManager vmgr, IConsole console)	throws Exception{
				return console.powerDown();
		}
}
