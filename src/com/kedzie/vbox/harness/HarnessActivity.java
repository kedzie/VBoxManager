package com.kedzie.vbox.harness;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IDisplay;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.jaxb.LockType;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.machine.MachineListFragmentActivity;
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

	FrameLayout imageFrame;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Harness created");
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.harness);
		imageFrame = (FrameLayout)findViewById(R.id.imageFrame);
		
		final Server server = new Server(null, "99.38.98.125", true, 18083, "kedzie", "Mk0204$$" );
		((Button)findViewById(R.id.testParcelButton)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new TestApiTask().execute(server);
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
	
	@Override
	protected void onStart() {
		super.onStart();
		setProgressBarIndeterminateVisibility(false);
	}
	
	class MachineListTask extends  ActionBarTask<Server, String> {

		public MachineListTask() { super(HarnessActivity.TAG, HarnessActivity.this, null); }

		@Override
		protected String work(Server... server) throws Exception {
			_vmgr = new VBoxSvc(server[0]);
			_vmgr.logon();
			return _vmgr.getVBox().getVersion();
		}

		@Override
		protected void onResult(String version) {
			startActivity(new Intent(HarnessActivity.this, MachineListFragmentActivity.class).putExtra(VBoxSvc.BUNDLE, _vmgr).putExtra("version", version));
		}
	}
	
	class HostMetricsTask extends  ActionBarTask<Server, IHost> {

		public HostMetricsTask() { super(HarnessActivity.TAG, HarnessActivity.this, null); }

		@Override
		protected IHost work(Server... server) throws Exception {
			_vmgr = new VBoxSvc(server[0]);
			_vmgr.logon();
			IHost host = _vmgr.getVBox().getHost();
			host.getMemorySize();
			return host;
		}

		@Override
		protected void onResult(IHost host) {
			startActivity(new Intent(HarnessActivity.this, MetricActivity.class)
					.putExtra(VBoxSvc.BUNDLE, _vmgr)
					.putExtra(MetricActivity.INTENT_TITLE, R.string.host_metrics)
					.putExtra(MetricActivity.INTENT_OBJECT, host.getIdRef() )
					.putExtra(MetricActivity.INTENT_RAM_AVAILABLE, host.getMemorySize())
					.putExtra(MetricActivity.INTENT_CPU_METRICS , new String[] { "CPU/Load/User", "CPU/Load/Kernel" } )
					.putExtra(MetricActivity.INTENT_RAM_METRICS , new String[] {  "RAM/Usage/Used" }));
		}
	}

	class TestApiTask extends  ActionBarTask<Server, byte[]> {
		public TestApiTask() { super(HarnessActivity.TAG, HarnessActivity.this, null); }

		@Override
		protected byte[] work(Server... server) throws Exception {
			_vmgr = new VBoxSvc(server[0]);
			_vmgr.logon();
			IMachine m = _vmgr.getVBox().getMachines().get(0);
			ISession session = _vmgr.getVBox().getSessionObject();
			m.lockMachine(session, LockType.SHARED);
			IConsole console = session.getConsole();
			IDisplay display = console.getDisplay();
			Map<String, List<String>> res = display.getScreenResolution(0);
			byte[] screenshot = display.takeScreenShotPNGToArray(0, new Integer(res.get("width").get(0)), new Integer(res.get("height").get(0)));
			session.unlockMachine();
			_vmgr.logoff();
			return screenshot;
		}
		
		@Override
		protected void onResult(byte[] result) {
			Bitmap screenshot = BitmapFactory.decodeStream(new ByteArrayInputStream(result));
			Utils.toastShort( HarnessActivity.this, String.format("Screenshot size %1$dx%2$d", screenshot.getWidth(), screenshot.getHeight()) );
			ImageView view = new ImageView(HarnessActivity.this);
			view.setImageBitmap(screenshot);
			imageFrame.addView(view, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
		}
	}
}
