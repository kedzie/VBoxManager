package com.kedzie.vbox.machine;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.api.IMachine;

public class MachineView extends LinearLayout {

	ImageView osIcon;
	ImageView stateIcon;
	TextView stateText;
	TextView nameText;
	TextView snapshotText;

	public MachineView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.machine_list_item, this, true);
		osIcon = (ImageView)findViewById(R.id.machine_list_item_ostype);
		nameText =(TextView)findViewById(R.id.machine_list_item_name);
		stateIcon = (ImageView)findViewById(R.id.machine_list_item_state);
		stateText = (TextView)findViewById(R.id.machine_list_item_state_text);
		snapshotText =  (TextView)findViewById(R.id.machine_list_item_snapshot);
	}
	
	public void update(IMachine m) {
		osIcon.setImageResource(VBoxApplication.get("os_"+m.getOSTypeId().toLowerCase()));
		nameText.setText(m.getName());
		stateIcon.setImageResource( VBoxApplication.get(m.getState()) );
		stateText.setText(m.getState().value());
		if(m.getCurrentSnapshot()!=null)  snapshotText.setText("("+m.getCurrentSnapshot().getName() + ")");
	}

}
