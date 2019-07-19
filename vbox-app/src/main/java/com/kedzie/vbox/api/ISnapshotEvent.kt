package com.kedzie.vbox.api

import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap(prefix="ISnapshotEvent")
interface ISnapshotEvent : IMachineEvent {

    @Cacheable("snapshotId")
	suspend fun getSnapshotId(): String
}