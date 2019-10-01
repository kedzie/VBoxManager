package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.MachineState
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap(prefix="IMachineStateChangedEvent")
interface IMachineStateChangedEvent : IMachineEvent {

    @Cacheable("state")
    suspend fun getState(): MachineState
}
