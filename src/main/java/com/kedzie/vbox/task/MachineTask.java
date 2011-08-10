package com.kedzie.vbox.task;

import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.SessionState;

import android.content.Context;
import android.os.Handler;

import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;

/**
 * Machine operation without VirtualBox progress handling
 * @author Marek Kedzierski
 * @Aug 8, 2011
 */
public abstract class MachineTask extends BaseTask<IMachine, IMachine> {

		public MachineTask(final Context ctx, VBoxSvc vmgr, String msg, boolean indeterminate) {
			super(ctx, vmgr, msg, indeterminate);
		}
		
		public MachineTask(Context ctx, VBoxSvc vmgr, String msg, boolean indeterminate, Handler h) {
			super(ctx, vmgr, msg, indeterminate, h);
		}
		
		@Override
		protected IMachine work(IMachine... params) throws Exception {
//			_vmgr.getVBox().getSessionObject().clearCache();
			if( _vmgr.getVBox().getSessionObject().getState().equals(SessionState.Unlocked)) 
				params[0].lockMachine(_vmgr.getVBox().getSessionObject(), LockType.Shared);
			
			if(pDialog.isIndeterminate())
				work(params[0], _vmgr, _vmgr.getVBox().getSessionObject().getConsole() );
			else
				handleProgress( workWithProgress(params[0], _vmgr, _vmgr.getVBox().getSessionObject().getConsole()) );
			
			if( _vmgr.getVBox().getSessionObject().getState().equals(SessionState.Locked))
				_vmgr.getVBox().getSessionObject().unlockMachine();
			return params[0];
		}

		protected void work(IMachine m, VBoxSvc vmgr, IConsole console) throws Exception {};
		
		protected IProgress workWithProgress(IMachine m, VBoxSvc vmgr, IConsole console) throws Exception { return null; };
}
