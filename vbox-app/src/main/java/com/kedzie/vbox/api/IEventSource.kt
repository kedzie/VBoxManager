package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.VBoxEventType
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap
interface IEventSource : IManagedObjectRef, Parcelable {

    suspend fun createListener(): IEventListener

    suspend fun registerListener(listener: IEventListener, interesting: Array<VBoxEventType>, active: Boolean)

    suspend fun unregisterListener(listener: IEventListener)

    suspend fun eventProcessed(listener: IEventListener,event: IEvent)

    suspend fun getEvent(listener: IEventListener, @Ksoap(type = "int") timeout: Int): IEvent

    suspend fun createAggregator(vararg subordinates: IEventSource): IEventSource

    suspend fun handleEvent(event: IEvent)

    suspend fun fireEvent(event: IEvent, @Ksoap(type = "unsignedInt") timeout: Int): Boolean
}
