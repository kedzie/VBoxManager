package com.kedzie.vbox.harness;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.jaxb.LockType;
import com.kedzie.vbox.machine.MachineListFragmentActivity;
import com.kedzie.vbox.machine.MachineView;
import com.kedzie.vbox.metrics.MetricActivity;
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
		setContentView(R.layout.harness);
		
		final Server server = new Server(0L, null, "99.38.98.125",18083, "kedzie", "Mk0204$$" );
		((Button)findViewById(R.id.testParcelButton)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new TestParcelTask().execute(server);
			}
		});
		((Button)findViewById(R.id.machineListButton)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new MachineListTask().execute(server);
			}
		});
		((Button)findViewById(R.id.hostMetricsButton)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new HostMetricsTask().execute(server);
			}
		});
	}
	
	class MachineListTask extends  ActionBarTask<Server, VBoxSvc> {

		public MachineListTask() { super(HarnessActivity.TAG, HarnessActivity.this, null); }

		@Override
		protected VBoxSvc work(Server... server) throws Exception {
			_vmgr = new VBoxSvc("http://"+server[0].getHost()+":"+server[0].getPort());
			_vmgr.logon(server[0].getUsername(), server[0].getPassword());
			return _vmgr;
		}

		@Override
		protected void onPostExecute(VBoxSvc vmgr) {
			startActivity(new Intent(HarnessActivity.this, MachineListFragmentActivity.class).putExtra(VBoxSvc.BUNDLE, vmgr));
			super.onPostExecute(vmgr);
		}
	}
	
	class HostMetricsTask extends  ActionBarTask<Server, IHost> {

		public HostMetricsTask() { super(HarnessActivity.TAG, HarnessActivity.this, null); }

		@Override
		protected IHost work(Server... server) throws Exception {
			_vmgr = new VBoxSvc("http://"+server[0].getHost()+":"+server[0].getPort());
			_vmgr.logon(server[0].getUsername(), server[0].getPassword());
			IHost host = _vmgr.getVBox().getHost();
			host.getMemorySize();
			return host;
		}

		@Override
		protected void onPostExecute(IHost host) {
			startActivity(new Intent(HarnessActivity.this, MetricActivity.class)
					.putExtra(VBoxSvc.BUNDLE, _vmgr)
					.putExtra(MetricActivity.INTENT_TITLE, R.string.host_metrics)
					.putExtra(MetricActivity.INTENT_OBJECT, host.getIdRef() )
					.putExtra(MetricActivity.INTENT_RAM_AVAILABLE, host.getMemorySize())
					.putExtra(MetricActivity.INTENT_CPU_METRICS , new String[] { "CPU/Load/User", "CPU/Load/Kernel" } )
					.putExtra(MetricActivity.INTENT_RAM_METRICS , new String[] {  "RAM/Usage/Used" }));
			super.onPostExecute(host);
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
			ISession session = _vmgr.getVBox().getSessionObject();
			m.lockMachine(session, LockType.SHARED);
			Bundle b = new Bundle();
			b.putParcelable("mp", m);
			IMachine mp = b.getParcelable("mp");
			System.out.println("Machine cache: " + mp.getCache());
			return _vmgr;
		}
	}
}
