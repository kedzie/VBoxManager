package com.kedzie.vbox.api;

import com.kedzie.vbox.KSOAP;
import com.kedzie.vbox.VBoxSvc;

/**
 * <p>Managed object reference.</p>
 * <p>Only within the webservice, a managed object reference (which is really an opaque number) allows a webservice client to address an object that lives in the address space of the webservice server.</p>
 * <p>Behind each managed object reference, there is a COM object that lives in the webservice server's address space. The COM object is not freed until the managed object reference is released, either by an explicit call to <code>IManagedObjectRef::release</code> or by logging off from the webservice (<code>IWebsessionManager::logoff</code>), which releases all objects created during the webservice session.</p>
 * <p>Whenever a method call of the VirtualBox API returns a COM object, the webservice representation of that method will instead return a managed object reference, which can then be used to invoke methods on that object.</p></p>
 */
public interface IManagedObjectRef  {
	
	/**
	 * @return Managed object identifier
	 */
	public String getIdRef();
	
	/**
	 * Clear cached property values
	 */
	public void clearCache();
	
	/**
	 * @return VirtualBox JAXWS API
	 */
	public VBoxSvc getVBoxAPI();
	
	/**
	 * Returns the name of the interface that this managed object represents, for example, "IMachine", as a string. 
	 */
	@KSOAP(prefix="IManagedObjectRef")
	public String getInterfaceName();
	
	/**
	 * <p>Releases this managed object reference and frees the resources that were allocated for it in the webservice server process.</p>
	 * <p>After calling this method, the identifier of the reference can no longer be used.</p>
	 */
	@KSOAP(prefix="IManagedObjectRef")
	public void release();
}
