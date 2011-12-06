package com.kedzie.vbox.common;

import java.util.ArrayList;
import java.util.List;

import android.os.Messenger;
import android.util.Log;

import com.kedzie.vbox.VBoxApplication.BundleBuilder;
import com.kedzie.vbox.VBoxSvc;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IEventListener;
import com.kedzie.vbox.api.IEventSource;
import com.kedzie.vbox.api.IMachineEvent;
import com.kedzie.vbox.api.jaxb.VBoxEventType;

/**
 * Listen for & dispatch VirtualBox events
 */
public class EventThread extends Thread {
	protected static final String TAG = EventThread.class.getSimpleName();
	public static final int WHAT_EVENT = 1;
	public static final String BUNDLE_EVENT = "evt", BUNDLE_MACHINE = "machine";
	public static final int DEFAULT_INTERVAL = 300;
	
	protected int _interval=DEFAULT_INTERVAL;
	protected boolean _running=true;
	protected List<Messenger> _listeners = new ArrayList<Messenger>();
	protected VBoxSvc _vmgr;
	
	public EventThread(VBoxSvc vmgr) {
		super("Event Listener");
		_vmgr=new VBoxSvc(vmgr);  //create a copy for thread-safety
	}
	
	@Override
	public void run() {
		IEvent event = null;
		IEventSource evSource =  _vmgr.getVBox().getEventSource();
		IEventListener listener = evSource.createListener();
		evSource.registerListener(listener, new VBoxEventType [] { VBoxEventType.MACHINE_EVENT }, false);
			while(_running) {
				try {
					if((event=evSource.getEvent(listener, 0))!=null) {
						Log.d(TAG, "Got Event: " + event.getType());
						BundleBuilder bundle = new BundleBuilder().putString("evt", event.getIdRef());
						if(event instanceof IMachineEvent) 
							bundle.putString("machine",  _vmgr.getVBox().findMachine(((IMachineEvent)event).getMachineId()).getIdRef());
						if(_listeners.isEmpty())
							wait();
						synchronized(_listeners) {
							if(!_listeners.isEmpty() && _running) {
								for(Messenger messenger : _listeners)
									bundle.sendMessage(messenger, WHAT_EVENT);
							}
						}
						evSource.eventProcessed(listener, event); 
					} else {
					 	sleep(_interval);
					}
				} catch (Throwable e) {
					Log.e(TAG, "Error", e);
				} 
			}
		evSource.unregisterListener(listener);
	}
	
	/**
	 * Add an event listener
	 * @param m Event Listener
	 */
	public void addListener(Messenger m) {
		synchronized (_listeners) {
			Log.i(TAG, "Subscribing event listener: " + m);
			_listeners.add(m);
			if(_listeners.size()==1) //if the thread is waiting for a listener
				notify();
		}
	}
	
	/**
	 * Remove an event listener
	 * @param m Event Listener
	 */
	public void removeListener(Messenger m) {
		synchronized(_listeners) {
			Log.i(TAG, "Removing event listener: " + m);
			_listeners.remove(m);
		}
	}
	
	public void setRunning(boolean r) {
		_running=r;
		if(_listeners.isEmpty()) //if thread is waiting for a listener, unblock to let it finish
			notify();
	}
	
	/**
	 * @param i milliseconds between calls to get events
	 */
	public void setInterval(int i) {
		_interval=i;
	}
	
	/**
	 * Nicely shuts down the thread
	 */
	public void quit() {
		boolean done = false;
        _running=false;
        if(_listeners.isEmpty()) //if thread is waiting for a listener, unblock to let it finish
        	notify();
        while (!done) {
            try {
                join();
                done = true;
            } catch (InterruptedException e) { }
        }
	}
}
