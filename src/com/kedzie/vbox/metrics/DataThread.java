package com.kedzie.vbox.metrics;

import java.util.Map;

import android.util.Log;

import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.LoopingThread;

public class DataThread extends LoopingThread {
		private static final String TAG = "MetricDataThread";

		private VBoxSvc _vmgr;
		private MetricView []_views;
		private String _object;
		private int _period;

		public DataThread(VBoxSvc vmgr, String object, int period, MetricView...views){
			super("Metric Data");
			_vmgr=vmgr;
			_object=object;
			_period=period;
			_views=views;
		}

		@Override
		public void loop() {
			try {
				Map<String, MetricQuery> data = _vmgr.queryMetrics(_object, "*:");
				for(MetricView v : _views)
					v.setQueries(data);
			} catch (Exception e) {
				Log.e(TAG, "", e);
			} finally {
				try { Thread.sleep(_period*1000); } catch (InterruptedException e) { 
					_running=false;
				}
			}
		}
}
