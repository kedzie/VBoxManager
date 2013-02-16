
package com.kedzie.vbox.harness;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.app.BaseActivity;
import com.kedzie.vbox.machine.MachineListActivity;
import com.kedzie.vbox.machine.MachineView;
import com.kedzie.vbox.machine.settings.CategoryActivity;
import com.kedzie.vbox.server.Server;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;

/**
 * Initializes & launches arbitrary activity from main launcher
 * 
 * @author Marek Kedzierski
 */
public class HarnessActivity extends BaseActivity {

	private class LogonTask extends ActionBarTask<Server, Void> {

		public LogonTask(VBoxSvc vmgr) {
			super("LogonTask", HarnessActivity.this, vmgr);
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
			super("LogoffTask", HarnessActivity.this, vmgr);
		}

		@Override
		protected Void work(Void... params) throws Exception {
			_vmgr.logoff();
			return null;
		}
	}

	private class MachineSettingsTask extends ActionBarTask<String, IMachine> {

		public MachineSettingsTask(VBoxSvc vmgr) {
			super("MachineSettingsTask", HarnessActivity.this, vmgr);
		}

		@Override
		protected IMachine work(String... params) throws Exception {
			IMachine machine = _vmgr.getVBox().findMachine(params[0]);
			MachineView.cacheProperties(machine);
			return machine;
		}

		@Override
		protected void onResult(IMachine result) {
			super.onResult(result);
			startActivity(new Intent(HarnessActivity.this, CategoryActivity.class).putExtra(VBoxSvc.BUNDLE, (Parcelable)_vboxApi).putExtra(IMachine.BUNDLE, result));
		}
	}

	private VBoxSvc _vboxApi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new LogonTask( _vboxApi).execute(new Server(null,"192.168.1.10", false, 18083, "kedzie", "Mk0204$$"));
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
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		new LogoffTask(_vboxApi).execute();
	}
}
