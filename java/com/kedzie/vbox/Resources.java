package com.kedzie.vbox;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class Resources {
	private static final String TAG = "VBoxMonitorActivity";
	public static Map<String,Integer> r = new HashMap<String, Integer>();
	
	static {
		r.put("state_running", R.drawable.state_running);
		r.put("state_poweredoff", R.drawable.state_poweredoff);
		r.put("state_paused", R.drawable.state_paused);
		r.put("state_saving", R.drawable.state_saving);
		r.put("state_saved", R.drawable.state_saved);
		r.put("state_aborted", R.drawable.state_aborted);
		r.put("state_stuck", R.drawable.state_stuck);
		r.put("os_osx64", R.drawable.os_virtualbox);
	}
	
	public static int get(String name) {
		try {
			String stateIcon = (String)R.drawable.class.getField(name).get(new R.drawable());
			Log.i(TAG, "Reflected Icon: " + stateIcon);
		} catch (Exception e) {
			Log.e(TAG, "Resource error", e);
		} 
		return r.get(name);
	}
}
