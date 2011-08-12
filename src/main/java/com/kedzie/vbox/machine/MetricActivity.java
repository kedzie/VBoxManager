package com.kedzie.vbox.machine;

import java.util.Map;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.VBoxSvc;

public class MetricActivity extends Activity {
	private static final String TAG = "vbox."+MetricActivity.class.getSimpleName();
	public static final String INTENT_OBJECT = "object",  INTENT_RAM_AVAILABLE = "ram_available";
	
	private MetricView cpuView, ramView;
	private MetricThread _thread;
	
	class MetricThread extends Thread {
		boolean _running=true;
		private VBoxSvc _vmgr;
		private MetricView []_views;
		private String _object;
		private int _count, _period;
		private Handler _handler =new Handler() {
			@Override
			public void handleMessage(Message msg) {
				for(MetricView v : _views) 	v.invalidate();
			}
		};
		
		public MetricThread(VBoxSvc vmgr, String object, int count, int period, MetricView...views){
			_vmgr=vmgr;
			_object=object;
			_period=period;
			_count=count;
			_views=views;
		}
		
		@Override
		public void run() {
			while(_running) {
				try {
					Map<String, Map<String, Object>> data = _vmgr.queryMetricsData(_object, _count, _period, "*:");
					for(MetricView v : _views) 	v.setData(data);
					_handler.sendEmptyMessage(1);
					Thread.sleep(_period*1000);
				} catch (Exception e) {
					Log.e(TAG, "", e);
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.metric);
		VBoxSvc vmgr = getIntent().getParcelableExtra("vmgr");
		setTitle(getIntent().getStringExtra("title"));
		((TextView)findViewById(R.id.cpu_metrics_title)).setText("CPU Load");
		((TextView)findViewById(R.id.ram_metrics_title)).setText("Memory Usage");

		int count =getApp().getCount();
		int period = getApp().getPeriod();
		cpuView = (MetricView)findViewById(R.id.cpu_metrics);
		cpuView.init(count, period, 100000L, getIntent().getStringArrayExtra("cpuMetrics"));
		ramView = (MetricView)findViewById(R.id.ram_metrics);
		ramView.init(count, period, getIntent().getIntExtra(INTENT_RAM_AVAILABLE, 0)*1000, getIntent().getStringArrayExtra("ramMetrics"));
		
		_thread = new MetricThread(vmgr, getIntent().getStringExtra(INTENT_OBJECT), count, period, cpuView, ramView);
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
	
	public VBoxApplication getApp() { 
		return (VBoxApplication)getApplication(); 
	}

}
