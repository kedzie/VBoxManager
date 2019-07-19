package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.DeviceType
import com.kedzie.vbox.api.jaxb.MediumState
import com.kedzie.vbox.api.jaxb.MediumType
import com.kedzie.vbox.app.Utils
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy
import java.io.IOException
import java.util.*

@KsoapProxy
@Ksoap
interface IMedium : IManagedObjectRef, Parcelable {
    companion object {
        const val BUNDLE = "medium"
    }

    @Cacheable("Id")
	suspend fun getId(): String

    @Cacheable("Description")
    suspend fun getDescription(): String
    suspend fun setDescription(@Cacheable("Description") description: String)

    @Cacheable("State")
	suspend fun getState(): MediumState

    @Cacheable("Variant")
	suspend fun getVariant(): Int

    @Cacheable("Location")
	suspend fun getLocation(): String;
    suspend fun setLocation(@Cacheable("Location") location: String)

    @Cacheable("Name")
	suspend fun getName(): String

    @Cacheable("DeviceType")
	suspend fun getDeviceType(): DeviceType

    @Cacheable("HostDrive")
	suspend fun getHostDrive(): Boolean

    @Cacheable("Size")
	suspend fun getSize(): Long

    @Cacheable("Format")
	suspend fun getFormat(): String

    @Cacheable("MediumFormat")
	suspend fun getMediumFormat(): IMediumFormat

    @Cacheable("Type")
	suspend fun getType(): MediumType
    suspend fun setType(@Cacheable("Type") type: MediumType)

    @Cacheable
    suspend fun getAllowedTypes(): Array<MediumType>

    @Cacheable("Parent")
	suspend fun getParent(): IMedium

    @Cacheable("Children")
	suspend fun getChildren(): List<IMedium>

    @Cacheable("Base")
	suspend fun getBase(): IMedium

    @Cacheable("ReadOnly")
	suspend fun getReadOnly(): Boolean

    @Cacheable("LogicalSize")
	suspend fun getLogicalSize(): Long

    @Cacheable("AutoReset")
	suspend fun getAutoReset(): Boolean;
    suspend fun setAutoReset(@Cacheable("AutoReset") autoReset: Boolean)

    @Cacheable("LastAccessError")
	suspend fun getLastAccessError(): String;

    @Cacheable("MachineIds")
	suspend fun getMachineIds(): List<String>

    suspend fun setIds(setImageId: Boolean, imageId: String, setParentId: Boolean, parentId: String)

    suspend fun refreshState(): MediumState

    @Cacheable("SnapshotIds")
    suspend fun getSnapshotIds(): List<String>

    suspend fun lockRead(): MediumState
    suspend fun unlockRead(): MediumState

    suspend fun lockWrite(): MediumState
    suspend fun unlockWrite(): MediumState

    suspend fun close()

    suspend fun getProperty(key: String): String

    suspend fun setProperty(key: String, value: String);

    suspend fun getProperties(names: String) : Map<String, List<String>>

    suspend fun setProperties(names: List<String>, values: List<String>)

    suspend fun createBaseStorage(@Ksoap(type="long") logicalSize: Long, @Ksoap(type="unsignedInt") variant: Int): IProgress

    suspend fun createDiffStorage(@Ksoap(type="long") logicalSize: Long, @Ksoap(type="unsignedInt") variant: Int): IProgress

    suspend fun deleteStorage():IProgress

    suspend fun mergeTo(target: IMedium): IProgress

    suspend fun cloneTo(target: IMedium, @Ksoap(type="unsignedInt") variant: Int, parent: IMedium): IProgress

    suspend fun cloneToBase(target: IMedium, @Ksoap(type="unsignedInt") variant: Int): IProgress

    suspend fun compact(): IProgress

    suspend fun resize(@Ksoap(type="long") logicalSize: Long): IProgress

    suspend fun reset(): IProgress
}

/**
 * Load medium properties
 * @param names  Property names to load, or empty for all
 * @return    properties
 */
suspend fun IMedium.getProperties(vararg names: String): Properties {
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