package com.kedzie.vbox.machine;

import java.io.IOException;
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
import com.kedzie.vbox.task.BaseThread;

/**
 * Listen for & dispatch VirtualBox events
 */
public class EventThread extends BaseThread {
	protected static final String TAG = EventThread.class.getSimpleName();
	public static final int WHAT_EVENT = 1;
	public static final String BUNDLE_EVENT = "evt";
	public static final String BUNDLE_MACHINE = "machine";
	
	protected int _interval;
	protected VBoxEventType[] _eventTypes;
	protected List<Messenger> _listeners = new ArrayList<Messenger>();
	protected VBoxSvc _vmgr;
	protected IEvent event ;
	protected IEventSource evSource ;
	protected IEventListener listener ;
	
	/**
	 * @param name				Thread name
	 * @param vmgr 				VirtualBox API
	 * @param interval 		polling interval
	 * @param events 			which events to subscribe to
	 */
	public EventThread(String name, VBoxSvc vmgr, int interval, VBoxEventType...events) {
		super(name + " Event Handler");
		_vmgr=new VBoxSvc(vmgr);
		_eventTypes=events;
		_interval=interval;
	}
	
	/**
	 * @param name				Thread name
	 * @param vmgr 				VirtualBox API
	 * @param events 			which events to subscribe to
	 */
	public EventThread(String name, VBoxSvc vmgr, VBoxEventType...events) {
		this(name, vmgr, 500, events);
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
		evSource =  _vmgr.getVBox().getEventSource();
		listener = evSource.createListener();
		evSource.registerListener(listener, _eventTypes, false);
	}
	
	@Override
	public void postExecute() {
		try { 
			evSource.unregisterListener(listener);	
		} catch(IOException e) {}
	}
	
	@Override
	public void loop() {
				try {
					if((event=evSource.getEvent(listener, 0))!=null) {
						Log.d(TAG, "Got Event: " + event.getType());
						BundleBuilder bundle = new BundleBuilder().putProxy(BUNDLE_EVENT, event);
						if(event instanceof IMachineEvent)
							bundle.putProxy(BUNDLE_MACHINE,  _vmgr.getVBox().findMachine(((IMachineEvent)event).getMachineId()));
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
