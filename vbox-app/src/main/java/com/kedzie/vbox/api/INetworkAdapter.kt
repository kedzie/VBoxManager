package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.NetworkAdapterPromiscModePolicy
import com.kedzie.vbox.api.jaxb.NetworkAdapterType
import com.kedzie.vbox.api.jaxb.NetworkAttachmentType
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy
import java.io.IOException
import java.util.*

@KsoapProxy
@Ksoap
interface INetworkAdapter : IManagedObjectRef, Parcelable {
    companion object {
        const val BUNDLE = "networkadapter"
    }

    @Cacheable("adapterType")
    suspend fun getAdapterType(): NetworkAdapterType
    suspend fun setAdapterType(@Cacheable("adapterType") adapterType: NetworkAdapterType)

    @Cacheable("Slot")
	suspend fun getSlot(): Int;

    @Cacheable("enabled")
	suspend fun getEnabled(): Boolean
    suspend fun setEnabled(@Cacheable("enabled") enabled: Boolean)

    @Cacheable("MACAddress")
	suspend fun getMACAddress(): String
    suspend fun setMACAddress(@Cacheable("MACAddress") MACAddress: String)

    @Cacheable("attachmentType")
	suspend fun getAttachmentType(): NetworkAttachmentType
    suspend fun setAttachmentType(@Cacheable("attachmentType") attachmentType: NetworkAttachmentType)

    @Cacheable("bridgedInterface")
	suspend fun getBridgedInterface(): String
    suspend fun setBridgedInterface(@Cacheable("bridgedInterface") bridgedInterface: String)

    @Cacheable("hostOnlyInterface")
	suspend fun getHostOnlyInterface(): String
    suspend fun setHostOnlyInterface(@Cacheable("hostOnlyInterface") hostOnlyInterface: String)

    @Cacheable("internalNetwork")
	suspend fun getInternalNetwork(): String
    suspend fun setInternalNetwork(@Cacheable("internalNetwork") internalNetwork: String)

    @Cacheable("NATNetwork")
	suspend fun getNATNetwork(): String
    suspend fun setNATNetwork(@Cacheable("NATNetwork") natNetwork: String)

    @Cacheable("genericDriver")
	suspend fun getGenericDriver(): String
    suspend fun setGenericDriver(@Cacheable("genericDriver") genericDriver: String)

    @Cacheable("cableConnected")
	suspend fun getCableConnected(): Boolean
    suspend fun setCableConnected(@Cacheable("cableConnected") cableConnected: Boolean)

    @Cacheable("lineSpeed")
	suspend fun getLineSpeed(): Int;
    suspend fun setLineSpeed(@Cacheable("lineSpeed") @Ksoap(type = "unsignedInt") lineSpeed: Int)

    @Cacheable("promiscModePolicy")
	suspend fun getPromiscModePolicy(): NetworkAdapterPromiscModePolicy;
    suspend fun setPromiscModePolicy(@Cacheable("promiscModePolicy") promiscModePolicy: NetworkAdapterPromiscModePolicy)

    @Cacheable("traceEnabled")
	suspend fun getTraceEnabled(): Boolean;
    suspend fun setTraceEnabled(@Cacheable("traceEnabled") traceEnabled: Boolean)

    @Cacheable("traceFile")
	suspend fun getTraceFile(): String;
    suspend fun setTraceFile(@Cacheable("traceFile") traceFile: String)

    @Cacheable("bootPriority")
	suspend fun getBootPriority(): Int;
    suspend fun setBootPriority(@Cacheable("bootPriority") bootPriority: Int)

    //	@Cacheable("BandwidthGroup") suspend fun getBandwidthGroup(): IBandwidthGroup;
//	@Asyncronous public void setBandwidthGroup(@Ksoap("bandwidthGroup") IBandwidthGroup bandwidthGroup);
//	Ksoap(cacheable=true) public INATEngine getNATEngine();

    suspend fun getProperty(key: String): String

    suspend fun setProperty(key: String, value: String)

    suspend fun getProperties(names: String): Map<String, List<String>>
}

/**
 * Load network adapter properties
 * @param names  Property names to load, or empty for all
 * @return    properties
 */
suspend fun INetworkAdapter.getProperties(vararg names: String): Properties {
    val nameString = StringBuffer()
    for (name in names)
        Utils.appendWithComma(nameString, name)

    val map = getProperties(nameString.toString())
    val returnNames = map.get("returnNames")
    val values = map.get("returnval")
    val properties = Properties()
    for (i in returnNames!!.indices)
        properties[returnNames!![i]] = values!![i]
    return properties
}