package com.kedzie.vbox.metrics;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.VBoxSvc;

public class MetricActivity extends Activity  {
	private static final String TAG = MetricActivity.class.getSimpleName();
	public static final String INTENT_TITLE="title", INTENT_OBJECT = "object",  INTENT_RAM_AVAILABLE = "ram_available", INTENT_RAM_METRICS="ram_metrics", INTENT_CPU_METRICS="cpu_metrics";
	
	private View cpuRenderer, ramRenderer;
	private DataThread _thread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		VBoxSvc vmgr = getIntent().getParcelableExtra(VBoxSvc.BUNDLE);
		setTitle(getIntent().getStringExtra(INTENT_TITLE));
		String []cpuMetrics = getIntent().getStringArrayExtra(INTENT_CPU_METRICS);
		String [] ramMetrics = getIntent().getStringArrayExtra(INTENT_RAM_METRICS);
		String object = getIntent().getStringExtra(INTENT_OBJECT);
		int ramAvailable = getIntent().getIntExtra(INTENT_RAM_AVAILABLE, 0);
		try {
			cpuRenderer = new MetricViewSurfaceView(this, getApp().getCount(), getApp().getPeriod(), 100000, cpuMetrics, vmgr.getVBox().getPerformanceCollector().getMetrics(cpuMetrics, object).get(0));
			ramRenderer = new MetricViewSurfaceView(this, getApp().getCount(), getApp().getPeriod(), ramAvailable*1000, ramMetrics, vmgr.getVBox().getPerformanceCollector().getMetrics(ramMetrics, object).get(0));
    		
    		LinearLayout cpuLayout = new LinearLayout(this);
    		cpuLayout.setOrientation(LinearLayout.VERTICAL);
    		TextView cpuText = new TextView(this);
    		cpuText.setText("CPU");
    		cpuText.setTextSize(16.f);
    		cpuLayout.addView(cpuText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    		params.weight=1.f;
    		cpuLayout.addView(cpuRenderer, params);
    		createMetricTextFields(cpuLayout, cpuMetrics);
    		
    		LinearLayout ramLayout = new LinearLayout(this);
    		ramLayout.setOrientation(LinearLayout.VERTICAL);
    		TextView ramText = new TextView(this);
    		ramText.setText("Memory");
    		ramText.setTextSize(16.f);
    		ramLayout.addView(ramText, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    		ramLayout.addView(ramRenderer, params);
    		createMetricTextFields(ramLayout, ramMetrics);
    		
    		LinearLayout contentView = new LinearLayout(this);
    		contentView.setOrientation(LinearLayout.VERTICAL);
    		params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    		params.weight=.5f;
    		contentView.addView(cpuLayout, params);
    		contentView.addView(ramLayout, params);
    		setContentView(contentView);
			_thread = new DataThread(vmgr, object, getApp().getCount(), getApp().getPeriod(), (DataThread.Renderer)cpuRenderer, (DataThread.Renderer)ramRenderer);
			_thread.start();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	protected void createMetricTextFields(LinearLayout parent, String[] metrics) {
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		for(String m : metrics) {
			TextView textView = new TextView(this);
			textView.setText(m);
			textView.setTextColor(getApp().getColor(m));
			textView.setPadding(0,0,8,0);
			ll.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		}
		parent.addView(ll, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
	}
	
	@Override
	protected void onDestroy() {
		if(_thread!=null)
			_thread.quit();
		super.onDestroy();
	}
	
	public VBoxApplication getApp() {  
		return (VBoxApplication)getApplication();  
	}
}
