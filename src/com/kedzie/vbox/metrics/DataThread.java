package com.kedzie.vbox.metrics;

import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.kedzie.vbox.app.LoopingThread;
import com.kedzie.vbox.soap.VBoxSvc;

public class DataThread extends LoopingThread {
		private static final String TAG = "MetricDataThread";

		private VBoxSvc _vmgr;
		private MetricView []_views;
		private String _object;
		private int _period;
		private Context _context;
		private LocalBroadcastManager _lbm;
		
		public DataThread(Context context, VBoxSvc vmgr, String object, int period, MetricView...views){
			super("Metric Data");
			_vmgr=vmgr;
			_object=object;
			_period=period;
			_views=views;
			_context = context;
			_lbm = LocalBroadcastManager.getInstance(_context);
		}

		@Override
		public void loop() {
			try {
				Map<String, MetricQuery> data = _vmgr.queryMetrics(_object, "*:");
				for(MetricView v : _views) {
				    if(v!=null)
				        v.setQueries(data);
					_lbm.sendBroadcast(new Intent(MetricActivity.ACTION_METRIC_QUERY));
				}
			} catch (Exception e) {
				Log.e(TAG, "", e);
			} finally {
				try { Thread.sleep(_period*1000); } catch (InterruptedException e) { 
					_running=false;
				}
			}
		}
}
