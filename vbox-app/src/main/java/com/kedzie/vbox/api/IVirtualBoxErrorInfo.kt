package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy


@KsoapProxy
@Ksoap
interface IVirtualBoxErrorInfo : IManagedObjectRef {
    @Cacheable("ResultCode")
	suspend fun getResultCode(): Int
    @Cacheable("Text")
	suspend fun getText(): String
    @Cacheable("Next")
	suspend fun getNext(): IVirtualBoxErrorInfo
    @Cacheable("Component")
	suspend fun getComponent(): String
    @Cacheable("InterfaceID")
	suspend fun getInterfaceID(): String
}