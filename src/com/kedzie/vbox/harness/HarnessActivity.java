package com.kedzie.vbox.harness;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.kedzie.vbox.machine.MachineListFragmentActivity;
import com.kedzie.vbox.server.Server;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.BaseTask;

/**
 * Initializes & launches arbitrary activity from main launcher
 * @author Marek Kedzierski
 */
public class HarnessActivity extends SherlockFragmentActivity {
	private static final String TAG = HarnessActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Harness created");
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setSupportProgressBarIndeterminateVisibility(false);
		Server server = new Server(0L, null, "192.168.1.10",18083, "kedzie", "Mk0204$$" );
		new MachineListTask().execute(server);
	}

	class MachineListTask extends  BaseTask<Server, VBoxSvc> {

		public MachineListTask() {
			super(TAG, HarnessActivity.this, null, "Starting Machine List");
		}

		@Override
		protected VBoxSvc work(Server... server) throws Exception {
			_vmgr = new VBoxSvc("http://"+server[0].getHost()+":"+server[0].getPort());
			_vmgr.logon(server[0].getUsername(), server[0].getPassword());
			return _vmgr;
		}

		@Override
		protected void onPostExecute(VBoxSvc vmgr) {
			startActivity(new Intent(HarnessActivity.this, MachineListFragmentActivity.class)
					.putExtra(VBoxSvc.BUNDLE, (Parcelable)vmgr));
			super.onPostExecute(vmgr);
		}
	}
}
