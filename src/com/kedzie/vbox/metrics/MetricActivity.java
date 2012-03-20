package com.kedzie.vbox.metrics;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.kedzie.vbox.MetricPreferencesActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ConfigureMetricsTask;

/**
 * Activity to view metric graphs for Virtual Machine or Host
 * @author Marek Kedzierski
 */
public class MetricActivity extends Activity  {
	private static final String TAG = MetricActivity.class.getSimpleName();
	private static final int REQUEST_CODE_PREFERENCES = 6;
	public static final String INTENT_TITLE="t",INTENT_OBJECT = "o",
			INTENT_RAM_AVAILABLE = "ra", INTENT_RAM_METRICS="rm",
			INTENT_CPU_METRICS="cm",INTENT_IMPLEMENTATION="i";
	
	private MetricView cpuV, ramV;
	private DataThread _thread;
	private VBoxSvc _vmgr;
	private String _object;
	
	/** Metric View component implementations */
	public enum Implementation { SURFACEVIEW, OPENGL; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_vmgr = getIntent().getParcelableExtra(VBoxSvc.BUNDLE);
		setTitle(getIntent().getStringExtra(INTENT_TITLE));
		String []cpuMetrics = getIntent().getStringArrayExtra(INTENT_CPU_METRICS);
		String [] ramMetrics = getIntent().getStringArrayExtra(INTENT_RAM_METRICS);
		_object = getIntent().getStringExtra(INTENT_OBJECT);
		int ramAvailable = getIntent().getIntExtra(INTENT_RAM_AVAILABLE, 0);
		Implementation _i = Implementation.valueOf(getIntent().getStringExtra(INTENT_IMPLEMENTATION));
		try {
			cpuV = new MetricView(this, "CPU", _i, 100000, cpuMetrics, _vmgr.getVBox().getPerformanceCollector().getMetrics(cpuMetrics, _object).get(0));
			ramV = new MetricView(this,"Memory", _i, ramAvailable*1000, ramMetrics, _vmgr.getVBox().getPerformanceCollector().getMetrics(ramMetrics, _object).get(0));
			LinearLayout contentView = new LinearLayout(this);
    		contentView.setOrientation(LinearLayout.VERTICAL);
    		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    		params.weight=.5f;
    		contentView.addView(cpuV, params);
    		contentView.addView(ramV, params);
    		setContentView(contentView);
			
			startDataThread();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}
	
	private void startDataThread() {
		_thread = new DataThread(_vmgr, _object, 
				VBoxApplication.getPeriodPreference(this), 
				cpuV, 
				ramV);
		_thread.start();
	}
	
	@Override
	protected void onDestroy() {
		if(_thread!=null) _thread.quit();
		super.onDestroy();
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
			new ConfigureMetricsTask(this, _vmgr) {
				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					_thread.setPeriod(VBoxApplication.getPeriodPreference(MetricActivity.this));
					cpuV.setMetricPreferences(
							VBoxApplication.getPeriodPreference(MetricActivity.this), 
							VBoxApplication.getCountPreference(MetricActivity.this));
					ramV.setMetricPreferences(
							VBoxApplication.getPeriodPreference(MetricActivity.this), 
							VBoxApplication.getCountPreference(MetricActivity.this));
				}
			}.execute(VBoxApplication.getPeriodPreference(this));
		}
	}

	@Override
	protected void onPause() {
		if(_thread!=null) _thread.quit();
		cpuV.pause();
		ramV.pause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		startDataThread();
		cpuV.resume();
		ramV.resume();
		super.onResume();
	}
}
