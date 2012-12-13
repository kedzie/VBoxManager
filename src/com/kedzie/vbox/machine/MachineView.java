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
 * 
 * @author Marek Kędzierski
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
	    _machine=m;
		osIcon.setImageResource(_app.getOSDrawable(m.getOSTypeId()));
		nameText.setText(m.getName());
		stateIcon.setImageResource( _app.getDrawable(m.getState()) );
		stateText.setText(m.getState().value());
		if(m.getCurrentSnapshot()!=null)  
			snapshotText.setText("("+m.getCurrentSnapshot().getName() + ")" + (m.getCurrentStateModified() ? "*" : ""));
		else 
			snapshotText.setText("");
	}

	/**
	 * Cache commonly used Machine properties
	 * @param m
	 */
	public static void cacheProperties(IMachine m) {
		m.clearCache();
		m.getName();
		m.getState();
		m.getCurrentStateModified(); 
		m.getOSTypeId();
		if(m.getCurrentSnapshot()!=null) 
			m.getCurrentSnapshot().getName();
	}
	
	public IMachine getMachine() {
	    return _machine;
	}
}
