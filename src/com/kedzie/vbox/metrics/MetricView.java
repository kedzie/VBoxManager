package com.kedzie.vbox.metrics;

import java.util.Map;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IPerformanceMetric;

public class MetricView extends LinearLayout {

	private View view;
	private BaseMetricView _renderer;
	
	/** Metric View component implementations */
	public enum Implementation { SURFACEVIEW, OPENGL; }

	public MetricView(Context context, String title, Implementation implementation,
			int max, String []metrics, IPerformanceMetric pm) {
		super(context);
		setOrientation(LinearLayout.VERTICAL);

		if(implementation.equals(Implementation.SURFACEVIEW)) {
			view = new SurfaceView(getContext());
			_renderer = new MetricViewSurfaceView(getContext(), (SurfaceView) view, max, metrics, pm);
		} else if (implementation.equals(Implementation.OPENGL)) {
			view = new GLSurfaceView(getContext());
			_renderer = new MetricViewGL(getContext(), (GLSurfaceView)view, max, metrics, pm);
		}
		TextView titletextView = new TextView(getContext());
		titletextView.setText(title);
		titletextView.setTextSize(16.f);
		addView(titletextView,
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.weight=1.f;
		addView(view, params);
		createMetricTextFields( metrics);
	}

	protected void createMetricTextFields(String[] metrics) {
		LinearLayout ll = new LinearLayout(getContext());
		ll.setOrientation(LinearLayout.HORIZONTAL);
		for(String m : metrics) {
			TextView textView = new TextView(getContext());
			textView.setText(m);
			textView.setTextColor(VBoxApplication.getColor(getContext(), m.replace('/', '_')));
			textView.setPadding(0,0,8,0);
			ll.addView(textView, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
		}
		addView(ll, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
	}

	/**
	 * Get X-coordinate of a specific point in time
	 * @param width	width (pixels) of graph
	 * @param pixelsPerSecond	how many pixels represent a second in time
	 * @param stamp datapoint timestamp
	 * @param current current timestamp
	 * @return X coordinate of data point
	 */
	public static int getXPixelFromTimestamp(int width, double pixelsPerSecond, long stamp, long current) {
		return width-(int)(((current-stamp)/1000.d)*pixelsPerSecond);
	}

	public void addData(Map<String, Point2F> d) {
		_renderer.addData(d);
	}

	public String[] getMetrics() {
		return _renderer.getMetrics();
	}

	public void setMetricPreferences(int period, int count) {
		_renderer.setMetricPreferences(period, count);
	}

	public void pause() {
		_renderer.pause();
	}

	public void resume() {
		_renderer.resume();
	}
}
