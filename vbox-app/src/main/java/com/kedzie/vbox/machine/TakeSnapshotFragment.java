package com.kedzie.vbox.machine;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IConsole;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.ISnapshot;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.ActionBarTask;
import com.kedzie.vbox.task.MachineTask;
import roboguice.fragment.RoboDialogFragment;

/**
 * Create a new snapshot
 * 
 * @apiviz.stereotype fragment
 */
public class TakeSnapshotFragment extends RoboDialogFragment {
    
	private VBoxSvc _vmgr;
	private IMachine _machine;
	private ISnapshot _snapshot;
	
	private TextView _nameText;
	private TextView _descriptionText;
	
	public static TakeSnapshotFragment getInstance(VBoxSvc vmgr, IMachine machine,ISnapshot snapshot) {
	    BundleBuilder b = new BundleBuilder().putParcelable(VBoxSvc.BUNDLE, vmgr).putProxy(IMachine.BUNDLE, machine);
	    if(snapshot!=null)
	        b.putProxy(ISnapshot.BUNDLE, snapshot);
		TakeSnapshotFragment f = new TakeSnapshotFragment();
		f.setArguments(b.create());
		return f;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setTitle(_snapshot==null ? getResources().getString(R.string.new_snapshot_dialog_title) : getResources().getString(R.string.edit_snapshot_dialog_title));
		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_vmgr = BundleBuilder.getProxy(getArguments(), VBoxSvc.BUNDLE, VBoxSvc.class);
		_machine = BundleBuilder.getProxy(getArguments(), IMachine.BUNDLE, IMachine.class);
		if(getArguments().containsKey(ISnapshot.BUNDLE)) 
		    _snapshot = BundleBuilder.getProxy(getArguments(), "snapshot", ISnapshot.class);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.snapshot_dialog, null);
		_nameText = (TextView)view.findViewById(R.id.snapshot_name);
		_descriptionText = (TextView)view.findViewById(R.id.snapshot_description);
		_descriptionText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
					takeSnapshot();
	            }  
				return false;
			}
		});
		if(_snapshot != null) {
		    _nameText.setText(_snapshot.getName());
		    _descriptionText.setText(_snapshot.getDescription());
		}
		((ImageButton)view.findViewById(R.id.button_save)).setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
				if(_snapshot!=null) {
    				new ActionBarTask<ISnapshot, Void>((AppCompatActivity)getActivity(), _vmgr) {
                        protected Void work(ISnapshot...s) throws Exception {     
                            s[0].setName(_nameText.getText().toString());
                            s[0].setDescription(_descriptionText.getText().toString());
                            return null;
                        }
                    }.execute(_snapshot);    
				} else {
    				takeSnapshot();
				}
			}
		});
		((ImageButton)view.findViewById(R.id.button_cancel)).setOnClickListener(new View.OnClickListener() { 
			public void onClick(View v) { 
				dismiss(); 
			} 
		});
		return view;
	}
	
	private void takeSnapshot() {
		new MachineTask<Void, Void>((AppCompatActivity)getActivity(), _vmgr, R.string.progress_taking_snapshot, false, _machine) {
			protected IProgress workWithProgress(IMachine m, IConsole console, Void...i) throws Exception { 	
				return console.takeSnapshot( _nameText.getText().toString(),  _descriptionText.getText().toString()); 
			}
		}.execute();
	}
}