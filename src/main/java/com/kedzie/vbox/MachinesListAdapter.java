package com.kedzie.vbox;

import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.util.Resources;

public class MachinesListAdapter extends BaseAdapter {
	private List<IMachine> _machines;
	private final LayoutInflater _layoutInflater;
	private MachineListActivity ctx;

	public MachinesListAdapter(MachineListActivity context, List<IMachine> machines) {
		this.ctx=context;
		_machines = machines;
		_layoutInflater = LayoutInflater.from(context);
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
			ImageView osTypeImage = (ImageView)view.findViewById(R.id.machine_list_item_ostype);
			try {
				SoapObject obj = new SoapObject(MachineListActivity.NAMESPACE, "IVirtualBox_getGuestOSType");
				obj.addProperty("_this", ctx.vmgr.getVBox ().getId ());
				obj.addProperty("id", m.getOSTypeId());
				SoapObject OSType = ctx.transport.callObject(obj);
				Log.e("VBoxMonitorActivity", "OSType: "+OSType);
			} catch (Exception e) {
				Log.e("VBoxMonitorActivity", "error getting OSType", e);
			} 
		}
		((TextView) view.findViewById(R.id.machine_name)).setText(m.getName()); 
		((ImageView)view.findViewById(R.id.machine_list_item_state)).setImageResource( Resources.get("state_"+m.getState().toLowerCase()) );
		return view;
	}
	
}
