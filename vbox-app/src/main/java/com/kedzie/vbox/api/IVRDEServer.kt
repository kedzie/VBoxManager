package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.AuthType
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy


@KsoapProxy
@Ksoap
interface IVRDEServer : IManagedObjectRef {
    companion object {
        const val BUNDLE = "vrde"
        const val PROPERTY_PORT = "TCP/Ports"
    }

    @Cacheable("enabled")
	suspend fun getEnabled(): Boolean
    suspend fun setEnabled(@Cacheable("enabled") enabled: Boolean)

    @Cacheable("authType")
	suspend fun getAuthType(): AuthType
    suspend fun setAuthType(@Cacheable("authType") authType: AuthType)

    @Cacheable("authTimeout")
	suspend fun getAuthTimeout(): Int
    suspend fun setAuthTimeout(@Cacheable("authTimeout") @Ksoap(type = "unsignedInt") authTimeout: Int)

    @Cacheable("allowMultiConnection")
	suspend fun getAllowMultiConnection(): Boolean
    suspend fun setAllowMultiConnection(@Cacheable("allowMultiConnection") allowMultiConnection: Boolean)

    @Cacheable("ReuseSingleConnection")
	suspend fun getReuseSingleConnection(): Boolean
    suspend fun setReuseSingleConnection(@Cacheable("reuseSingleConnection") reuseSingleConnection: Boolean)

    @Cacheable("VRDEExtPack")
	suspend fun getVRDEExtPack(): String
    suspend fun setVRDEExtPack(@Cacheable("VRDEExtPack") VRDEExtPack: String)

    @Cacheable("authLibrary")
	suspend fun getAuthLibrary(): String
    suspend fun setAuthLibrary(@Cacheable("authLibrary") authLibrary: String)

    @Cacheable("VRDEProperties")
	suspend fun getVRDEProperties(): Array<String>

    suspend fun setVRDEProperty(key: String, value: String)

    suspend fun getVRDEProperty(key: String): String
}