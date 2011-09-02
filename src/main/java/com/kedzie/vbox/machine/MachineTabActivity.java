package com.kedzie.vbox.machine;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import com.kedzie.vbox.R;
import com.kedzie.vbox.common.MetricActivity;

public class MachineTabActivity extends TabActivity {
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tabs);
	    getTabHost().addTab(getTabHost().newTabSpec("actions").setIndicator("Actions", getResources().getDrawable(R.drawable.ic_tab_actions)).setContent(new Intent(this, MachineInfoActivity.class)));
	    getTabHost().addTab(getTabHost().newTabSpec("info").setIndicator("Info", getResources().getDrawable(R.drawable.ic_tab_info)).setContent(new Intent(this, MachineInfoActivity.class)));
	    getTabHost().addTab(getTabHost().newTabSpec("snapshots").setIndicator("Snapshots",	getResources().getDrawable(R.drawable.ic_tab_snapshots)).setContent(new Intent(this, SnapshotActivity.class)));
	    getTabHost().addTab(getTabHost().newTabSpec("metrics").setIndicator("Metrics", getResources().getDrawable(R.drawable.ic_tab_metrics)).setContent(new Intent(this, MetricActivity.class)));
	    getTabHost().setCurrentTab(0);
	}
	
}
