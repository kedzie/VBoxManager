package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.VBoxEventType
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

/**
 * Abstract parent interface for VirtualBox events.
 *
 * Actual events will typically implement a more specific interface which derives from this (see below).
 *
 * **Introduction to VirtualBox events**
 *
 * Generally speaking, an event (represented by this interface) signals that something happened, while an event listener (see [IEventListener]) represents an entity that is interested in certain events.
 * In order for this to work with unidirectional protocols (i.e. web services), the concepts of passive and active listener are used.
 *
 *
 * Event consumers can register themselves as listeners, providing an array of events they are interested in (see [IEventSource.registerListener]). When an event triggers, the listener is notified about the
 * event. The exact mechanism of the notification depends on whether the listener was registered as an active or passive listener:
 *
 *
 *  * An active listener is very similar to a callback: it is a function invoked by the API. As opposed to the callbacks that were used in the API before VirtualBox 4.0 however, events are now objects with an interface hierarchy.
 *  * Passive listeners are somewhat trickier to implement, but do not require a client function to be callable, which is not an option with scripting languages or web service clients. Internally the [IEventSource]
 * implementation maintains an event queue for each passive listener, and newly arrived events are put in this queue. When the listener calls [IEventSource.getEvent]****, first element from its internal event
 * queue is returned. When the client completes processing of an event, the [IEventSource.eventProcessed] function must be called, acknowledging that the event was processed. It supports implementing
 * *waitable* events. On passive listener unregistration, all events from its queue are auto-acknowledged.
 *
 *
 * Waitable events are useful in situations where the event generator wants to track delivery or a party wants to wait until all listeners have completed the event. A typical example would be a vetoable event (see [IVetoEvent])
 * where a listeners might veto a certain action, and thus the event producer has to make sure that all listeners have processed the event and not vetoed before taking the action.
 *
 *
 * A given event may have both passive and active listeners at the same time.
 *
 * **Using events**
 *
 *
 * Any VirtualBox object capable of producing externally visible events provides an `eventSource` read-only attribute, which is of the type [IEventListener]**** interface must be provided. For active
 * listeners, such an object is typically created by the consumer, while for passive listeners [IEventSource.createListener]**** should be used. Please note that a listener created with [IEventSource.createListener]
 * must not be used as an active listener.
 *
 *
 * Once created, the listener must be registered to listen for the desired events (see [VBoxEventType]**** enums. Those elements can either be the individual event IDs or wildcards matching multiple event IDs.
 *
 *
 * After registration, the callback's [IEventListener.handleEvent]**** method is called automatically when the event is triggered, while passive listeners have to call [IEventSource.getEvent]**** and
 * [IEventSource.eventProcessed]**** in an event processing loop.
 *
 *
 * The [type]**** attribute of the event and then cast to the appropriate specific interface using `QueryInterface()`.
 *
 *
 */
@Ksoap(prefix = "IEvent")
@KsoapProxy
interface IEvent : IManagedObjectRef {

    @Cacheable("type")
    suspend fun getType(): VBoxEventType

    @Cacheable("source")
    suspend fun getSource(): IEventSource

    @Cacheable("waitable")
    suspend fun getWaitable(): Boolean

    suspend fun waitProcessed(@Ksoap(type = "unsignedInt") timeout: Int): Boolean

    suspend fun setProcessed(): Boolean
}
