package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.FileSeekOrigin
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap(prefix = "IFile")
interface IFile : IManagedObjectRef {
    @Cacheable( "CreationMode")
	suspend fun getCreationMode(): Int

    @Cacheable("Disposition")
	suspend fun getDisposition(): Int

    @Cacheable( "FileName")
	suspend fun getFileName(): String

    @Cacheable("InitialSize")
	suspend fun getInitialSize(): Long

    @Cacheable("OpenMode")
	suspend fun getOpenMode(): Int

    @Cacheable("Offset")
	suspend fun getOffset(): Long

    suspend fun close()

    @Cacheable("QueryInfo")
	suspend fun getQueryInfo(): IFsObjInfo

    suspend fun read(@Ksoap(type="unsignedInt") toRead: Int, @Ksoap(type="unsignedInt") timeoutMS: Int): String

    suspend fun readAt(@Ksoap(type="long") offset: Long, @Ksoap(type="unsignedInt") toRead: Int, @Ksoap(type="unsignedInt") timeoutMS: Int): String

    suspend fun seek(@Ksoap(type="unsignedInt") offset: Long, whence: FileSeekOrigin)

    suspend fun setACL(acl: String)

    suspend fun write(data: String, @Ksoap(type="unsignedInt") timeoutMS: Int): Int

    suspend fun writeAt(@Ksoap(type="unsignedInt") offset: Long, data: String, @Ksoap(type="unsignedInt") timeoutMS: Int): Int
}