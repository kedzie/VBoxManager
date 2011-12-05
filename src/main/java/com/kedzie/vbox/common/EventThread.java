package com.kedzie.vbox.common;

import android.os.Messenger;
import android.util.Log;

import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.VBoxApplication.BundleBuilder;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IEventListener;
import com.kedzie.vbox.api.IEventSource;
import com.kedzie.vbox.api.IMachineEvent;
import com.kedzie.vbox.api.jaxb.VBoxEventType;

/**
 * Listen to & publish VirtualBox events
 */
public class EventThread extends Thread {
	protected static final String TAG = EventThread.class.getSimpleName();
	public static final int WHAT_EVENT = 1;

	/** Event polling interval */
	private static final int INTERVAL = 300;
	/** Event loop is running */
	public boolean _running=true;
	/** Event listener */
	private Messenger _messenger;
	/** VirtualBox API */
	private VBoxSvc _vmgr;
	
	public EventThread(VBoxSvc vmgr) {
		super("Event Listener");
		_vmgr=new VBoxSvc(vmgr);  //create a copy for thread-safety
	}
	
	@Override
	public void run() {
		IEventSource evSource = _vmgr.getVBox().getEventSource();
		IEventListener listener = evSource.createListener();
		IEvent event = null;
		try {
			evSource.registerListener(listener, new VBoxEventType [] { VBoxEventType.MACHINE_EVENT }, false);
			while(_running) {
					if((event=evSource.getEvent(listener, 0))!=null) {
						Log.d(TAG, "Got Event: " + event.getType());
						BundleBuilder b = new BundleBuilder().putString("evt", event.getIdRef());
						if(event instanceof IMachineEvent) b.putString("machine",  _vmgr.getVBox().findMachine(((IMachineEvent)event).getMachineId()).getIdRef());
						synchronized(EventService.class) {
							if(_messenger==null)
								wait();
							if(_messenger!=null && _running)
								b.sendMessage(_messenger, WHAT_EVENT);
						}
						evSource.eventProcessed(listener, event); 
					} else
						sleep(INTERVAL);
			}
		} catch (Throwable e) {
			Log.e(TAG, "Error", e);
		} 
		finally {
			try {
				if(listener!=null && evSource!=null) 
					evSource.unregisterListener(listener);
			} catch (Throwable e) {}
		}
	}
	
	/**
	 * Add an event listener
	 * @param m Event Listener
	 */
	public synchronized void addListener(Messenger m) {
		Log.i(TAG, "Setting event listener: " + m);
		_messenger = m;
		notify();
	}
}
