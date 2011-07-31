package com.kedzie.vbox.task;

import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.SessionState;

import com.kedzie.vbox.BaseListActivity;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.WebSessionManager;

public class ACPITask extends MachineTask {
	
		public ACPITask(BaseListActivity activity, WebSessionManager vmgr) {
			super(activity, vmgr, "ACPI Shutdown");
		}
		
		@Override
		protected IMachine work(IMachine m, WebSessionManager vmgr)	throws Exception{
				SessionState state = vmgr.getSession().getState();
				if(state.equals(SessionState.Unlocked))  m.lockMachine( vmgr.getSession(), LockType.Shared);
				vmgr.getSession().getConsole().powerButton();
				return m;
		}
}
