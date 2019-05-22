package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap
interface IMediumFormat : IManagedObjectRef, Parcelable {

    @Cacheable("Id")
	suspend fun getId(): String

    @Cacheable("Name")
	suspend fun getName(): String

    @Cacheable("Capabilities")
	suspend fun getCapabilities(): Int

    @Cacheable
    suspend fun describeFileExtensions(): Map<String, List<String>>

    @Cacheable
    suspend fun describeProperties():Map<String, List<String>>
}