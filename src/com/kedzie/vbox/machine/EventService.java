package com.kedzie.vbox.machine;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;

import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.PreferencesActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.Utils;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMachineStateChangedEvent;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Listen for VirtualBox events and publish notifications
 */
public class EventService extends Service {
	protected static final String TAG = EventService.class.getSimpleName();
	
	protected EventThread _eventThread;
	protected VBoxSvc _vmgr;
	protected Messenger statusBarNotificationListener = new Messenger(new Handler() {
		protected static final int NOTIFICATION_ID = 1;
		@Override
		public void handleMessage(Message msg) {
			if(!isNotificationEnabled()) 
				return;
			IEvent event = BundleBuilder.getProxy(msg.getData(), EventThread.BUNDLE_EVENT, IEvent.class);
			Log.i(TAG, "Status Bar Notification Listener recieved event: " + event);
			String text = "VirtualBox event: " + event.getType();
			String description = "";
			Intent intent = null;
			PendingIntent pending = null;
			if(event instanceof IMachineStateChangedEvent) {
				IMachineStateChangedEvent me = (IMachineStateChangedEvent)event;
				IMachine eventMachine = BundleBuilder.getProxy(msg.getData(), IMachine.BUNDLE, IMachine.class);
				intent = new Intent(EventService.this, MachineFragmentActivity.class).putExtra("vmgr", (Parcelable)_vmgr);
				BundleBuilder.addProxy(intent, "machine", eventMachine);
				description = eventMachine.getName() + " changed State:  "  + me.getState();
				Notification notification = new Notification(R.drawable.ic_list_vbox, text, System.currentTimeMillis());
				if(intent!=null) 
					pending = PendingIntent.getActivity(EventService.this, 0, intent, 0);
				notification.setLatestEventInfo(getApplicationContext(), text, description, pending);
				((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);
			}
		}
	});
	
	/**
	 * Check Shared Preferences if Event Notifications are enabled.
	 * @return true if notifications are enabled, false otherwise
	 */
	protected boolean isNotificationEnabled() {
		return PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(PreferencesActivity.NOTIFICATIONS, false);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		_vmgr = intent.getParcelableExtra(VBoxSvc.BUNDLE);
		_eventThread = new EventThread("Notification", _vmgr);
		_eventThread.addListener(statusBarNotificationListener);
		_eventThread.start();
		Utils.toast(this, "Starting Event Notifier Service");
		return new LocalBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Utils.toast(this, "Shutting down VirtualBox Event Notifier Service");
		_eventThread.quit();
		return super.onUnbind(intent);
	}
	
	public  class LocalBinder extends Binder {
		public EventService getLocalBinder() { return EventService.this; }
	}
}
