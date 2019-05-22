package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.HostNetworkInterfaceMediumType
import com.kedzie.vbox.api.jaxb.HostNetworkInterfaceStatus
import com.kedzie.vbox.api.jaxb.HostNetworkInterfaceType
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap
interface IHostNetworkInterface : IManagedObjectRef, Parcelable {

    companion object {
        const val BUNDLE = "hostNetwork"
    }

    @Cacheable( "Name")
	suspend fun getName(): String

    @Cacheable("Id")
	suspend fun getId(): String

    @Cacheable("NetworkName")
	suspend fun getNetworkName(): String

    @Cacheable("DHCPEnabled")
	suspend fun getDHCPEnabled(): Boolean

    @Cacheable("IPAddress")
	suspend fun getIPAddress(): String

    @Cacheable("NetworkMask")
	suspend fun getNetworkMask(): String

    @Cacheable("IPV6Supported")
    suspend fun getIPV6Supported(): Boolean

    @Cacheable("IPV6Address")
    suspend fun getIPV6Address() :String

    @Cacheable("IPV6NetworkMaskPrefixLength")
    suspend fun getIPV6NetworkMaskPrefixLength(): Int

    @Cacheable("HardwareAddress")
	suspend fun getHardwareAddress(): String

    @Cacheable("MediumType")
	suspend fun getMediumType(): HostNetworkInterfaceMediumType

    @Cacheable("Status")
	suspend fun getStatus(): HostNetworkInterfaceStatus

    @Cacheable("InterfaceType")
	suspend fun getInterfaceType(): HostNetworkInterfaceType

    @Cacheable("DHCPRediscover")
    suspend fun DHCPRediscover(): Boolean
}