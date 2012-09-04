package com.kedzie.vbox.task;

import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kedzie.vbox.MetricPreferencesActivity;
import com.kedzie.vbox.Utils;
import com.kedzie.vbox.api.IManagedObjectRef;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Configure VirtualBox metric collecting parameters
 * @param period Metric collection interval
 * @author Marek Kedzierski
 */
public class ConfigureMetricsTask extends ActionBarTask<Integer, Void> {
	
	public ConfigureMetricsTask(SherlockFragmentActivity ctx, VBoxSvc vmgr) { 
		super( "ConfigureMetricsTask", ctx, vmgr);
	}

	@Override
	protected Void work(Integer...v) throws Exception {
		int period = Utils.getIntPreference(_context, MetricPreferencesActivity.PERIOD);
		int count = Utils.getIntPreference(_context, MetricPreferencesActivity.COUNT);
		if(!Utils.isNullArray(v)) {
			period = v[0];
			count = v[1];
		} 
		Log.i(TAG, String.format("Configuring metrics: period = %d, count = %d", period, count));
		_vmgr.getVBox().getPerformanceCollector().setupMetrics(new String[] { "*:" }, period, count, (IManagedObjectRef)null);
		return null;
	}
}
