package com.kedzie.vbox.metrics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.kedzie.vbox.PreferencesActivity;
import com.kedzie.vbox.Utils;
import com.kedzie.vbox.api.IPerformanceMetric;

public class MetricRenderer {
	public static int EXTRA_POINTS = 2;
	private static String TAG = "MetricRenderer";
	protected Context _context;
	/** Maximum Y Value */
	protected int _max;
	/** # of data points */
	protected int _count;
	/** Time interval between datapoints */
	protected int _period;
	/** width in pixels */
	protected int _width;
	/** height (pixels) */
	protected int _height;
	/** Metric names to render */
	protected String[] _metrics;
	protected IPerformanceMetric _baseMetric;
	/** pixels/period */
	protected int hStep;
	/** pixels/unit */
	protected double vStep;
	/** pixels/second */
	protected double pixelsPerSecond;
	protected Map<String, LinkedList<Point2F>> data= new HashMap<String, LinkedList<Point2F>>();

	public MetricRenderer(Context context, int max, String []metrics, IPerformanceMetric pm) {
		_context = context;
		_max=max;
		_metrics=metrics;
		_baseMetric=pm;
		_count=Utils.getIntPreference(context, PreferencesActivity.COUNT);
		_period=Utils.getIntPreference(context, PreferencesActivity.PERIOD);
		for(String metric : _metrics)
			data.put(metric, new LinkedList<Point2F>());
	}

	public synchronized void addData(Map<String, Point2F> d) {
		for(String metric : _metrics){
			if(!d.containsKey(metric)) continue;
			d.get(metric).scaledY = (float)(d.get(metric).y*vStep);
			data.get(metric).addLast( d.get(metric) );
			if(data.get(metric).size()>_count+EXTRA_POINTS)
				data.get(metric).removeFirst();
		}
	}
	
	protected void update() {
		long timestamp= System.currentTimeMillis();
		for(String metric : _metrics) {
			if(data.get(metric).isEmpty()) continue;
			for(Point2F p : data.get(metric)) 
				p.x=getXPixelFromTimestamp(p.timestamp, timestamp);
		}
	}

	public String[] getMetrics() {
		return _metrics;
	}

	protected Context getContext() {
		return _context;
	}

	public synchronized void setSize(int width, int height) {
		Log.i(TAG, "OnSizeChanged("+width+"," + height + ")");
		_width=width;
		_height=height;
		vStep = (float)height/(float)_max;
		setMetricPreferences(_period,_count);
		for(String metric : _metrics) {  //REscale the scaled data set
			for(Point2F p : data.get(metric))
				p.scaledY = (float)(p.y*vStep);
		}
	}

	/**
	 * Get X-coordinate of a specific point in time
	 * @param stamp datapoint timestamp
	 * @param current current timestamp
	 * @return X coordinate of data point
	 */
	protected int getXPixelFromTimestamp(long stamp, long current) {
		return _width-(int)(((current-stamp)/1000.d)*pixelsPerSecond);
	}

	public synchronized void setMetricPreferences(int period, int count) {
			for(String metric : _metrics) {
				while(data.get(metric).size()>count+EXTRA_POINTS) //if count is lowered, dump unecessary data points
					data.get(metric).removeFirst();
			}
			_period = period;
			_count = count;
			hStep = _width/_count;
			pixelsPerSecond =((float)hStep/(float)_period);
			Log.i(TAG, "Metric Preferences Changed ("+period+"," + count + ") vStep: " + vStep + " hStep="+hStep);
	}

	public void pause() {}

	public void resume() {}
}
