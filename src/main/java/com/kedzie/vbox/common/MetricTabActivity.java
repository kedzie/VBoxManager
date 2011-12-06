package com.kedzie.vbox.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.TabActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.VBoxSvc;

public class MetricTabActivity extends TabActivity  {
	private static final String TAG = MetricTabActivity.class.getSimpleName();
	public static final String INTENT_OBJECT = "object",  INTENT_RAM_AVAILABLE = "ram_available", INTENT_RAM_METRICS="ram_metrics", INTENT_CPU_METRICS="cpu_metrics";
	
	private MetricView cpuView, ramView;
	private MetricThread _thread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.metric_tabs);
		VBoxSvc vmgr = getIntent().getParcelableExtra("vmgr");
		setTitle(getIntent().getStringExtra("title"));
		String []cpuMetrics = getIntent().getStringArrayExtra(INTENT_CPU_METRICS);
		String [] ramMetrics = getIntent().getStringArrayExtra(INTENT_RAM_METRICS);
		String object = getIntent().getStringExtra(INTENT_OBJECT);
		int ramAvailable = getIntent().getIntExtra(INTENT_RAM_AVAILABLE, 0);
		try {
			getTabHost().addTab( getTabHost().newTabSpec("cpu").setIndicator("CPU", getResources().getDrawable(R.drawable.ic_tab_actions)).setContent(R.id.cpu_tab) );
			getTabHost().addTab( getTabHost().newTabSpec("cpu").setIndicator("CPU", getResources().getDrawable(R.drawable.ic_tab_actions)).setContent(R.id.ram_tab) );
			cpuView = new MetricView(this, ((SurfaceView)findViewById(R.id.cpu_metrics)).getHolder(), getApp().getCount(), getApp().getPeriod(), 100000, cpuMetrics, vmgr.getVBox().getPerformanceCollector().getMetrics(cpuMetrics, object).get(0));
			ramView = new MetricView(this, ((SurfaceView)findViewById(R.id.ram_metrics)).getHolder(), getApp().getCount(), getApp().getPeriod(), ramAvailable*1000, ramMetrics, vmgr.getVBox().getPerformanceCollector().getMetrics(ramMetrics, object).get(0));
			_thread = new MetricThread(vmgr, object, getApp().getCount(), getApp().getPeriod(), cpuView, ramView);
			_thread.start();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	@Override
	protected void onDestroy() {
		boolean done = false;
        _thread._running= false;
        while (!done) {
            try {
                _thread.join();
                done = true;
            } catch (InterruptedException e) { }
        }
		super.onDestroy();
	}
	
	public VBoxApplication getApp() {  
		return (VBoxApplication)getApplication();  
	}
	
	class MetricThread extends Thread {
		boolean _running=true;
		private VBoxSvc _vmgr;
		private MetricView []_views;
		private String _object;
		private int _period;
		
		public MetricThread(VBoxSvc vmgr, String object, int count, int period, MetricView...views){
			super("Metric Data");
			_vmgr=vmgr;
			_object=object;
			_period=period;
			_views=views;
		}
		
		@Override @SuppressWarnings("unchecked")
		public void run() {
			while(_running) {
				try {
					Map<String, Point2D> newData = new HashMap<String, Point2D>();
					Map<String, Map<String, Object>> data = _vmgr.queryMetricsData(_object, 1, _period, "*:");
					for(String metric : data.keySet()){
						int newValue  = ((List<Integer>)data.get(metric).get("val")).get(0);
						newData.put(metric, new Point2D( 0, newValue, System.currentTimeMillis()+1000 ));
					}
					for(MetricView v : _views)
						v.addData(newData);
					Thread.sleep(_period*1000);
				} catch (Exception e) {
					Log.e(TAG, "", e);
				} 
			}
		}
	}
}
