package com.kedzie.vbox.task;

import android.content.Context;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.jaxb.LockType;
import com.kedzie.vbox.api.jaxb.SessionState;

/**
 * Machine operation without VirtualBox progress handling
 * @author Marek Kedzierski
 * @Aug 8, 2011
 */
public abstract class MachineTask extends BaseTask<IMachine, IMachine> {

		public MachineTask(Context ctx, VBoxSvc vmgr, String msg, boolean indeterminate) {
			super(ctx, vmgr, msg, indeterminate);
		}
		
		@Override
		protected IMachine work(IMachine... params) throws Exception {
			ISession session = _vmgr.getVBox().getSessionObject();
			if( session.getState().equals(SessionState.UNLOCKED)) params[0].lockMachine(session, LockType.SHARED);
			
			if(pDialog.isIndeterminate())
				work(params[0], _vmgr.getVBox().getSessionObject().getConsole() );
			else
				handleProgress( workWithProgress(params[0], _vmgr.getVBox().getSessionObject().getConsole()) );
			
			if(session.getState().equals(SessionState.LOCKED)) session.unlockMachine();
			return params[0];
		}

		protected void work(IMachine m, IConsole console) throws Exception {};
		
		protected IProgress workWithProgress(IMachine m, IConsole console) throws Exception { return null; };
}
