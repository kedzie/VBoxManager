package com.kedzie.vbox.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kedzie.vbox.SettingsActivity;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.app.Utils;

import timber.log.Timber;

/**
 * Create notifications from Machine State change events
 * @author Marek Kędzierski
 */
public class EventNotificationReceiver extends BroadcastReceiver {
	private static final String TAG = "EventNotificationReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Timber.i("Recieved Broadcast: " + intent.getAction());
		if(intent.getAction().equals(VBoxEventType.ON_MACHINE_STATE_CHANGED.name())
				&& Utils.getBooleanPreference(context, SettingsActivity.PREF_NOTIFICATIONS)) 
			context.startService(new Intent(context, EventNotificationService.class).putExtras(intent));
	}
}
