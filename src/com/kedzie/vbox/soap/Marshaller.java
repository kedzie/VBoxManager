package com.kedzie.vbox.soap;

import java.lang.reflect.Type;

import org.ksoap2.serialization.SoapObject;

/**
 * 
 */
public interface Marshaller {
	
	public boolean handleObject(Class<?> clazz);

	/**
	 * Add an argument to a SOAP request
	 * @param request  SOAP request
	 * @param ksoap   parameter annotation with marshalling configuration
	 * @param clazz     {@link Class} of parameter
	 * @param gType   Generic type of parameter
	 * @param obj  object to marshall
	 */
	public void marshal(SoapObject request, KSOAP ksoap, Class<?> clazz, Type gType, Object obj);
	
	/**
	 * convert string return value to correct type
	 * @param returnType remote method return type
	 * @param genericType remote method return type (parameterized)
	 * @param ret  marshalled value
	 * @return unmarshalled return value
	 */
	public Object unmarshal(Class<?> returnType, Type genericType, Object ret);
}
