package com.kedzie.vbox;


import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.virtualbox_4_1.MachineState;

import android.app.Application;

import com.kedzie.vbox.server.ServerDB;

public class VBoxApplication extends Application {

	private ServerDB _db;
	
	@Override
	public void onCreate() {
		super.onCreate();
		_db = new ServerDB(this);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public ServerDB getDB() { return _db;	}
	
	public static Map<String,Integer> r = new HashMap<String, Integer>();
	public static Map<MachineState,Integer> s = new HashMap<MachineState, Integer>();
	
	static {
		s.put(MachineState.Running, R.drawable.ic_list_start_small);
		s.put(MachineState.Starting, R.drawable.ic_list_start_small);
		s.put(MachineState.Stopping, R.drawable.ic_list_poweroff_small);
		s.put(MachineState.PoweredOff, R.drawable.ic_list_poweroff_small);
		s.put(MachineState.Paused, R.drawable.ic_list_pause_small);
		s.put(MachineState.LiveSnapshotting, R.drawable.ic_list_save_small);
		s.put(MachineState.DeletingSnapshot, R.drawable.ic_list_save_small);
		s.put(MachineState.DeletingSnapshotOnline, R.drawable.ic_list_save_small);
		s.put(MachineState.DeletingSnapshotPaused, R.drawable.ic_list_save_small);
		s.put(MachineState.RestoringSnapshot, R.drawable.ic_list_save_small);
		s.put(MachineState.Saving, R.drawable.ic_list_save_small);
		s.put(MachineState.Saved, R.drawable.ic_list_save_small);
		s.put(MachineState.Restoring, R.drawable.ic_list_start_small);
		s.put(MachineState.Aborted, R.drawable.ic_list_abort_small);
		s.put(MachineState.Stuck, R.drawable.ic_list_stuck_small);
		
		r.put( "Start", R.drawable.ic_list_start );
		r.put("Power Off", R.drawable.ic_list_poweroff);
		r.put("Pause", R.drawable.ic_list_pause);
		r.put("Reset", R.drawable.ic_list_reset);
		r.put("Power Button", R.drawable.ic_list_acpi );
		r.put("Save State", R.drawable.ic_list_save);
		r.put("Discard State", R.drawable.ic_list_save);
		r.put("Take Snapshot", R.drawable.ic_list_save);
				
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
		r.put("os_debian_64", R.drawable.ic_list_os_debian_64);
		r.put("os_debian", R.drawable.ic_list_os_debian);
		r.put("os_windowsxp_64", R.drawable.ic_list_os_winxp_64);
		r.put("os_windowsxp", R.drawable.ic_list_os_winxp);
		r.put("os_windows7_64", R.drawable.ic_list_os_win7_64);
		r.put("os_windows7", R.drawable.ic_list_os_win7);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Annotation> T getAnnotation(Class<T> clazz, Annotation []a) {
		for(Annotation at : a)
			if(at.annotationType().equals(clazz)) return (T)at;
		return null;
	}
	
public String[] getActions(MachineState state) {
		if(state.equals(MachineState.Running)) {
			return new String[] { "Pause", "Reset", "Power Off" , "Save State", "Power Button", "Take Snapshot" };
		} else if (state.equals(MachineState.PoweredOff) || state.equals(MachineState.Aborted)){
			return new String[] { "Start",  "Take Snapshot"  };
		} else if (state.equals(MachineState.Paused)){
			return new String[] { "Resume", "Reset", "Power Off" };
		} else if (state.equals(MachineState.Saved)) {
			return new String[] { "Restore State", "Discard State" };
		} 
		return new String[] {};
	}
	
	public static int get(String name) {
		return r.containsKey(name) ? r.get(name) : R.drawable.ic_list_os_linux;
	}
	
	public static int get(MachineState state) {
		return s.containsKey(state)  ? s.get(state) : R.drawable.ic_list_start_small;
	}
}
