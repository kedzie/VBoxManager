/**
 * VirtualBox API remote proxies.  
 * <p>Each interface defines webservice calls with marshalling details specified using {@link KSOAP} annotations on methods and/or parameters</p>
 * <p>A proxy can be instantiated using:</p>
 * <pre>
 * VBoxSvc vboxAPI = ...;  //logged on to webservice
 * String machineId = ...; //managed object identifier
 * IMachine proxy = vboxAPI.getProxy(IMachine.class, machineId);
 * </pre>
 * @apiviz.stereotype Proxy
 * @apiviz.category Proxy
 */
package com.kedzie.vbox.api;