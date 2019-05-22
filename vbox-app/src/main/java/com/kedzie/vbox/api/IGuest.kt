package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.AdditionsRunLevelType
import com.kedzie.vbox.api.jaxb.AdditionsUpdateFlag
import com.kedzie.vbox.soap.Cacheable
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

/**
 * The {@link IGuest} interface represents information about the operating system running inside the virtual machine.
 * <p>
 * Used in {@link IConsole#getGuest}<b></b>.<p>
 * {@link IGuest} provides information about the guest operating system, whether Guest Additions are installed and other OS-specific virtual machine properties.<p>
 * <dl class="user" compact><dt><b>Interface ID:</b></dt><dd><code>{ED109B6E-0578-4B17-8ACE-52646789F1A0}</code> </dd></dl>
 */
@KsoapProxy
@Ksoap
interface IGuest: IManagedObjectRef, Parcelable {

    @Cacheable(value = "MemoryBalloonSize")
	suspend fun getMemoryBalloonSize(): Int
    suspend fun setMemoryBalloonSize(@Cacheable(value = "MemoryBalloonSize") @Ksoap(type="unsignedint") memoryBalloonSize: Int)

    @Cacheable(value = "StatisticsUpdateInterval")
	suspend fun getStatisticsUpdateInterval(): Int
    suspend fun setStatisticsUpdateInterval(@Cacheable(value = "StatisticsUpdateInterval") @Ksoap(type="unsignedint") statisticsUpdateInterval: Int)

    suspend fun getAdditionsStatus(level: AdditionsRunLevelType): Boolean

    suspend fun setCredentials(userName: String, password: String, domain: String, @Ksoap(type="boolean") allowInteractiveLogon: Boolean)

    @Cacheable(value = "OSTypeId")
	suspend fun getOSTypeId(): String
    @Cacheable(value = "AdditionsRunLevel")
	suspend fun getAdditionsRunLevel(): AdditionsRunLevelType
    @Cacheable(value = "AdditionsVersion")
	suspend fun getAdditionsVersion(): String
    @Cacheable(value = "AdditionsRevision")
	suspend fun getAdditionsRevision(): Int

    @Cacheable(value = "Sessions")
    suspend fun getSessions():  List<IGuestSession>

    suspend fun createSession(userName: String, password: String, domain: String, sessionName: String): IGuestSession

    suspend fun findSession(sessionName: String): List<IGuestSession>

    suspend fun updateGuestAdditions(source: String, flags: List<AdditionsUpdateFlag>): IProgress
}