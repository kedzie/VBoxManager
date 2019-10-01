package com.kedzie.vbox.task;

import com.kedzie.vbox.SettingsActivity;
import com.kedzie.vbox.api.IManagedObjectRef;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.soap.VBoxSvc;

import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;

public class ConfigureMetricsTask extends BaseTask<Integer, Void> {
	
	public ConfigureMetricsTask(AppCompatActivity ctx, VBoxSvc vmgr) {
		super(ctx, vmgr);
	}

	@Override
	protected Void work(Integer... params) throws Exception {
		int count=0, period=0;
		if(Utils.isEmpty(params)) {
			count = Utils.getIntPreference(getContext(), SettingsActivity.PREF_COUNT);
			period = Utils.getIntPreference(getContext(), SettingsActivity.PREF_PERIOD);
		} else {
			period = params[0];
			count = params[1];
		}
		Timber.i("Configuring metrics: period = %d, count = %d", period, count);
		_vmgr.getVBox().getPerformanceCollector().setupMetrics(new String[] { "*:" }, period, count, (IManagedObjectRef)null);
		return null;
	}
}