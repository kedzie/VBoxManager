package com.kedzie.vbox.task;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;

import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.jaxb.LockType;
import com.kedzie.vbox.api.jaxb.SessionState;
import com.kedzie.vbox.soap.VBoxSvc;

import java.io.IOException;

/**
 * Operation on {@link IMachine} with progress handling by service notification
 *
 * @apiviz.stereotype Task
 */
public abstract class MachineTask<Input, Output> extends ServiceTask<Input, Output> {

		protected IMachine _machine;

		public MachineTask(AppCompatActivity context, VBoxSvc vmgr, int icon, boolean indeterminate, IMachine m) {
			super(context, vmgr, icon);
			_indeterminate=indeterminate;
			_machine=m;
		}
		
		@Override
		protected Output work(Input...inputs) throws Exception {
			ISession session = _vmgr.getVBox().getSessionObject();
			if( session.getState().equals(SessionState.UNLOCKED)) 
				_machine.lockMachine(session, LockType.SHARED);
			try {
				if(_indeterminate)
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
