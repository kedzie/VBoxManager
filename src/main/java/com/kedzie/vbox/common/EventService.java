package com.kedzie.vbox.common;

import android.R;
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
import android.util.Log;

import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMachineStateChangedEvent;
import com.kedzie.vbox.machine.MachineListActivity;
import com.kedzie.vbox.machine.MachineTabActivity;

/**
 * Listen for VirtualBox events and publish notifications
 */
public class EventService extends Service {
	protected static final String TAG = EventService.class.getSimpleName();
	
	protected EventThread _eventThread;
	protected VBoxSvc _vmgr;
	/** Event listener for Status Bar Notifications */
	protected Messenger statusBarNotificationListener = new Messenger(new Handler() {
		protected static final int NOTIFICATION_ID = 1;
		@Override
		public void handleMessage(Message msg) {
			IEvent event = _vmgr.getProxy(IEvent.class, msg.getData().getString("evt"));
			Log.i(TAG, "Status Bar Notification Listener recieved event: " + event);
			if(event instanceof IMachineStateChangedEvent) {
				IMachine eventMachine = _vmgr.getProxy(IMachine.class, msg.getData().getString("machine"));
				Intent machineActivityIntent = new Intent(EventService.this, MachineTabActivity.class).putExtra("vmgr", _vmgr).putExtra("machine", msg.getData().getString("machine")); 
				Notification notification = new Notification(R.drawable.ic_list_vbox, "VirtualBox Event", System.currentTimeMillis());
				notification.setLatestEventInfo(getApplicationContext(), "VirtualBox notification", "VirtualBox Event!", PendingIntent.getActivity(EventService.this, 0, machineActivityIntent, 0));
				((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);
			}
		}
	});
	
	@Override
	public IBinder onBind(Intent intent) {
		_vmgr=(VBoxSvc)intent.getParcelableExtra("vmgr");
		_eventThread = new EventThread(_vmgr);
		_eventThread.addListener(statusBarNotificationListener);
		_eventThread.start();
		return new LocalBinder();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(TAG, "Destroying Event Service");
        _eventThread.quit();
		return super.onUnbind(intent);
	}
	
	/**
	 * Subscribe event listener
	 * @param m event listener
	 */
	public void addListener(Messenger m) {
		_eventThread.addListener(m);
	}
	
	/**
	 * Unsubscribe event listener
	 * @param m event listener
	 */
	public void removeListener(Messenger m) {
		_eventThread.removeListener(m);
	}
	
	public  class LocalBinder extends Binder {
		public EventService getLocalBinder() { return EventService.this; }
	}
}
