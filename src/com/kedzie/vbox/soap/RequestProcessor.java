package com.kedzie.vbox.soap;

import org.ksoap2.serialization.SoapObject;

/**
 * Do custom processing on  soap request before it is sent
 */
public interface RequestProcessor {

	public void processRequest(SoapObject soapObject, KSOAP ksoap);
	
}
