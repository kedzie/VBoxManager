package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap
interface IDHCPServer : IManagedObjectRef {

    companion object {
        const val BUNDLE = "dhcp"
    }

    @Cacheable(value = "Enabled")
	suspend fun getEnabled(): Boolean

    suspend fun setEnabled(@Cacheable(value = "Enabled") enabled: Boolean)

    @Cacheable(value = "IPAddress")
	suspend fun getIPAddress(): String

    @Cacheable(value = "NetworkMask")
	suspend fun getNetworkMask(): String

    @Cacheable(value = "NetworkName")
	suspend fun getNetworkName(): String

    @Cacheable(value = "LowerIP")
	suspend fun getLowerIP(): String

    @Cacheable(value = "UpperIP")
	suspend fun getUpperIP(): String

    suspend fun setConfiguration(IPAddress: String, networkMask: String, FromIPAddress: String, ToIPAddress: String);

    suspend fun start()

    suspend fun stop()
}