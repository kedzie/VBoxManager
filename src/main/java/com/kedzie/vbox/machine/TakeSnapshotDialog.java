package com.kedzie.vbox.machine;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.ISnapshot;
import com.kedzie.vbox.task.MachineTask;

/**
 * Dialog to take a new {@link ISnapshot}
 */
public class TakeSnapshotDialog extends Dialog implements View.OnClickListener {

	private IMachine _machine;
	private VBoxSvc _vmgr;
	private TextView snapshotName;
	private TextView snapshotDescription;
	private Button saveButton;
	private Button cancelButton;
	
	public TakeSnapshotDialog(Context context, VBoxSvc api, IMachine m) {
		super(context);
		_machine=m;
		_vmgr = api;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.snapshot_dialog);
		snapshotName = (TextView)findViewById(R.id.snapshot_name);
		snapshotDescription = (TextView)findViewById(R.id.snapshot_description);
		saveButton = (Button)findViewById(R.id.button_save);
		cancelButton = (Button)findViewById(R.id.button_cancel);
		saveButton.setOnClickListener(this);
		cancelButton.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { dismiss(); } });
	}

	@Override
	public void onClick(View v) {
		dismiss();
		new MachineTask<Void>("TakeSnapshotTask", getContext(), _vmgr, "Taking Snapshot", false, _machine) {	
			protected IProgress workWithProgress(IMachine m, IConsole console, Void...i) throws Exception { 	
				return console.takeSnapshot( snapshotName.getText().toString(),  snapshotDescription.getText().toString()); 
			}
		}.execute();		
	}
}
