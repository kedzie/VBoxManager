package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.*
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap
interface ISystemProperties : IManagedObjectRef, Parcelable {

    @Cacheable("MinGuestRAM")
	suspend fun getMinGuestRAM(): Int
    @Cacheable("MaxGuestRAM")
	suspend fun getMaxGuestRAM(): Int

    @Cacheable("MinGuestVRAM")
	suspend fun getMinGuestVRAM(): Int
    @Cacheable("MaxGuestVRAM")
	suspend fun getMaxGuestVRAM(): Int

    @Cacheable("MinGuestCPUCount")
	suspend fun getMinGuestCPUCount(): Int
    @Cacheable("MaxGuestCPUCount")
	suspend fun getMaxGuestCPUCount(): Int

    @Cacheable("MaxGuestMonitors")
	suspend fun getMaxGuestMonitors(): Int

    @Cacheable("MaxBootPosition")
	suspend fun getMaxBootPosition(): Int

    @Cacheable("WebServiceAuthLibrary")
	suspend fun getWebServiceAuthLibrary(): String
    suspend fun setWebServiceAuthLibrary(@Cacheable("webServiceAuthLibrary") webServiceAuthLibrary: String)

    @Cacheable("VRDEAuthLibrary")
	suspend fun getVRDEAuthLibrary(): String;
    suspend fun setVRDEAuthLibrary(@Cacheable("VRDEAuthLibrary") VRDEAuthLibrary: String)

    @Cacheable("DefaultAudioDriver")
	suspend fun getDefaultAudioDriver(): AudioDriverType

    @Cacheable("MediumFormats")
	suspend fun getMediumFormats(): IMediumFormat

    suspend fun getMaxNetworkAdapters(chipset: ChipsetType): Int
    suspend fun getMaxNetworkAdapters(chipset: ChipsetType, type: NetworkAttachmentType): Int

    suspend fun getMaxDevicesPerPortForStorageBus(bus: StorageBus): Int
    suspend fun getMinPortCountForStorageBus(bus: StorageBus): Int
    suspend fun getMaxPortCountForStorageBus(bus: StorageBus): Int
    suspend fun getMaxInstancesOfStorageBus(chipset: ChipsetType, bus: StorageBus): Int
    suspend fun getDeviceTypesForStorageBus(bus: StorageBus): List<DeviceType>
}