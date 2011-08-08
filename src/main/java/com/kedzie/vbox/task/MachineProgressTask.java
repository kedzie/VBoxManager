package com.kedzie.vbox.task;

import java.io.IOException;
import org.virtualbox_4_1.LockType;
import org.virtualbox_4_1.SessionState;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.kedzie.vbox.WebSessionManager;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;

/**
 * Machine Operation which uses VirtualBox Progress Handling
 * @author Marek Kedzierski
 * @Aug 8, 2011
 */
public abstract class MachineProgressTask extends BaseTask<IMachine, IMachine> {
		private static final String TAG = "vbox."+MachineProgressTask.class.getSimpleName();
		protected final static int PROGRESS_INTERVAL = 500;
		
		public MachineProgressTask(final Context ctx, WebSessionManager vmgr, String msg) {
			super(ctx, vmgr, msg, false);
		}
		
		public MachineProgressTask(Context ctx, WebSessionManager vmgr, String msg, Handler h) {
			super(ctx, vmgr, msg, false, h);
		}

		@Override
		protected IMachine work(IMachine... params) throws Exception {
			if( _vmgr.getVBox().getSessionObject().getState().equals(SessionState.Unlocked))
				params[0].lockMachine(_vmgr.getVBox().getSessionObject(), LockType.Shared);
			handleProgress(work(params[0], _vmgr, _vmgr.getVBox().getSessionObject().getConsole()));
			return params[0];
		}
				
		protected IProgress handleProgress(IProgress p)  throws IOException {
			if(p==null) return null;
			while(p.getCompleted()) {
				publishProgress(p);
				try { Thread.sleep(PROGRESS_INTERVAL);	} catch (InterruptedException e) { Log.e(TAG, "Interrupted", e); 	}
			}
			return p;
		}

		protected abstract IProgress work(IMachine m, WebSessionManager vmgr, IConsole console) throws Exception;
}
