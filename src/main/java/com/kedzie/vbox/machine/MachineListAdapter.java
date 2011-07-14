package com.kedzie.vbox.machine;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kedzie.vbox.R;
import com.kedzie.vbox.Resources;
import com.kedzie.vbox.api.IMachine;

public class MachineListAdapter extends BaseAdapter {
//	private final static String TAG = MachineListAdapter.class.getName();
	
	private List<IMachine> _machines;
	private final LayoutInflater _layoutInflater;
	
	public MachineListAdapter(MachineListActivity context, List<IMachine> machines) {
		_machines = machines;
		_layoutInflater = LayoutInflater.from(context);
	}

	public void setMachines(List<IMachine> m) {
		this._machines=m;
		notifyDataSetChanged();
	}
	
	public int getCount() {
		return _machines.size();
	}

	public IMachine getItem(int position) {
		return _machines.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		IMachine m = getItem(position);
		if (view == null) {
			view = _layoutInflater.inflate(R.layout.machine_list_item, parent, false);
			((ImageView)view.findViewById(R.id.machine_list_item_ostype)).setImageResource(Resources.get("os_"+m.getOSTypeId().toLowerCase()));
			((TextView) view.findViewById(R.id.machine_list_item_name)).setText(m.getName());			
		}
		String state = m.getState();
		((ImageView)view.findViewById(R.id.machine_list_item_state)).setImageResource( Resources.get("state_"+state.toLowerCase()) );
		((TextView)view.findViewById(R.id.machine_list_item_state_text)).setText(state);
		return view;
	}
}
