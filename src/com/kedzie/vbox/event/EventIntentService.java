package com.kedzie.vbox.event;

import java.io.IOException;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.kedzie.vbox.R;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IEventListener;
import com.kedzie.vbox.api.IEventSource;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMachineEvent;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.machine.MachineListFragmentActivity;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Polls VirtualBox for events and publishes them in local broadcasts.
 */
public class EventIntentService extends IntentService {
	private static final String TAG = EventIntentService.class.getSimpleName();
	
	private static final int NOTIFICATION_ID = 749;
	private static final int DEFAULT_INTERVAL = 500;
	public static final String BUNDLE_EVENT = "evt";
	public static final String INTENT_INTERVAL="interval";
	
	private VBoxSvc _vmgr;
	private int _interval;
	private boolean _running=true;
	private LocalBroadcastManager _lbm;
	private IEventSource evSource;
	private IEventListener listener;
	
	public EventIntentService() {
		super("VirtualBox Event Handler");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		_lbm = LocalBroadcastManager.getInstance(getApplicationContext());
		_vmgr = intent.getParcelableExtra(VBoxSvc.BUNDLE);
		_interval = intent.getIntExtra(INTENT_INTERVAL, DEFAULT_INTERVAL);
//		sendNotification();
		
		IEvent event = null;
		evSource =  _vmgr.getVBox().getEventSource();
		listener = evSource.createListener();
		evSource.registerListener(listener, new VBoxEventType [] { VBoxEventType.ANY }, false);
		while(_running) {
			try {
				if((event=evSource.getEvent(listener, 0))!=null) {
					BundleBuilder bundle = new BundleBuilder().putProxy(BUNDLE_EVENT, event);
					if(event instanceof IMachineEvent)
						bundle.putProxy(IMachine.BUNDLE,  _vmgr.getVBox().findMachine(((IMachineEvent)event).getMachineId()));
					_lbm.sendBroadcast(new Intent(event.getType().name()).putExtras(bundle.create()));
					evSource.eventProcessed(listener, event); 
				} else if(_running)
				 	Thread.sleep(_interval);
			} catch (Throwable e) {
				Log.e(TAG, "Error", e);
			} 
		}
	}
	
	/**
	 * Create notification notifying user that events are being handled in real-time.
	 */
    private void sendNotification() {
	    String title = getString(R.string.event_handler_notification_title);
	    Notification notification =  new NotificationCompat.Builder(EventIntentService.this)
            .setContentTitle(title)
            .setContentText(getString(R.string.event_handler_notification_content, _vmgr.getServer().toString()))
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_notif_vbox)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
            .setContentIntent(PendingIntent.getActivity(EventIntentService.this, 0, new Intent(this, MachineListFragmentActivity.class).putExtra(VBoxSvc.BUNDLE, (Parcelable)_vmgr), 0))
            .setTicker(title)
            .setAutoCancel(false)
            .build();
	    ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);
	}
	
	@Override
	public void onDestroy() {
//	    ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIFICATION_ID);
		if(_running) {
			_running=false;
			Log.w(TAG, "Service is still in running state onDestroy!");
		}
		new Thread() {
			@Override
			public void run() {
				try { 
					if(evSource!=null)
					evSource.unregisterListener(listener);	
				} catch(IOException e) {}
			}
		}.start();
		super.onDestroy();
	}
}
