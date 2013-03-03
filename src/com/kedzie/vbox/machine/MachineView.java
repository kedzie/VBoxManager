package com.kedzie.vbox.machine;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IMachine;

/**
 * Show VM information
 * 
 * @apiviz.stereotype view
 */
public class MachineView extends FrameLayout {

    private VBoxApplication _app;
	private ImageView osIcon;
	private ImageView stateIcon;
	private TextView stateText;
	private TextView nameText;
	private TextView snapshotText;
	private IMachine _machine;
	
	public MachineView(Context context) {
		super(context);
		_app=VBoxApplication.getInstance();
		LayoutInflater.from(context).inflate(R.layout.machine_view, this, true);
		osIcon = (ImageView)findViewById(R.id.machine_list_item_ostype);
		nameText =(TextView)findViewById(R.id.machine_list_item_name);
		stateIcon = (ImageView)findViewById(R.id.machine_list_item_state);
		stateText = (TextView)findViewById(R.id.machine_list_item_state_text);
		snapshotText =  (TextView)findViewById(R.id.machine_list_item_snapshot);
	}
	
	public void update(IMachine m) {
		synchronized(m) {
			_machine=m;
			osIcon.setImageResource(_app.getOSDrawable(m.getOSTypeId()));
			nameText.setText(m.getName());
			stateIcon.setImageResource( _app.getDrawable(m.getState()) );
			stateText.setText(m.getState().value());
			if(m.getCurrentSnapshot()!=null)  
				snapshotText.setText(new StringBuffer("(").append(m.getCurrentSnapshot().getName()).append(")").append((m.getCurrentStateModified() ? "*" : "")).toString());
			else 
				snapshotText.setText("");
		}
	}

	public IMachine getMachine() {
	    return _machine;
	}
}
