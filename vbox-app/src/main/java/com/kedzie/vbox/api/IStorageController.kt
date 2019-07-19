package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.StorageBus
import com.kedzie.vbox.api.jaxb.StorageControllerType
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap
interface IStorageController : IManagedObjectRef, Parcelable {
    companion object {
        const val BUNDLE = "storageController"
    }

    @Cacheable("Name")
	suspend fun getName(): String;
    @Cacheable("MaxDevicesPerPortCount")
	suspend fun getMaxDevicesPerPortCount(): Int
    @Cacheable("MinPortCount")
	suspend fun getMinPortCount(): Int
    @Cacheable("MaxPortCount")
	suspend fun getMaxPortCount(): Int
    @Cacheable("Bus")
	suspend fun getBus(): StorageBus;

    @Cacheable("Instance")
	suspend fun getInstance(): Int
    suspend fun setInstance(@Cacheable("Instance") @Ksoap(type = "unsignedInt") instance: Int)

    @Cacheable("PortCount")
	suspend fun getPortCount(): Int
    suspend fun setPortCount(@Cacheable("PortCount") @Ksoap(type = "unsignedInt") portCount: Int)

    @Cacheable("controllerType")
	suspend fun getControllerType(): StorageControllerType;
    suspend fun setControllerType(@Cacheable("controllerType") controllerType: StorageControllerType)

    @Cacheable("useHostIOCache")
	suspend fun getUseHostIOCache(): Boolean;
    suspend fun setUseHostIOCache(@Cacheable("useHostIOCache") useHostIOCache: Boolean)

    @Cacheable("Bootable")
	suspend fun getBootable(): Boolean;
}