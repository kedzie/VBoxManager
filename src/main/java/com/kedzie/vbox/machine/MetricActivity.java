package com.kedzie.vbox.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IPerformanceMetric;
import com.kedzie.vbox.api.WebSessionManager;

public class MetricActivity extends Activity {
	private static final String TAG = "vbox."+MetricActivity.class.getSimpleName();
	public static final String INTENT_OBJECT = "object";
	public static final String INTENT_RAM_AVAILABLE = "ram_available";
	
	WebSessionManager vmgr;
	MetricView cpuView;
	MetricView ramView;
	String object;
	List<IPerformanceMetric> baseMetrics = new ArrayList<IPerformanceMetric>();
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
		object = getIntent().getStringExtra(INTENT_OBJECT);
		cpuMetrics = getIntent().getStringArrayExtra("cpuMetrics");
		ramMetrics = getIntent().getStringArrayExtra("ramMetrics");
		
		this.period = getSharedPreferences(getPackageName(), 0).getInt("metric_period", 1);
		this.count = getSharedPreferences(getPackageName(), 0).getInt("metric_count", 25);
		
		cpuView = (MetricView)findViewById(R.id.cpu_metrics);
		
		ramView = (MetricView)findViewById(R.id.ram_metrics);
		for(IPerformanceMetric pm : baseMetrics) {
			Log.i(TAG, "Base Metric: " + pm.getMetricName());
			if(pm.getMetricName().contains("CPU/Load")) {
				TextView cpuText = (TextView)findViewById(R.id.cpu_metrics_title);
				cpuText.setText(pm.getMetricName());
				cpuView.init(count, period, 100000L, cpuMetrics, pm);
			} else if(pm.getMetricName().contains("RAM/Usage")) {
				TextView text = (TextView)findViewById(R.id.ram_metrics_title);
				text.setText(pm.getMetricName());
				ramView.init(count, period, getIntent().getIntExtra(INTENT_RAM_AVAILABLE, 0)*1000, ramMetrics, pm);
			}
		}
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
