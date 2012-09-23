package com.kedzie.vbox.task;

import android.content.Context;
import android.util.Log;

import com.kedzie.vbox.api.IManagedObjectRef;
import com.kedzie.vbox.soap.VBoxSvc;

public class ConfigureMetricsTask extends BaseTask<Integer, Void> {
	
	public ConfigureMetricsTask(Context ctx, VBoxSvc vmgr) {
		super("ConfigureMetricsTask", ctx, vmgr);
	}

	@Override
	protected Void work(Integer... params) throws Exception {
		int period=params[0], count=params[1];
		Log.i(TAG, String.format("Configuring metrics: period = %d, count = %d", period, count));
		_vmgr.getVBox().getPerformanceCollector().setupMetrics(new String[] { "*:" }, period, count, (IManagedObjectRef)null);
		return null;
	}
}