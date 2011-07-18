package com.kedzie.vbox.machine;

import com.kedzie.vbox.VBoxApplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MachineActionAdapter extends ArrayAdapter<String> {
	private final LayoutInflater _layoutInflater;
	private final int layoutId;
	private final int textResourceId;
	private final int iconResourceId;
	
	public MachineActionAdapter(Context context, int id, int textResourceId, int iconResourceId, String []strings) {
		super(context, id, textResourceId, strings);
		_layoutInflater = LayoutInflater.from(context);
		this.layoutId=id;
		this.textResourceId=textResourceId;
		this.iconResourceId=iconResourceId;
	}

	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			view = _layoutInflater.inflate(this.layoutId, parent, false);
			((TextView)view.findViewById(textResourceId)).setText(getItem(position));
			((ImageView)view.findViewById(iconResourceId)).setImageResource( VBoxApplication.get(getItem(position)));
		}
		return view;
	}
}
