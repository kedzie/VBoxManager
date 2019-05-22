package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap
interface ISnapshot : IManagedObjectRef, Parcelable {
    companion object {
        const val BUNDLE = "snapshot"
    }

    @Cacheable("Id")
    suspend fun getId(): String

    @Cacheable("name")
	suspend fun getName(): String
    suspend fun setName(@Cacheable("name") name: String)

    @Cacheable("description")
	suspend fun getDescription(): String
    suspend fun setDescription(@Cacheable("description") description: String)

    @Cacheable("Timestamp")
	suspend fun getTimestamp(): Long

    @Cacheable("Online")
	suspend fun getOnline(): Boolean

    @Cacheable("Parent")
	suspend fun getParent(): ISnapshot

    @Cacheable("Children")
	suspend fun getChildren(): List<ISnapshot>

    @Cacheable("Machine")
	suspend fun getMachine(): IMachine
}