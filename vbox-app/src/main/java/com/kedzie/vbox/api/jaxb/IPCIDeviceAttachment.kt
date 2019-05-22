package com.kedzie.vbox.api.jaxb

import android.os.Parcelable
import com.kedzie.vbox.soap.KsoapObject
import kotlinx.android.parcel.Parcelize

@KsoapObject("IPCIDeviceAttachment")
@Parcelize
data class IPCIDeviceAttachment(
    var name: String,
    var isIsPhysicalDevice: Boolean,
    var hostAddress: Int,
    var guestAddress: Int) : Parcelable