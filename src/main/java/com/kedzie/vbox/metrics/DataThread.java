package com.kedzie.vbox.metrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.task.BaseThread;

public class DataThread extends BaseThread {
		private static final String TAG = "MetricDataThread";
		
		private VBoxSvc _vmgr;
		private Renderer []_views;
		private String _object;
		private int _period;
		
		public DataThread(VBoxSvc vmgr, String object, int count, int period, Renderer...views){
			super("Metric Data");
			_vmgr=vmgr;
			_object=object;
			_period=period;
			_views=views;
		}

		@Override
		@SuppressWarnings("unchecked")
		public void loop() {
			try {
				Map<String, Point2F> newData = new HashMap<String, Point2F>();
				Map<String, Map<String, Object>> data = _vmgr.queryMetricsData(_object, "*:");
				for(String metric : data.keySet()){
					int newValue  = ((List<Integer>)data.get(metric).get("val")).get(0);
					newData.put(metric, new Point2F( 0, newValue, System.currentTimeMillis()+1000 ));
				}
				for(Renderer v : _views)
					v.addData(newData);
				Thread.sleep(_period*1000);
			} catch (Exception e) {
				Log.e(TAG, "", e);
			} 
		}
		
		
		/**
		 * Takes metric data from data thread
		 */
		public static interface Renderer {
			/**
			 * Add a new metric data point
			 * @param d new datapoint
			 */
			public void addData(Map<String, Point2F> d);
			
			/**
			 * which metrics does this render?
			 * @return list of metrics
			 */
			public String[] getMetrics();
		}
}
