package com.kedzie.vbox.metrics;

import java.io.IOException;
import java.lang.Thread.State;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.kedzie.vbox.MetricPreferencesActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.Utils;
import com.kedzie.vbox.api.IPerformanceCollector;
import com.kedzie.vbox.metrics.MetricView.Implementation;
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
			IPerformanceCollector pc = _vmgr.getVBox().getPerformanceCollector();
			cpuV = new MetricView(this, "CPU", _i, 100000, cpuMetrics, pc.getMetrics(cpuMetrics, _object).get(0));
			ramV = new MetricView(this,"Memory", _i, ramAvailable*1000, ramMetrics, pc.getMetrics(ramMetrics, _object).get(0));

			LinearLayout contentView = new LinearLayout(this);
    		contentView.setOrientation(LinearLayout.VERTICAL);
    		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    		params.weight=.5f;
    		contentView.addView(cpuV, params);
    		contentView.addView(ramV, params);
    		setContentView(contentView);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
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
					cpuV.setMetricPreferences(
							Utils.getPeriodPreference(MetricActivity.this),
							Utils.getCountPreference(MetricActivity.this));
					ramV.setMetricPreferences(
							Utils.getPeriodPreference(MetricActivity.this),
							Utils.getCountPreference(MetricActivity.this));
				}
			}.execute(Utils.getPeriodPreference(this));
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		_thread = new DataThread(_vmgr, _object, Utils.getPeriodPreference(this), cpuV, ramV);
		_thread.start();
	}	
	
	@Override 
	protected void onStop() {
		if(_thread!=null){
			if(_thread.getState().equals(State.TIMED_WAITING))
				_thread.interrupt();
			_thread.quit();
		}
		super.onStop();
	}
	
	@Override
	protected void onPause() {
		cpuV.pause();
		ramV.pause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		cpuV.resume();
		ramV.resume();
		super.onResume();
	}
}
