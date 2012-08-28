package com.kedzie.vbox.machine;

import java.io.IOException;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IEventListener;
import com.kedzie.vbox.api.IEventSource;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMachineEvent;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.soap.VBoxSvc;

public class EventIntentService extends IntentService {
	private static final String TAG = EventIntentService.class.getSimpleName();
	private static final int DEFAULT_INTERVAL = 500;
	public static final String com_virtualbox_EVENT = "com.virtualbox.EVENT";
	public static final String BUNDLE_EVENT = "evt";
	public static final String INTENT_TYPES = "eventTypes";
	public static final String INTENT_INTERVAL="interval";
	
	private VBoxSvc _vmgr;
	private int _interval;
	private VBoxEventType[] _eventTypes = new VBoxEventType[] { VBoxEventType.MACHINE_EVENT };
	private boolean _running=true;
	private LocalBroadcastManager _lbm;
	
	public EventIntentService() {
		super("VirtualBox Event Handler");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		_lbm = LocalBroadcastManager.getInstance(getApplicationContext());
		_vmgr = intent.getParcelableExtra(VBoxSvc.BUNDLE);
		_interval = intent.getIntExtra(INTENT_INTERVAL, DEFAULT_INTERVAL);
		if(intent.hasExtra(INTENT_TYPES))
			_eventTypes =  (VBoxEventType[]) intent.getSerializableExtra(INTENT_TYPES);
		
		IEvent event = null;
		IEventSource evSource =  _vmgr.getVBox().getEventSource();
		IEventListener listener = evSource.createListener();
		evSource.registerListener(listener, _eventTypes, false);
		while(_running) {
			try {
				if((event=evSource.getEvent(listener, 0))!=null) {
					Log.i(TAG, "Got Event: " + event.getType());
					BundleBuilder bundle = new BundleBuilder().putProxy(BUNDLE_EVENT, event);
					if(event instanceof IMachineEvent)
						bundle.putProxy(IMachine.BUNDLE,  _vmgr.getVBox().findMachine(((IMachineEvent)event).getMachineId()));
					_lbm.sendBroadcast(new Intent(com_virtualbox_EVENT).putExtras(bundle.create()));
					evSource.eventProcessed(listener, event); 
				} else if(_running)
				 	Thread.sleep(_interval);
			} catch (Throwable e) {
				Log.e(TAG, "Error", e);
			} 
		}
		try { 
			evSource.unregisterListener(listener);	
		} catch(IOException e) {}
	}
}
