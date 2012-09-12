package com.kedzie.vbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Create notifications from Machine State change events
 * @author Marek Kędzierski
 */
public class EventNotificationReceiver extends BroadcastReceiver {
	private static final String TAG = "EventNotificationReceiver";
	
	private VBoxSvc _vmgr;
	
	public EventNotificationReceiver(VBoxSvc svc) {
		_vmgr = new VBoxSvc(svc);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "Recieved Broadcast: " + intent.getAction());
		if(intent.getAction().equals(VBoxEventType.ON_MACHINE_STATE_CHANGED.name())) {
			if(!Utils.getBooleanPreference(context, PreferencesActivity.NOTIFICATIONS)) 
				return;
			context.startService(new Intent(context, EventNotificationService.class).putExtras(intent).putExtra(VBoxSvc.BUNDLE, _vmgr));
		}
	}
}
