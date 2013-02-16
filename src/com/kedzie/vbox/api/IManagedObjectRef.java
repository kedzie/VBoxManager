package com.kedzie.vbox.api;

import java.util.Map;

import com.kedzie.vbox.soap.KSOAP;
import com.kedzie.vbox.soap.VBoxSvc;
import com.kedzie.vbox.soap.VBoxSvc.FutureValue;

/**
 * <p>Managed object reference.</p>
 * <p>Only within the webservice, a managed object reference (which is really an opaque number) allows a webservice client to address an object that lives in the address space of the webservice server.</p>
 * <p>Behind each managed object reference, there is a COM object that lives in the webservice server's address space. The COM object is not freed until the managed object reference is released, either by an explicit call to {@link IManagedObjectRef#release} or by logging off from the webservice ({@link IVirtualBox#logoff}), which releases all objects created during the webservice session.</p>
 * <p>Whenever a method call of the VirtualBox API returns a COM object, the webservice representation of that method will instead return a managed object reference, which can then be used to invoke methods on that object.</p></p>
 * @apiviz.stereotype Proxy
 * @apiviz.category Proxy
 */
public interface IManagedObjectRef  {
	
	public Class<? extends IManagedObjectRef> getInterface();
	
	/**
	 * @return Managed object identifier
	 */
	public String getIdRef();
	
	/**
	 * Clear cached property values
	 */
	public void clearCache();
	
	/**
	 * Clear cached property values
	 * @param names  names of properties
	 */
	public void clearCacheNamed(String...names);
	
	/**
	 * Get property cache
	 */
	public Map<String, Object> getCache();
	
	public void populateView(String method, FutureValue future);
	
	/**
	 * @return VirtualBox JAXWS API
	 */
	public VBoxSvc getAPI();
	
	/**
	 * Returns the name of the interface that this managed object represents, for example, "IMachine", as a string. 
	 */
	@KSOAP(prefix="IManagedObjectRef")
	public String getInterfaceName();
	
	/**
	 * Releases this managed object reference and frees the resources that were allocated for it in the webservice server process.
	 * <p>After calling this method, the identifier of the reference can no longer be used.
	 */
	@KSOAP(prefix="IManagedObjectRef")
	public void release();
}
