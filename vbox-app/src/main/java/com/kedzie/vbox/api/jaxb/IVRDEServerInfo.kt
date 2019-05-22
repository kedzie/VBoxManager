package com.kedzie.vbox.api.jaxb

import android.os.Parcelable
import com.kedzie.vbox.soap.KsoapObject
import kotlinx.android.parcel.Parcelize

@Parcelize
@KsoapObject("IVRDEServerInfo")
data class IVRDEServerInfo (var active: Boolean,
                            var port: Int,
                            var numberOfClients: Long,
                            var beginTime: Long,
                            var endTime: Long,
                            var bytesSent: Long,
                            var bytesSentTotal: Long,
                            var bytesReceived: Long,
                            var bytesReceivedTotal: Long,
                            var user: String,
                            var domain: String,
                            var clientName: String,
                            var clientIP: String,
                            var clientVersion: Long,
                            var encryptionStyle: Long) : Parcelable