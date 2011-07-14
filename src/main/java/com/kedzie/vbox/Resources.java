package com.kedzie.vbox;

import java.util.HashMap;
import java.util.Map;

import com.kedzie.vbox.R;

public class Resources {
	public static Map<String,Integer> r = new HashMap<String, Integer>();
	
	static {
		r.put("state_running", R.drawable.ic_list_start_small);
		r.put("state_starting", R.drawable.ic_list_start_small);
		r.put("state_restoring", R.drawable.ic_list_start_small);
		r.put("state_stopping", R.drawable.ic_list_poweroff_small);
		r.put("state_poweredoff", R.drawable.ic_list_poweroff_small);
		r.put("state_paused", R.drawable.ic_list_pause_small);
		r.put("state_saving", R.drawable.ic_list_state_save);
		r.put("state_saved", R.drawable.ic_list_state_save);
		r.put("state_aborted", R.drawable.ic_list_abort_small);
		r.put("state_stuck", R.drawable.ic_list_state_stuck);
		
		r.put("os_ubuntu", R.drawable.ic_list_os_ubuntu);
		r.put("os_ubuntu_64", R.drawable.ic_list_os_ubuntu_64);
		r.put("os_macos", R.drawable.ic_list_os_macos);
		r.put("os_macos_64", R.drawable.ic_list_os_macos_64);
		r.put("os_linux", R.drawable.ic_list_os_linux);
		r.put("os_linux24_64", R.drawable.ic_list_os_linux24_64);
		r.put("os_linux24", R.drawable.ic_list_os_linux24);
		r.put("os_linux26_64", R.drawable.ic_list_os_linux26_64);
		r.put("os_linux26", R.drawable.ic_list_os_linux26);
		r.put("os_redhat_64", R.drawable.ic_list_os_redhat_64);
		r.put("os_redhat", R.drawable.ic_list_os_redhat);
		r.put("os_winxp_64", R.drawable.ic_list_os_winxp_64);
		r.put("os_winxp", R.drawable.ic_list_os_winxp);
		r.put("os_winxp_64", R.drawable.ic_list_os_win7_64);
		r.put("os_winxp", R.drawable.ic_list_os_win7);
	}
	
	public static int get(String name) {
		if (!r.containsKey(name) && name.startsWith("os_")) //default OS 
			return R.drawable.ic_list_os_linux;
		return r.get(name);
	}
}
