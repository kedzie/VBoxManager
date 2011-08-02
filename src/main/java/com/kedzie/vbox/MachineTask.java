package com.kedzie.vbox;

import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.SessionState;

import android.content.Context;

import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.WebSessionManager;

public abstract class MachineTask extends BaseTask<IMachine, IMachine> {

		public MachineTask(final Context ctx, WebSessionManager vmgr, String msg) {
			super(ctx, vmgr, msg, true);
		}
		
		@Override
		protected IMachine work(IMachine... params) throws Exception {
			if( _vmgr.getVBox().getSessionObject().getState().equals(SessionState.Unlocked))
				params[0].lockMachine(_vmgr.getVBox().getSessionObject(), LockType.Shared);
			work(params[0], _vmgr, _vmgr.getVBox().getSessionObject().getConsole());
			return params[0];
		}

		protected abstract void work(IMachine m, WebSessionManager vmgr, IConsole console) throws Exception;
}
