package com.kedzie.vbox.task;

import android.content.Context;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IMachine;

public enum Action {
	Start("Start", R.drawable.ic_list_start) {
		@Override public IMachine execute(Context ctx, VBoxSvc vmgr, IMachine m) {
			return m;
		}
	},
	Pause("Pause", R.drawable.ic_list_pause) {
		@Override public IMachine execute(Context ctx, VBoxSvc vmgr, IMachine m) {
			return m;
		}
	}, 
	Resume("Resume", R.drawable.ic_list_start) {
		@Override public IMachine execute(Context ctx, VBoxSvc vmgr, IMachine m) {
			return m;
		}
	},
	Reset("Reset", R.drawable.ic_list_reset) {
		@Override public IMachine execute(Context ctx, VBoxSvc vmgr, IMachine m) {
			return m;
		}
	},
	PowerOff("Power Off", R.drawable.ic_list_poweroff) {
		@Override public IMachine execute(Context ctx, VBoxSvc vmgr, IMachine m) {
			return m;
		}
	};
	
	private String _name;
	private int _resID;
	Action(String name, int resID) { _name=name; _resID=resID; 	}
	public String getText() { return _name; }
	public int getDrawable() { return _resID; }
	public abstract IMachine execute(Context ctx, VBoxSvc vmgr, IMachine m);
}
