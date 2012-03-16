package com.kedzie.vbox.metrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.LoopingThread;

public class DataThread extends LoopingThread {
		private static final String TAG = "MetricDataThread";
		
		private VBoxSvc _vmgr;
		private Renderer []_views;
		private String _object;
		private int _period;
		
		public DataThread(VBoxSvc vmgr, String object, int period, Renderer...views){
			super("Metric Data");
			_vmgr=vmgr;
			_object=object;
			_period=period;
			_views=views;
		}
		
		public void setPeriod(int period) {
			_period = period;
		}

		@Override
		@SuppressWarnings("unchecked")
		public void loop() {
			try {
				Map<String, Point2F> newData = new HashMap<String, Point2F>();
				Map<String, Map<String, Object>> data = _vmgr.queryMetricsData(_object, "*:");
				if(data.keySet().isEmpty()) return;
				for(String metric : data.keySet()){
					List<Integer> values = (List<Integer>)data.get(metric).get("val");
					if(values==null || values.isEmpty()) continue;
					Point2F datapoint = new Point2F( 0, values.get(0), System.currentTimeMillis()+1000 );
					newData.put(metric, datapoint);
				}
				for(Renderer v : _views)
					v.addData(newData);
			} catch (Exception e) {
				Log.e(TAG, "", e);
			} finally {
				try { Thread.sleep(_period*1000); } catch (InterruptedException e) { }
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
			
			public void setMetricPreferences(int period, int count);
			
			public void pause();
			
			public void resume();
		}
}
