package com.kedzie.vbox.machine;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;

import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.common.MetricActivity;

public class MachineTabActivity extends TabActivity  {
	
	private VBoxSvc _vmgr;
	private IMachine _machine;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tabs);
	    _vmgr =getIntent().getParcelableExtra("vmgr");
	    _machine = _vmgr.getProxy(IMachine.class, getIntent().getStringExtra("machine"));
	    getTabHost().addTab(getTabHost().newTabSpec("actions").setIndicator("Actions", getResources().getDrawable(R.drawable.ic_tab_actions)).setContent(
	    		new Intent(this, MachineActivity.class).putExtras(getIntent())));
	    getTabHost().addTab(getTabHost().newTabSpec("info").setIndicator("Info", getResources().getDrawable(R.drawable.ic_tab_info)).setContent(
	    		new Intent(this, MachineInfoActivity.class).putExtras(getIntent())));
	    getTabHost().addTab(getTabHost().newTabSpec("snapshots").setIndicator("Snapshots",	getResources().getDrawable(R.drawable.ic_tab_snapshots)).setContent(
	    		new Intent(this, SnapshotActivity.class).putExtras(getIntent())));
	    getTabHost().addTab(getTabHost().newTabSpec("metrics").setIndicator("Metrics", getResources().getDrawable(R.drawable.ic_tab_metrics)).setContent(
	    		new Intent(this, MetricActivity.class).putExtras(getIntent())
		    		.putExtra(MetricActivity.INTENT_RAM_AVAILABLE, _machine.getMemorySize())
					.putExtra(MetricActivity.INTENT_OBJECT, _machine.getIdRef() )
					.putExtra(MetricActivity.INTENT_CPU_METRICS , new String[] { "Guest/CPU/Load/User", "Guest/CPU/Load/Kernel" } )
					.putExtra(MetricActivity.INTENT_RAM_METRICS , new String[] {  "Guest/RAM/Usage/Shared", "Guest/RAM/Usage/Cache" } )));
	}
}
