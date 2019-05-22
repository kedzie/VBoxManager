package com.kedzie.vbox.api.jaxb

import android.os.Parcelable
import com.kedzie.vbox.soap.KsoapObject
import kotlinx.android.parcel.Parcelize

@KsoapObject("IAdditionsFacility")
@Parcelize
data class IAdditionsFacility(
    var classType: AdditionsFacilityClass,
    var lastUpdated: Long,
    var name: String,
    var status: AdditionsFacilityStatus,
    var type: AdditionsFacilityType) : Parcelable