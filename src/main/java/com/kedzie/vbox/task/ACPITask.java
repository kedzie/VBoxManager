package com.kedzie.vbox.task;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.WebSessionManager;

public class ACPITask extends MachineTask {
	
		public ACPITask(BaseListActivity activity, WebSessionManager vmgr) {
			super(activity, vmgr, "ACPI Shutdown", true);
		}
		
		@Override
		protected IProgress work(IMachine m, WebSessionManager vmgr, IConsole console)	throws Exception{
				console.powerButton();
				return null;
		}
}
