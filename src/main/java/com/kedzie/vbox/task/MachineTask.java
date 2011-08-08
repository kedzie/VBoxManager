package com.kedzie.vbox.task;

import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.SessionState;

import android.content.Context;
import android.os.Handler;

import com.kedzie.vbox.WebSessionManager;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;

/**
 * Machine operation without VirtualBox progress handling
 * @author Marek Kedzierski
 * @Aug 8, 2011
 */
public abstract class MachineTask extends BaseTask<IMachine, IMachine> {

		public MachineTask(final Context ctx, WebSessionManager vmgr, String msg) {
			super(ctx, vmgr, msg, true);
		}
		
		public MachineTask(Context ctx, WebSessionManager vmgr, String msg, Handler h) {
			super(ctx, vmgr, msg, true, h);
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
