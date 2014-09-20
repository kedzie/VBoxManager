
package com.kedzie.vbox.harness;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.app.VBoxProgressDialog;
import com.kedzie.vbox.machine.MachineListActivity;
import com.kedzie.vbox.machine.settings.VMSettingsActivity;
import com.kedzie.vbox.server.Server;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;
import com.kedzie.vbox.task.DialogTask;

/**
 * Initializes & launches arbitrary activity from main launcher
 * 
 * @author Marek Kedzierski
 */
public class HarnessActivity extends BaseActivity {

	private class LogonTask extends ActionBarTask<Server, Void> {

		public LogonTask(VBoxSvc vmgr) {
			super(HarnessActivity.this, vmgr);
		}

		@Override
		protected Void work(Server... server) throws Exception {
			_vboxApi = new VBoxSvc(server[0]);
			_vboxApi.logon();
			_vboxApi.getVBox().getHost().getMemorySize();
			return null;
		}
	}

	private class LogoffTask extends ActionBarTask<Void, Void> {

		public LogoffTask(VBoxSvc vmgr) {
			super(HarnessActivity.this, vmgr);
		}

		@Override
		protected Void work(Void... params) throws Exception {
			_vmgr.logoff();
			return null;
		}
	}

	private class MachineSettingsTask extends ActionBarTask<String, IMachine> {

		public MachineSettingsTask(VBoxSvc vmgr) {
			super(HarnessActivity.this, vmgr);
		}

		@Override
		protected IMachine work(String... params) throws Exception {
			IMachine machine = _vmgr.getVBox().findMachine(params[0]);
			Utils.cacheProperties(machine);
			return machine;
		}

		@Override
		protected void onSuccess(IMachine result) {
			super.onSuccess(result);
			startActivity(new Intent(HarnessActivity.this, VMSettingsActivity.class).putExtra(VBoxSvc.BUNDLE, (Parcelable)_vboxApi).putExtra(IMachine.BUNDLE, result));
		}
	}

    private class MockProgressTask extends DialogTask<Void, String> {

        public MockProgressTask() {
            super(HarnessActivity.this, null, "Mock Task");
        }

        @Override
        protected String work(Void... params) throws Exception {
            handleProgress(new MockProgress());
            return "Bla";
        }

        @Override
        protected void onSuccess(String result) {
            super.onSuccess(result);
        }
    }

	private VBoxSvc _vboxApi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new LogonTask( _vboxApi).execute(new Server(null,"10.0.2.2", false, 19083, "kedzie", "Mk0204$$"));
	}
	
	@Override
    protected void onDestroy() {
        new LogoffTask(_vboxApi).execute();
        super.onDestroy();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.harness_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.harness_machineList:
				startActivity(new Intent(this, MachineListActivity.class).putExtra(VBoxSvc.BUNDLE, (Parcelable)_vboxApi));
				return true;
			case R.id.harness_machineSettings:
				new MachineSettingsTask(_vboxApi).execute("TEST");
				return true;
            case R.id.harness_task:
                new MockProgressTask().execute();
		}
		return false;
	}
}
