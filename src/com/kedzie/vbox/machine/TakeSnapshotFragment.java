package com.kedzie.vbox.machine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.MachineTask;

public class TakeSnapshotFragment extends SherlockDialogFragment {

	protected IMachine _machine;
	protected VBoxSvc _vmgr;
	protected View _view;
	protected TextView snapshotName;
	protected TextView snapshotDescription;
	protected Button saveButton;
	protected Button cancelButton;
	
	public static TakeSnapshotFragment getInstance(Bundle args) {
		TakeSnapshotFragment f = new TakeSnapshotFragment();
		f.setArguments(args);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_vmgr = BundleBuilder.getProxy(getArguments(), VBoxSvc.BUNDLE, VBoxSvc.class);
		_machine = BundleBuilder.getProxy(getArguments(), "machine", IMachine.class);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.snapshot_dialog, null);
		snapshotName = (TextView)_view.findViewById(R.id.snapshot_name);
		snapshotDescription = (TextView)_view.findViewById(R.id.snapshot_description);
		saveButton = (Button)_view.findViewById(R.id.button_save);
		cancelButton = (Button)_view.findViewById(R.id.button_cancel);
		saveButton.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
				new MachineTask<Void>("TakeSnapshotTask", getActivity(), _vmgr, "Taking Snapshot", false, _machine) {	
					protected IProgress workWithProgress(IMachine m, IConsole console, Void...i) throws Exception { 	
						return console.takeSnapshot( snapshotName.getText().toString(),  snapshotDescription.getText().toString()); 
					}
				}.execute();	
			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener() { 
			public void onClick(View v) { 
				dismiss(); 
			} 
		});
		return _view;
	}
}