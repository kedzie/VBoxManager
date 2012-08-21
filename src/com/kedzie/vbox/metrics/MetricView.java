package com.kedzie.vbox.metrics;

import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;

public class MetricView extends LinearLayout {

	private MetricRenderer _renderer;
	private View _content;
	
	public MetricView(Context context) {
		super(context);
		_content = LayoutInflater.from(context).inflate(R.layout.metric_view, null);
	}

	public void init(String title, int max, String []metrics) {
		TextView titletextView = (TextView) _content.findViewById(R.id.title);
		titletextView.setText(title);
		_renderer = (MetricRenderer)_content.findViewById(R.id.renderer);
		_renderer.init(max, metrics);
		LinearLayout ll = (LinearLayout)_content.findViewById(R.id.metric_names);
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		for(String m : metrics) {
			TextView textView = new TextView(getContext());
			textView.setText(m);
			textView.setTextColor(VBoxApplication.getColor(getContext(), m.replace('/', '_')));
			textView.setPadding(0,2,8,0);
			ll.addView(textView, p);
		}
	}

	public void setQueries(Map<String,MetricQuery> q) {
		_renderer.setQuery(q);
	}
}
