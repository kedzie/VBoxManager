package com.kedzie.vbox.machine;

import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IPerformanceMetric;

public class MetricActivity extends Activity {
	private static final String TAG = "vbox."+MetricActivity.class.getSimpleName();
	public static final String INTENT_OBJECT = "object";
	public static final String INTENT_RAM_AVAILABLE = "ram_available";
	
	MetricView cpuView;
	MetricView ramView;
	String object;
	int count;
	int period;
	
	class MetricThread extends Thread {
		boolean _running=true;
		private VBoxSvc _vmgr;
		private Handler _handler;
		private MetricView []_views;
		private IPerformanceMetric _metric;
		
		public MetricThread(VBoxSvc vmgr, Handler h, IPerformanceMetric metric, MetricView...views){
			_vmgr=vmgr;
			_handler=h;
			_views=views;
			_metric = metric;
		}
		
		@Override
		public void run() {
			while(_running) {
				try {
					Map<String, Map<String, Object>> data = _vmgr.queryMetricsData(object, count, period, "*:");
					for(MetricView v : _views) {
						v.setData(data);
					}
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
		VBoxSvc vmgr = getIntent().getParcelableExtra("vmgr");
		object = getIntent().getStringExtra(INTENT_OBJECT);
		String []cpuMetrics = getIntent().getStringArrayExtra("cpuMetrics");
		String []ramMetrics = getIntent().getStringArrayExtra("ramMetrics");
		setTitle(getIntent().getStringExtra("title"));
		this.period = getSharedPreferences(getPackageName(), 0).getInt("metric_period", 1);
		this.count = getSharedPreferences(getPackageName(), 0).getInt("metric_count", 25);
		
		
		((TextView)findViewById(R.id.cpu_metrics_title)).setText("CPU Load");
		((TextView)findViewById(R.id.ram_metrics_title)).setText("Memory Usage");
		cpuView = (MetricView)findViewById(R.id.cpu_metrics);
		cpuView.init(count, period, 100000L, cpuMetrics);
		ramView = (MetricView)findViewById(R.id.ram_metrics);
		ramView.init(count, period, getIntent().getIntExtra(INTENT_RAM_AVAILABLE, 0)*1000, ramMetrics);
		
		
		
		_thread = new MetricThread(vmgr,  new Handler() {
			@Override
			public void handleMessage(Message msg) {
				cpuView.invalidate();
				ramView.invalidate();
			}
		}, null, cpuView, ramView);
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
