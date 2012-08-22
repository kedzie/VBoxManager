package com.kedzie.vbox.metrics;

import java.lang.Thread.State;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kedzie.vbox.PreferencesActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.Utils;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Activity to view metric graphs for Virtual Machine or Host
 * @author Marek Kedzierski
 */
public class MetricActivity extends SherlockActivity  {
	public static final String INTENT_TITLE="t",INTENT_OBJECT = "o",
			INTENT_RAM_AVAILABLE = "ra", INTENT_RAM_METRICS="rm",
			INTENT_CPU_METRICS="cm";

	private MetricView cpuV, ramV;
	private DataThread _thread;
	private VBoxSvc _vmgr;
	private String _object;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getIntent().getStringExtra(INTENT_TITLE));
		_vmgr = getIntent().getParcelableExtra(VBoxSvc.BUNDLE);
		_object = getIntent().getStringExtra(INTENT_OBJECT);
		int ramAvailable = getIntent().getIntExtra(INTENT_RAM_AVAILABLE, 0);
		setContentView(R.layout.metrics);
		cpuV = (MetricView) findViewById(R.id.cpu_metrics);
		cpuV.init("CPU", 100000, getIntent().getStringArrayExtra(INTENT_CPU_METRICS));
		ramV = (MetricView) findViewById(R.id.ram_metrics);
		ramV.init("Memory", ramAvailable*1000, getIntent().getStringArrayExtra(INTENT_RAM_METRICS));
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
			startActivity(new Intent(this, MetricPreferencesActivity.class));
			return true;
		default:
			return true;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		_thread = new DataThread(_vmgr, _object, Utils.getIntPreference(this, PreferencesActivity.PERIOD), cpuV, ramV);
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
