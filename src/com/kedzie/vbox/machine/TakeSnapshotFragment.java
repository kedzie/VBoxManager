package com.kedzie.vbox.machine;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.MachineTask;

public class TakeSnapshotFragment extends SherlockDialogFragment {

	private IMachine _machine;
	private VBoxSvc _vmgr;
	private View _view;
	private TextView snapshotName;
	private TextView snapshotDescription;
	
	public static TakeSnapshotFragment getInstance(Bundle args) {
		TakeSnapshotFragment f = new TakeSnapshotFragment();
		f.setArguments(args);
		return f;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setTitle("New Snapshot");
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_vmgr = BundleBuilder.getProxy(getArguments(), VBoxSvc.BUNDLE, VBoxSvc.class);
		_machine = BundleBuilder.getProxy(getArguments(), IMachine.BUNDLE, IMachine.class);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		_view = inflater.inflate(R.layout.snapshot_dialog, null);
		snapshotName = (TextView)_view.findViewById(R.id.snapshot_name);
		snapshotDescription = (TextView)_view.findViewById(R.id.snapshot_description);
		((ImageButton)_view.findViewById(R.id.button_save)).setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
				new MachineTask<Void, Void>("TakeSnapshotTask", getActivity(), _vmgr, "Taking Snapshot", false, _machine) {	
					protected IProgress workWithProgress(IMachine m, IConsole console, Void...i) throws Exception { 	
						return console.takeSnapshot( snapshotName.getText().toString(),  snapshotDescription.getText().toString()); 
					}
				}.execute();	
			}
		});
		((ImageButton)_view.findViewById(R.id.button_cancel)).setOnClickListener(new View.OnClickListener() { 
			public void onClick(View v) { 
				dismiss(); 
			} 
		});
		return _view;
	}
}