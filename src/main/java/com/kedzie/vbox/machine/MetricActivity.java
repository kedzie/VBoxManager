package com.kedzie.vbox.machine;

import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.WebSessionManager;

public class MetricActivity extends Activity {
	private static final String TAG = "vbox."+MetricActivity.class.getSimpleName();

	WebSessionManager vmgr;
	MetricView cpuView;
	MetricView ramView;
	String object;
	private String[] cpuMetrics;
	private String[] ramMetrics;
	private int count;
	private int period;
	
	Handler _handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			cpuView.invalidate();
			ramView.invalidate();
		}
	};
	
	class MetricThread extends Thread {
		boolean _running=true;
		@Override
		public void run() {
			while(_running) {
				try {
					Map<String, Map<String, Object>> data = vmgr.queryMetricsData(new String[] { "*:" }, count, period, object);
					cpuView.setData(data);
					ramView.setData(data);
					_handler.sendEmptyMessage(1);
					Thread.sleep(period*1000);
				} catch (Exception e) {
					Log.e(TAG, "", e);
				}
			}
		}
	};
	
	private MetricThread _thread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.metric);
		vmgr = getIntent().getParcelableExtra("vmgr");
		object = getIntent().getStringExtra("object");
		cpuMetrics = getIntent().getStringArrayExtra("cpuMetrics");
		ramMetrics = getIntent().getStringArrayExtra("ramMetrics");
		this.period = getSharedPreferences(getPackageName(), 0).getInt("metric_period", 1);
		this.count = getSharedPreferences(getPackageName(), 0).getInt("metric_count", 50);
		cpuView = (MetricView)findViewById(R.id.cpu_metrics);
		ramView = (MetricView)findViewById(R.id.ram_metrics);
		cpuView.init(count, period, 100000, cpuMetrics);
		ramView.init(count, period, 8000000, ramMetrics);
		_thread = new MetricThread();
		_thread._running = true;
		_thread.start();
	}

	@Override
	protected void onDestroy() {
		boolean retry = true;
        _thread._running= false;
        while (retry) {
            try {
                _thread.join();
                retry = false;
            } catch (InterruptedException e) { }
        }
		super.onDestroy();
	}

}
