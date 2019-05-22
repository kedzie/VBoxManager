package com.kedzie.vbox.api.jaxb

import android.os.Parcelable
import com.kedzie.vbox.api.IMedium
import com.kedzie.vbox.soap.KsoapObject
import kotlinx.android.parcel.Parcelize

@KsoapObject("IMediumAttachment")
@Parcelize
data class IMediumAttachment(
        var medium: IMedium,
        var controller: String?,
        var deviceType: DeviceType,
        var passthrough: Boolean,
        var temporaryEject: Boolean,
        var isEjected: Boolean,
        var nonRotational: Boolean,
        var discard: Boolean,
        var hotPluggable: Boolean,
        var bandwidthGroup: String,
        var port: Int,
        var device: Int) : Parcelable {

    var slot: Slot
        get() = Slot(port, device)
        set(value) {
            port = value.port
            device = value.device
        }
}

/**
 * Attachment location and optional name *only used for IDE*
 */
@Parcelize
data class Slot(var port: Int,
           var device: Int,
           var name: String? = null)
    : Parcelable