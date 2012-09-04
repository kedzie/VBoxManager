package com.kedzie.vbox.metrics;

import java.lang.Thread.State;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.MetricPreferencesActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.Utils;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Activity to view metric graphs for Virtual Machine or Host
 * @author Marek Kedzierski
 */
public class MetricActivity extends SherlockActivity  {
	private static final int REQUEST_CODE_PREFS = 1;

	public static final String INTENT_TITLE="t",INTENT_OBJECT = "o",
			INTENT_RAM_AVAILABLE = "ra", INTENT_RAM_METRICS="rm",
			INTENT_CPU_METRICS="cm";

	private MetricView cpuV, ramV;
	private DataThread _thread;
	private VBoxSvc _vmgr;
	private String _object;
	private int _ramAvailable;
	private int _count;
	private int _period;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setTitle(getIntent().getStringExtra(INTENT_TITLE));
		
		_vmgr = getIntent().getParcelableExtra(VBoxSvc.BUNDLE);
		_object = getIntent().getStringExtra(INTENT_OBJECT);
		_ramAvailable = getIntent().getIntExtra(INTENT_RAM_AVAILABLE, 0);
		_count = Utils.getIntPreference(getApplicationContext(), MetricPreferencesActivity.COUNT);
		_period = Utils.getIntPreference(getApplicationContext(), MetricPreferencesActivity.PERIOD);
		
		setContentView(R.layout.metrics);
		cpuV = (MetricView) findViewById(R.id.cpu_metrics);
		cpuV.init(100, getIntent().getStringArrayExtra(INTENT_CPU_METRICS));
		ramV = (MetricView) findViewById(R.id.ram_metrics);
		ramV.init( _ramAvailable*1000, getIntent().getStringArrayExtra(INTENT_RAM_METRICS));
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_CODE_PREFS) {
			_count = Utils.getIntPreference(getApplicationContext(), MetricPreferencesActivity.COUNT);
			_period = Utils.getIntPreference(getApplicationContext(), MetricPreferencesActivity.PERIOD);
			cpuV.setMetricPrefs(_count, _period);
			ramV.setMetricPrefs(_count, _period);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.metrics_options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.metrics_option_menu_preferences:
			startActivityForResult(new Intent(this, MetricPreferencesActivity.class), REQUEST_CODE_PREFS);
			return true;
		}
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		_thread = new DataThread(_vmgr, _object, _period, cpuV, ramV);
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
}
