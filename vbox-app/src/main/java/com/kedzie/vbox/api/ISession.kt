package com.kedzie.vbox.api

import android.os.Parcelable
import com.kedzie.vbox.api.jaxb.SessionState
import com.kedzie.vbox.api.jaxb.SessionType
import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy

@KsoapProxy
@Ksoap
interface ISession : IManagedObjectRef {
    /**
     * Unlocks a machine that was previously locked for the current session.
     */
    suspend fun unlockMachine()

    /**
     * @return Console object associated with this session.
     */
    suspend fun getConsole(): IConsole

    /**
     * @cached false
     * @return     Type of this session
     */
    suspend fun getType(): SessionType

    /**
     * @cached false
     * @return     state of this session
     */
    suspend fun getState(): SessionState

    /**
     * @return Machine object associated with this session.
     */
    suspend fun getMachine(): IMachine
}