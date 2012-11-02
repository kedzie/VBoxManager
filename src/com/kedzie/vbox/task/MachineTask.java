package com.kedzie.vbox.task;

import android.content.Context;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.jaxb.LockType;
import com.kedzie.vbox.api.jaxb.SessionState;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Machine operation without VirtualBox progress handling
 * @author Marek Kedzierski
 * @Aug 8, 2011
 */
public abstract class MachineTask<Input, Output> extends DialogTask<Input, Output> {

		protected boolean indeterminate;
		protected IMachine _machine;
		
		public MachineTask(String TAG, Context ctx, VBoxSvc vmgr, String msg, boolean indeterminate, IMachine m) {
			super(TAG, ctx, vmgr, msg);
			this.indeterminate=indeterminate;
			_machine=m;
		}
		
		@Override
		protected Output work(Input...inputs) throws Exception {
			ISession session = _vmgr.getVBox().getSessionObject();
			if( session.getState().equals(SessionState.UNLOCKED)) 
				_machine.lockMachine(session, LockType.SHARED);
			try {
				if(indeterminate)
					return work(_machine, session.getConsole(), inputs);
				else
					handleProgress( workWithProgress(_machine, session.getConsole(), inputs) );
				return null;
			} finally {
				if(session.getState().equals(SessionState.LOCKED)) 
					session.unlockMachine();
			}
		}

		protected Output work(IMachine m, IConsole console, Input...inputs) throws Exception {return null;};
		
		protected IProgress workWithProgress(IMachine m, IConsole console, Input...inputs) throws Exception { return null; };
}