package com.kedzie.vbox.machine;

import java.io.IOException;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;

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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.PreferencesActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.Utils;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IEventListener;
import com.kedzie.vbox.api.IEventSource;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMachineEvent;
import com.kedzie.vbox.api.IMachineStateChangedEvent;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.LoopingThread;

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
			if(!isNotificationEnabled()) 
				return;
			if(event instanceof IMachineStateChangedEvent) {
				String text = "VirtualBox event: " + event.getType();
				IMachine eventMachine = BundleBuilder.getProxy(msg.getData(), IMachine.BUNDLE, IMachine.class);
				String description = eventMachine.getName() + " changed State:  "  + eventMachine.getState();
				Intent intent = new Intent(EventService.this, MachineFragmentActivity.class).putExtra("vmgr", (Parcelable)_vmgr);
				BundleBuilder.addProxy(intent, "machine", eventMachine);
				Notification notification = new Notification(R.drawable.ic_list_vbox, text, System.currentTimeMillis());
				notification.setLatestEventInfo(getApplicationContext(), text, description, PendingIntent.getActivity(EventService.this, 0, intent, 0));
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
		 lbm = LocalBroadcastManager.getInstance(getApplicationContext());
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
	
	public class EventThread extends LoopingThread {
		protected static final String TAG = EventThread.class.getSimpleName();
		private static final int DEFAULT_INTERVAL = 500;
		public static final int WHAT_EVENT = 1;
		public static final String BUNDLE_EVENT = "evt";
		
		protected int _interval;
		protected VBoxEventType[] _eventTypes;
		protected List<Messenger> _listeners = new ArrayList<Messenger>();
		protected VBoxSvc _vmgr;
		protected IEvent _event ;
		protected IEventSource _evSource ;
		protected IEventListener _listener ;
		
		/**
		 * @param name				Thread name
		 * @param vmgr 				VirtualBox API
		 * @param interval 			polling interval
		 * @param events 			event types to subscribe to
		 */
		public EventThread(String name, VBoxSvc vmgr, int interval, VBoxEventType...events) {
			super(name + " Event Handler");
			_vmgr = new VBoxSvc(vmgr);
			_eventTypes = events;
			_interval = interval;
		}
		
		/**
		 * @param name				Thread name
		 * @param vmgr 				VirtualBox API
		 * @param events 			which events to subscribe to
		 */
		public EventThread(String name, VBoxSvc vmgr, VBoxEventType...events) {
			this(name, vmgr, DEFAULT_INTERVAL, events);
		}
		
		/**
		 * @param name		Thread name
		 * @param vmgr 		VirtualBox API
		 */
		public EventThread(String name, VBoxSvc vmgr) {
			this(name, vmgr, VBoxEventType.MACHINE_EVENT);
		}
		
		@Override
		public void preExecute() {
			_evSource =  _vmgr.getVBox().getEventSource();
			_listener = _evSource.createListener();
			_evSource.registerListener(_listener, _eventTypes, false);
		}
		
		@Override
		public void postExecute() {
			try { 
				_evSource.unregisterListener(_listener);	
			} catch(IOException e) {}
		}
		
		@Override
		public void loop() {
					try {
						if((_event=_evSource.getEvent(_listener, 0))!=null) {
							Log.d(TAG, "Got Event: " + _event.getType());
							BundleBuilder bundle = new BundleBuilder().putProxy(BUNDLE_EVENT, _event);
							if(_event instanceof IMachineEvent)
								bundle.putProxy(IMachine.BUNDLE,  _vmgr.getVBox().findMachine(((IMachineEvent)_event).getMachineId()));
							if(_listeners.isEmpty())
								wait();
							synchronized(_listeners) {
								if(!_listeners.isEmpty() && _running) {
									for(Messenger messenger : _listeners)
										bundle.sendMessage(messenger, WHAT_EVENT);
								}
							}
							_evSource.eventProcessed(_listener, _event); 
						} else {
						 	sleep(_interval);
						}
					} catch (Throwable e) {
						Log.e(TAG, "Error", e);
					} 
		}
		
		/**
		 * Add an event listener
		 * @param m Event Listener
		 */
		public void addListener(Messenger m) {
			synchronized (_listeners) {
				Log.d(TAG, "Subscribing event listener: " + m);
				_listeners.add(m);
				if(_listeners.size()==1 && getState().equals(State.WAITING)) //if the thread is waiting for a listener
					notify();
			}
		}
		
		/**
		 * Remove an event listener
		 * @param m Event Listener
		 */
		public void removeListener(Messenger m) {
			synchronized(_listeners) {
				Log.d(TAG, "Removing event listener: " + m);
				_listeners.remove(m);
			}
		}
	}
}
