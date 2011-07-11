package com.kedzie.vbox.util;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.kedzie.vbox.R;

public class Resources {
	private static final String TAG = "MachineListActivity";
	public static Map<String,Integer> r = new HashMap<String, Integer>();
	
	static {
		r.put("state_running", R.drawable.ic_list_state_running);
		r.put("state_poweredoff", R.drawable.ic_list_state_poweredoff);
		r.put("state_paused", R.drawable.ic_list_state_paused);
//		r.put("state_saving", R.drawable.state_saving);
//		r.put("state_saved", R.drawable.state_saved);
//		r.put("state_aborted", R.drawable.state_aborted);
//		r.put("state_stuck", R.drawable.state_stuck);
		r.put("os_ubuntu", R.drawable.ic_list_os_ubuntu);
		r.put("os_ubuntu_64", R.drawable.ic_list_os_ubuntu_64);
		r.put("os_mac_os", R.drawable.ic_list_os_mac_os);
		r.put("os_mac_os_64", R.drawable.ic_list_os_mac_os_64);
		
	}
	
	public static int get(String name) {
		try {
			Object stateIcon = R.drawable.class.getField(name).get(new R.drawable());
			Log.i(TAG, "Reflected Icon: " + stateIcon);
		} catch (Exception e) {
			Log.e(TAG, "Resource error", e);
		} 
		return r.get(name);
	}
}
