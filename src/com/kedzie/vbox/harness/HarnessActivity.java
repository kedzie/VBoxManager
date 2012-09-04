package com.kedzie.vbox.harness;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.machine.MachineListFragmentActivity;
import com.kedzie.vbox.machine.MachineView;
import com.kedzie.vbox.server.Server;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;

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
		Server server = new Server(0L, null, "99.38.98.125",18083, "kedzie", "Mk0204$$" );
		new TestParcelTask().execute(server);
	}
	
	class MachineListTask extends  ActionBarTask<Server, VBoxSvc> {

		public MachineListTask() {
			super(HarnessActivity.TAG, HarnessActivity.this, null);
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

	class TestParcelTask extends  ActionBarTask<Server, VBoxSvc> {
		public TestParcelTask() { super(HarnessActivity.TAG, HarnessActivity.this, null); }

		@Override
		protected VBoxSvc work(Server... server) throws Exception {
			_vmgr = new VBoxSvc("http://"+server[0].getHost()+":"+server[0].getPort());
			_vmgr.logon(server[0].getUsername(), server[0].getPassword());
			List<IMachine> machines = _vmgr.getVBox().getMachines();
			IMachine m = machines.get(0);
			MachineView.cacheProperties(m);
			Bundle b = new Bundle();
			b.putParcelable("mp", m);
			IMachine mp = b.getParcelable("mp");
			System.out.println("Machine cache: " + mp.getCache());
			return _vmgr;
		}
	}
}
