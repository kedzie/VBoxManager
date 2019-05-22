package com.kedzie.vbox.api

import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap
interface ISnapshotDeletedEvent : ISnapshotEvent