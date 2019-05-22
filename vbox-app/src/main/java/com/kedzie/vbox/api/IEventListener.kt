package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap
interface IEventListener : IManagedObjectRef, Parcelable {

    suspend fun handleEvent(event: IEvent)
}