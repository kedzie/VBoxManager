package com.kedzie.vbox.machine;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.PreferencesActivity;
import com.kedzie.vbox.Utils;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Listen for VirtualBox events and publish notifications
 */
public class EventService extends Service {
	protected static final String TAG = EventService.class.getSimpleName();
	public static final String com_virtualbox_EVENT = "com.virtualbox.EVENT";
	
	protected EventThread _eventThread;
	protected VBoxSvc _vmgr;
	
	protected LocalBroadcastManager lbm;
	
	protected Messenger statusBarNotificationListener = new Messenger(new Handler() {
		protected static final int NOTIFICATION_ID = 1;
		@Override
		public void handleMessage(Message msg) {
			IEvent event = BundleBuilder.getProxy(msg.getData(), EventThread.BUNDLE_EVENT, IEvent.class);
			Log.i(TAG, "Status Bar Notification Listener recieved event: " + event);
			lbm.sendBroadcast(new Intent(com_virtualbox_EVENT).putExtras(msg.getData()));
			if(!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(PreferencesActivity.NOTIFICATIONS, false)) 
				return;
//			if(event instanceof IMachineStateChangedEvent) {
//				String text = "VirtualBox event: " + event.getType();
//				IMachine eventMachine = BundleBuilder.getProxy(msg.getData(), IMachine.BUNDLE, IMachine.class);
//				String description = eventMachine.getName() + " changed State:  "  + eventMachine.getState();
//				Intent intent = new Intent(EventService.this, MachineFragmentActivity.class).putExtra("vmgr", (Parcelable)_vmgr);
//				BundleBuilder.addProxy(intent, "machine", eventMachine);
//				Notification notification = new Notification(R.drawable.ic_list_vbox, text, System.currentTimeMillis());
//				notification.setLatestEventInfo(getApplicationContext(), text, description, PendingIntent.getActivity(EventService.this, 0, intent, 0));
//				((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);
//			}
		}
	});
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		_vmgr = intent.getParcelableExtra(VBoxSvc.BUNDLE);
		 lbm = LocalBroadcastManager.getInstance(getApplicationContext());
		_eventThread = new EventThread("Notification", _vmgr);
		_eventThread.addListener(statusBarNotificationListener);
		_eventThread.start();
		Utils.toast(this, "Starting Event Notifier Service");
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Utils.toast(this, "Shutting down VirtualBox Event Notifier Service");
		_eventThread.quit();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
