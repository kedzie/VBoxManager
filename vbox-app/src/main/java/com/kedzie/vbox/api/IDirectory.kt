package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap(prefix = "IDirectory")
interface IDirectory : IManagedObjectRef {

    suspend fun getDirectoryName():  String
    suspend fun getFilter():  String
    suspend fun close()
    suspend fun read(): String
}