package com.kedzie.vbox.metrics;

import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kedzie.vbox.VBoxApplication;

public class MetricView extends LinearLayout {

	private MetricRenderer _renderer;
	
	public MetricView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init("Metric Name", 100, null);
	}

	public MetricView(Context context, String title, int max, String []metrics) {
		super(context);
		init(title, max, metrics);
	}
	
	private void init(String title, int max, String []metrics) {
		setOrientation(LinearLayout.VERTICAL);
		_renderer = new MetricRenderer(getContext(), max, metrics);
		TextView titletextView = new TextView(getContext());
		titletextView.setText(title);
		titletextView.setTextSize(16.f);
		addView(titletextView,
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.weight=1.f;
		addView(_renderer, params);
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
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
	}

	public void setQueries(Map<String,MetricQuery> q) {
		_renderer.setQuery(q);
	}
}
