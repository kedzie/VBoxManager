package com.kedzie.vbox.harness;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IPerformanceMetric;
import com.kedzie.vbox.machine.MachineListActivity;
import com.kedzie.vbox.metrics.MetricActivity;
import com.kedzie.vbox.metrics.MetricView;
import com.kedzie.vbox.server.Server;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.BaseTask;

/**
 * Initializes & launches arbitrary activity from main launcher
 * @author Marek Kedzierski
 */
public class HarnessActivity extends Activity {
	private static final String TAG = HarnessActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Harness created");
		Server server = new Server(0L, null, "192.168.1.10",18083, "kedzie", "Mk0204$$" );
		new MachineListTask().execute(server);
//		new GLMetricsTask(MetricActivity.Implementation.OPENGL).execute(server); 
//		new GLMetricsTask(MetricActivity.Implementation.SURFACEVIEW).execute(server); 
	}
	
	class GLMetricsTask extends  BaseTask<Server, IHost> {
		private MetricView.Implementation implementation;
		
		public GLMetricsTask(MetricView.Implementation i) {
			super(TAG, HarnessActivity.this, null, "Starting GLMetrics");
			this.implementation=i;
		}
			
		@Override
		protected IHost work(Server... server) throws Exception {
			_vmgr = new VBoxSvc("http://"+server[0].getHost()+":"+server[0].getPort());
			_vmgr.logon(server[0].getUsername(), server[0].getPassword());
			IHost host = _vmgr.getVBox().getHost();
			host.getMemorySize();
			_vmgr.getVBox().getPerformanceCollector().setupMetrics(new String[] { "*:" }, VBoxApplication.getPeriodPreference(context), 1, host);
			for(IPerformanceMetric m : _vmgr.getVBox().getPerformanceCollector().getMetrics(new String[] {"*:"}, _vmgr.getVBox().getHost().getIdRef()) )
				Log.i(TAG, "Host metric: " + m.getMetricName());
			List<IMachine> machines = _vmgr.getVBox().getMachines();
			for(IPerformanceMetric m : _vmgr.getVBox().getPerformanceCollector().getMetrics(new String[] {"*:"}, machines.get(0).getIdRef()) )
				Log.i(TAG, "Machine metric: " + m.getMetricName());
			Thread.sleep(2000);
			return host;
		}

		@Override
		protected void onPostExecute(IHost host) {
			startActivity(new Intent(HarnessActivity.this, MetricActivity.class)
					.putExtra(VBoxSvc.BUNDLE, _vmgr)
					.putExtra(MetricActivity.INTENT_IMPLEMENTATION, implementation.name())
					.putExtra(MetricActivity.INTENT_TITLE, "Host Metrics")
					.putExtra(MetricActivity.INTENT_OBJECT,host.getIdRef() )
					.putExtra(MetricActivity.INTENT_RAM_AVAILABLE, host.getMemorySize())
					.putExtra(MetricActivity.INTENT_CPU_METRICS , new String[] { "CPU/Load/User", "CPU/Load/Kernel" } )
					.putExtra(MetricActivity.INTENT_RAM_METRICS , new String[] {  "RAM/Usage/Used" } ));
			super.onPostExecute(host);
		}
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
			startActivity(new Intent(HarnessActivity.this, MachineListActivity.class).putExtra(VBoxSvc.BUNDLE, vmgr));
			super.onPostExecute(vmgr);
		}
	}
	
	protected VBoxApplication getApp() {
		return (VBoxApplication)getApplication();
	}
}
