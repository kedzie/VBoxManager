package com.kedzie.vbox.api.jaxb

import android.os.Parcelable
import com.kedzie.vbox.soap.KsoapObject
import kotlinx.android.parcel.Parcelize

@KsoapObject("IGuestOSType")
@Parcelize
data class IGuestOSType(
    var familyId: String,
    var familyDescription: String,
    var id: String,
    var description: String,
    var isIs64Bit: Boolean,
    var isRecommendedIOAPIC: Boolean ,
    var isRecommendedVirtEx: Boolean,
    var recommendedRAM: Long,
    var recommendedGraphicsController: GraphicsControllerType,
    var recommendedVRAM: Long,
    var isRecommended2DVideoAcceleration: Boolean,
    var isRecommended3DAcceleration: Boolean,
    var recommendedHDD: Long,
    var adapterType: NetworkAdapterType,
    var isRecommendedPAE: Boolean,
    var recommendedDVDStorageController: StorageControllerType,
    var recommendedDVDStorageBus: StorageBus,
    var recommendedHDStorageController: StorageControllerType,
    var recommendedHDStorageBus: StorageBus,
    var recommendedFirmware: FirmwareType,
    var isRecommendedUSBHID: Boolean,
    var isRecommendedHPET: Boolean ,
    var isRecommendedUSBTablet: Boolean,
    var isRecommendedRTCUseUTC: Boolean,
    var recommendedChipset: ChipsetType,
    var recommendedAudioController: AudioControllerType,
    var recommendedAudioCodec: AudioCodecType,
    var isRecommendedFloppy: Boolean ,
    var isRecommendedUSB: Boolean,
    var isRecommendedUSB3: Boolean,
    var isRecommendedTFReset: Boolean ,
    var isRecommendedX2APIC: Boolean) : Parcelable