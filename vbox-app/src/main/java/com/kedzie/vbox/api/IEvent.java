package com.kedzie.vbox.api;

import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;

/**
 * Abstract parent interface for VirtualBox events. 
 * <p>Actual events will typically implement a more specific interface which derives from this (see below).
 * <p><b>Introduction to VirtualBox events</b>
 * <p>Generally speaking, an event (represented by this interface) signals that something happened, while an event listener (see {@link IEventListener}) represents an entity that is interested in certain events.
 *  In order for this to work with unidirectional protocols (i.e. web services), the concepts of passive and active listener are used.<p>
 * Event consumers can register themselves as listeners, providing an array of events they are interested in (see {@link IEventSource#registerListener}). When an event triggers, the listener is notified about the 
 * event. The exact mechanism of the notification depends on whether the listener was registered as an active or passive listener:<p>
 * <ul><li>An active listener is very similar to a callback: it is a function invoked by the API. As opposed to the callbacks that were used in the API before VirtualBox 4.0 however, events are now objects with an interface hierarchy.</li>
 * <li>Passive listeners are somewhat trickier to implement, but do not require a client function to be callable, which is not an option with scripting languages or web service clients. Internally the {@link IEventSource} 
 * implementation maintains an event queue for each passive listener, and newly arrived events are put in this queue. When the listener calls {@link IEventSource#getEvent}<b></b>, first element from its internal event 
 * queue is returned. When the client completes processing of an event, the {@link IEventSource#eventProcessed} function must be called, acknowledging that the event was processed. It supports implementing 
 * <em>waitable</em> events. On passive listener unregistration, all events from its queue are auto-acknowledged. </li>
 * </ul>
 * <p>Waitable events are useful in situations where the event generator wants to track delivery or a party wants to wait until all listeners have completed the event. A typical example would be a vetoable event (see {@link IVetoEvent}) 
 * where a listeners might veto a certain action, and thus the event producer has to make sure that all listeners have processed the event and not vetoed before taking the action.<p>
 * A given event may have both passive and active listeners at the same time.
 * <p><b>Using events</b><p>
 * Any VirtualBox object capable of producing externally visible events provides an <code>eventSource</code> read-only attribute, which is of the type {@link IEventListener}<b></b> interface must be provided. For active 
 * listeners, such an object is typically created by the consumer, while for passive listeners {@link IEventSource#createListener}<b></b> should be used. Please note that a listener created with {@link IEventSource#createListener} 
 * must not be used as an active listener.<p>
 * Once created, the listener must be registered to listen for the desired events (see {@link VBoxEventType}<b></b> enums. Those elements can either be the individual event IDs or wildcards matching multiple event IDs.<p>
 * After registration, the callback's {@link IEventListener#handleEvent}<b></b> method is called automatically when the event is triggered, while passive listeners have to call {@link IEventSource#getEvent}<b></b> and 
 * {@link IEventSource#eventProcessed}<b></b> in an event processing loop.<p>
 * The {@link type}<b></b> attribute of the event and then cast to the appropriate specific interface using <code>QueryInterface()</code>.<p>
 */
@KSOAP
public interface IEvent extends IManagedObjectRef, Parcelable {

	static ClassLoader loader = IEvent.class.getClassLoader();
	
	public static final Parcelable.Creator<IEvent> CREATOR = new Parcelable.Creator<IEvent>() {
		public IEvent createFromParcel(Parcel in) {
			VBoxSvc vmgr =  in.readParcelable(loader);
			String id = in.readString();
			Map<String, Object> cache = new HashMap<String, Object>();
			in.readMap(cache, loader);
			return (IEvent) vmgr.getProxy(IEvent.class, id, cache); 
		}
		public IEvent[] newArray(int size) {  
			return new IEvent[size]; 
		}
	};
	
	@KSOAP(cacheable=true, prefix="IEvent") public VBoxEventType getType();
	@KSOAP(prefix="IEvent")	public Boolean waitProcessed(@KSOAP(type="unsignedInt", value="timeout") int timeout);
	@KSOAP(prefix="IEvent")	public boolean setProcessed();
	@KSOAP(cacheable=true, prefix="IEvent")  public IEventSource getSource();
	@KSOAP(cacheable=true, prefix="IEvent")  public boolean getWaitable();
}
