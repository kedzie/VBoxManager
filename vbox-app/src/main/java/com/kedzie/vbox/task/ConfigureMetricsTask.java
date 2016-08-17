package com.kedzie.vbox.task;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.kedzie.vbox.SettingsActivity;
import com.kedzie.vbox.api.IManagedObjectRef;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.soap.VBoxSvc;

public class ConfigureMetricsTask extends BaseTask<Integer, Void> {
	
	public ConfigureMetricsTask(AppCompatActivity ctx, VBoxSvc vmgr) {
		super("ConfigureMetricsTask", ctx, vmgr);
	}

	@Override
	protected Void work(Integer... params) throws Exception {
		int count=0, period=0;
		if(Utils.isEmpty(params)) {
			count = Utils.getIntPreference(getContext(), SettingsActivity.PREF_COUNT);
			period = Utils.getIntPreference(getContext(), SettingsActivity.PREF_PERIOD);
		}
		period=params[0];
		count=params[1];
		Log.i(TAG, String.format("Configuring metrics: period = %d, count = %d", period, count));
		_vmgr.getVBox().getPerformanceCollector().setupMetrics(new String[] { "*:" }, period, count, (IManagedObjectRef)null);
		return null;
	}
}