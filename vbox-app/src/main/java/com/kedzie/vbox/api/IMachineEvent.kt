package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap(prefix = "IMachineEvent")
interface IMachineEvent : IEvent {

    @Cacheable("machineId")
    suspend fun getMachineId(): String
}
