package com.kedzie.vbox.task;

import android.content.Context;

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
		_vmgr.getVBox().getPerformanceCollector().setupMetrics(new String[] { "*:" }, params[0], 1, (IManagedObjectRef)null);
		return null;
	}
}
