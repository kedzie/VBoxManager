package com.kedzie.vbox.machine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.R;
import com.kedzie.vbox.TabActivity;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.machine.MachineListFragment.SelectMachineListener;
import com.kedzie.vbox.soap.VBoxSvc;

public class MachineListFragmentActivity extends TabActivity implements SelectMachineListener {

	/** Is the dual Fragment Layout active? */
	private boolean _dualPane;
	
	/** VirtualBox API */
	private VBoxSvc _vmgr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_vmgr = (VBoxSvc)getIntent().getParcelableExtra(VBoxSvc.BUNDLE);
		setContentView(R.layout.fragment_layout_support);
		View detailsFrame = findViewById(R.id.details);
		_dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
	}

	@Override
	public void onMachineSelected(IMachine machine) {
		if (_dualPane) {
			removeAllTabs();
			Bundle b = new BundleBuilder()
				.putParcelable(VBoxSvc.BUNDLE, _vmgr)
				.putProxy(IMachine.BUNDLE, machine)
				.putBoolean("dualPane", true)
				.create();
			addTab("Actions", ActionsFragment.getInstance(b), R.id.details);
			addTab("Info", InfoFragment.getInstance(b), R.id.details);
			addTab("Log", LogFragment.getInstance(b), R.id.details);
			addTab("Snapshots", SnapshotFragment.getInstance(b), R.id.details);
		} else {
			Intent intent = new Intent(this, MachineFragmentActivity.class).putExtra(VBoxSvc.BUNDLE, _vmgr);
			BundleBuilder.addProxy(intent, IMachine.BUNDLE, machine );
			startActivity(intent);
		}
	}
}
