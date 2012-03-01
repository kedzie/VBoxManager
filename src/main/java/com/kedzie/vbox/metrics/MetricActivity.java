package com.kedzie.vbox.metrics;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kedzie.vbox.MetricPreferencesActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ConfigureMetricsTask;

/**
 * Activity to view metric graphs for Virtual Machine or Host
 * 
 * @author Marek Kedzierski
 */
public class MetricActivity extends Activity  {
	private static final String TAG = MetricActivity.class.getSimpleName();
	private static final int REQUEST_CODE_PREFERENCES = 6;
	public static final String INTENT_TITLE="title";
	public static final String INTENT_OBJECT = "object";
	public static final String INTENT_RAM_AVAILABLE = "ram_available";
	public static final String INTENT_RAM_METRICS="ram_metrics";
	public static final String INTENT_CPU_METRICS="cpu_metrics";
	public static final String INTENT_IMPLEMENTATION="implementation";
	
	private View cpuRenderer, ramRenderer;
	private DataThread _thread;
	private VBoxSvc _vmgr;
	
	/** Metric View component implementations */
	public enum Implementation { SURFACEVIEW, OPENGL; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_vmgr = getIntent().getParcelableExtra(VBoxSvc.BUNDLE);
		setTitle(getIntent().getStringExtra(INTENT_TITLE));
		String []cpuMetrics = getIntent().getStringArrayExtra(INTENT_CPU_METRICS);
		String [] ramMetrics = getIntent().getStringArrayExtra(INTENT_RAM_METRICS);
		String object = getIntent().getStringExtra(INTENT_OBJECT);
		int ramAvailable = getIntent().getIntExtra(INTENT_RAM_AVAILABLE, 0);
		try {
			Implementation _i = Implementation.valueOf(getIntent().getStringExtra(INTENT_IMPLEMENTATION));
			if(_i.equals(Implementation.SURFACEVIEW)) {
				cpuRenderer = new MetricViewSurfaceView(this, getApp().getCountPreference(), getApp().getPeriodPreference(), 100000, cpuMetrics, _vmgr.getVBox().getPerformanceCollector().getMetrics(cpuMetrics, object).get(0));
				ramRenderer = new MetricViewSurfaceView(this, getApp().getCountPreference(), getApp().getPeriodPreference(), ramAvailable*1000, ramMetrics, _vmgr.getVBox().getPerformanceCollector().getMetrics(ramMetrics, object).get(0));
			} else if (_i.equals(Implementation.OPENGL)) {
				cpuRenderer = new MetricViewGL(this, getApp().getCountPreference(), getApp().getPeriodPreference(), 100000, cpuMetrics, _vmgr.getVBox().getPerformanceCollector().getMetrics(cpuMetrics, object).get(0));
				ramRenderer = new MetricViewGL(this, getApp().getCountPreference(), getApp().getPeriodPreference(), ramAvailable*1000, ramMetrics, _vmgr.getVBox().getPerformanceCollector().getMetrics(ramMetrics, object).get(0));
			}
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
			_thread = new DataThread(_vmgr, object, getApp().getCountPreference(), getApp().getPeriodPreference(), (DataThread.Renderer)cpuRenderer, (DataThread.Renderer)ramRenderer);
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
		if(_thread!=null) _thread.quit();
		super.onDestroy();
	}
	
	public VBoxApplication getApp() {  
		return (VBoxApplication)getApplication();  
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.metrics_options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.metrics_option_menu_preferences:
			startActivityForResult(new Intent(this, MetricPreferencesActivity.class),REQUEST_CODE_PREFERENCES);
			return true;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==REQUEST_CODE_PREFERENCES) {
			new ConfigureMetricsTask(this, _vmgr).execute(getApp().getPeriodPreference());
			_thread.setPeriod(getApp().getPeriodPreference());
			((DataThread.Renderer)cpuRenderer).setMetricPreferences(getApp().getPeriodPreference(), getApp().getCountPreference());
			((DataThread.Renderer)ramRenderer).setMetricPreferences(getApp().getPeriodPreference(), getApp().getCountPreference());
		}
	}
}
