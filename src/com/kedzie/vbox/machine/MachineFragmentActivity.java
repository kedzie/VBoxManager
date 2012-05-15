package com.kedzie.vbox.machine;

import android.os.Bundle;

import com.kedzie.vbox.TabActivity;

public class MachineFragmentActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState==null) {
			addTab("Actions", ActionsFragment.getInstance(getIntent().putExtra("dualPane", false).getExtras()));
			addTab("Info", InfoFragment.getInstance(getIntent().getExtras()));
			addTab("Log", LogFragment.getInstance(getIntent().getExtras()));
			addTab("Snapshots", SnapshotFragment.getInstance(getIntent().getExtras()));
		}
	}
}
