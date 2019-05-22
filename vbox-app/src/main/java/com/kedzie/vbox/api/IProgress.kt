package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap
interface IProgress : IManagedObjectRef, Parcelable {

    companion object {
        const val BUNDLE = "progress"
    }

    suspend fun waitForCompletion(@Ksoap(type="int") timeout: Int)

    suspend fun waitForCompletion(@Ksoap(type="unsignedInt") operation: Int, @Ksoap(type="int") timeout: Int)

    suspend fun waitForAsyncProgressCompletion(@Ksoap("pProgressAsync") pProgressAsync: IProgress);

    @Cacheable(value = "timeout")
    suspend fun getTimeout(): Int

    @Cacheable(value = "ResultCode")
    suspend fun getResultCode(): Int

    @Cacheable(value = "ErrorInfo")
	suspend fun getErrorInfo(): IVirtualBoxErrorInfo

    @Cacheable(value = "Description")
	suspend fun getDescription(): String
    @Cacheable(value = "Percent")
	suspend fun getPercent(): Int
    @Cacheable(value = "TimeRemaining")
	suspend fun getTimeRemaining(): Int
    @Cacheable(value = "Operation")
	suspend fun getOperation(): Int
    @Cacheable(value = "OperationCount")
	suspend fun getOperationCount(): Int
    @Cacheable(value = "OperationDescription")
	suspend fun getOperationDescription(): String
    @Cacheable(value = "OperationPercent")
	suspend fun getOperationPercent(): Int
    @Cacheable(value = "OperationWeight")
	suspend fun getOperationWeight(): Int
    @Cacheable(value = "Initiator")
	suspend fun getInitiator(): String
    @Cacheable(value = "Cancelled")
	suspend fun getCancelled(): Boolean
    @Cacheable(value = "Cancelable")
	suspend fun getCancelable(): Boolean
    @Cacheable(value = "Completed")
	suspend fun getCompleted(): Boolean

    suspend fun cancel()

    suspend fun setTimeout(@Ksoap(type="unsignedInt", value="timeout") timeout: Int)

    suspend fun setCurrentOperationProgress(@Ksoap(type="unsignedInt") percent: Int)

    suspend fun setNextOperation(nextOperationDescription: String,
                                 @Ksoap(type="unsignedInt") nextOperationsWeight: Int)
}
