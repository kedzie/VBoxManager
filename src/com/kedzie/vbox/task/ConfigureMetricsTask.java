package com.kedzie.vbox.task;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kedzie.vbox.PreferencesActivity;
import com.kedzie.vbox.Utils;
import com.kedzie.vbox.api.IManagedObjectRef;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Configure VirtualBox metric collecting parameters
 * @param period Metric collection interval
 * @author Marek Kedzierski
 */
public class ConfigureMetricsTask extends ActionBarTask<Void, Void> {
	
	public ConfigureMetricsTask(SherlockFragmentActivity ctx, VBoxSvc vmgr) { 
		super( "ConfigureMetricsTask", ctx, vmgr);
	}

	@Override
	protected Void work(Void...v) throws Exception {
		_vmgr.getVBox().getPerformanceCollector().setupMetrics(new String[] { "*:" }, 
				Utils.getIntPreference(_context, PreferencesActivity.PERIOD), 
				Utils.getIntPreference(_context, PreferencesActivity.COUNT), 
				(IManagedObjectRef)null);
		return null;
	}
}
