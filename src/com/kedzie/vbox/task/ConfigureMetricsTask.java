package com.kedzie.vbox.task;

import android.content.Context;

import com.kedzie.vbox.PreferencesActivity;
import com.kedzie.vbox.Utils;
import com.kedzie.vbox.api.IManagedObjectRef;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Configure VirtualBox metric collecting parameters
 * @param period Metric collection interval
 * @author Marek Kedzierski
 */
public class ConfigureMetricsTask extends BaseTask<Integer, Void> {
	
	public ConfigureMetricsTask(Context ctx, VBoxSvc vmgr) { 
		super( "ConfigureMetricsTask", ctx, vmgr, "Configuring Metrics");
	}

	@Override
	protected Void work(Integer... params) throws Exception {
		//if no period is specified then default to preferences
		_vmgr.getVBox().getPerformanceCollector().setupMetrics(new String[] { "*:" }, 
				Utils.isNullArray(params)  ? Utils.getIntPreference(context, PreferencesActivity.PERIOD) : params[0], 
						1, (IManagedObjectRef)null);
		return null;
	}
}
