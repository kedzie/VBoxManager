package com.kedzie.vbox.api

import com.kedzie.vbox.api.jaxb.SessionState
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap
interface ISessionStateChangedEvent : IMachineEvent {
    @Cacheable("State")
	suspend fun getState(): SessionState
}