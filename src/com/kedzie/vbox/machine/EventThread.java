package com.kedzie.vbox.machine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Messenger;
import android.util.Log;

import com.kedzie.vbox.BundleBuilder;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IEventListener;
import com.kedzie.vbox.api.IEventSource;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMachineEvent;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.task.LoopingThread;

/**
 * Listen for & dispatch VirtualBox events
 */
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
