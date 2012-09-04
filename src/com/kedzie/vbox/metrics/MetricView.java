package com.kedzie.vbox.metrics;

import java.util.Map;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;

public class MetricView extends LinearLayout {

	private TextView _titleTextView;
	private MetricRenderer _renderer;
	private LinearLayout _metricNames;
	
	public MetricView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MetricView, 0, 0);
		try {
			setOrientation(LinearLayout.VERTICAL);
			String header = a.getString(R.styleable.MetricView_header);
			int bgColor = a.getColor(R.styleable.MetricView_backgroundColor, android.R.color.white);
			int gridColor = a.getColor(R.styleable.MetricView_backgroundColor, android.R.color.white);
			int textColor = a.getColor(R.styleable.MetricView_backgroundColor, android.R.color.white);
			int borderColor = a.getColor(R.styleable.MetricView_backgroundColor, android.R.color.white);
			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			_titleTextView = new TextView(context);
			_titleTextView.setText(header);
			_titleTextView.setTextAppearance(context, R.style.HeaderText);
			addView(_titleTextView, p);
			_renderer = new MetricRenderer(context, bgColor, gridColor, textColor, borderColor);
			p.weight=1;
			addView(_renderer, p);
			_metricNames = new LinearLayout(context);
			_metricNames.setOrientation(LinearLayout.HORIZONTAL);
			p.weight=0;
			addView(_metricNames, p);
		} finally {
			a.recycle();
		}
	}

	public void init(int max, String []metrics) {
		_renderer.init(max, metrics);
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		for(String m : metrics) {
			TextView textView = new TextView(getContext());
			textView.setText(m);
			textView.setTextColor(VBoxApplication.getColor(getContext(), m.replace('/', '_')));
			textView.setPadding(0,2,8,0);
			_metricNames.addView(textView, p);
		}
	}
	
	public void setMetricPrefs(int count, int period) {
		_renderer.setMetricPrefs(count, period);
	}

	public void setQueries(Map<String,MetricQuery> q) {
		_renderer.setQuery(q);
	}
}
