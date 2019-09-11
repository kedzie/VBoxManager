package com.kedzie.vbox.api

import com.kedzie.vbox.soap.Ksoap
import com.kedzie.vbox.soap.KsoapProxy
import com.kedzie.vbox.soap.VBoxSvc

/**
 *
 * Managed object reference.
 *
 * Only within the webservice, a managed object reference (which is really an opaque number) allows a webservice client to address an object that lives in the address space of the webservice server.
 *
 * Behind each managed object reference, there is a COM object that lives in the webservice server's address space. The COM object is not freed until the managed object reference is released, either by an explicit call to [IManagedObjectRef.release] or by logging off from the webservice ([IVirtualBox.logoff]), which releases all objects created during the webservice session.
 *
 * Whenever a method call of the VirtualBox API returns a COM object, the webservice representation of that method will instead return a managed object reference, which can then be used to invoke methods on that object.
 * @apiviz.stereotype Proxy
 * @apiviz.category Proxy
 */
@KsoapProxy
interface IManagedObjectRef {

    /**
     * @return Managed object identifier
     */
    val idRef: String

    /**
     * @return VirtualBox JAXWS API
     */
    val api: VBoxSvc

    /**
     * room cache
     */
    val database: CacheDatabase

    /**
     * Returns the name of the interface that this managed object represents, for example, "IMachine", as a string.
     */
    @Ksoap
    suspend fun getInterfaceName(): String

    /**
     * Releases this managed object reference and frees the resources that were allocated for it in the webservice server process.
     *
     * After calling this method, the identifier of the reference can no longer be used.
     */
    @Ksoap
    suspend fun release()
}
