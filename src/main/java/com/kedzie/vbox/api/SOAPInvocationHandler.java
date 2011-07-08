package com.kedzie.vbox.api;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.ksoap2.serialization.SoapObject;

public class SOAPInvocationHandler implements InvocationHandler {
	private static final String NAMESPACE = "http://www.virtualbox.org/";

	public KSOAPTransport transport;
	private String id;
	private String i;
	
	public SOAPInvocationHandler(String id, String i, String url) {
		transport = new KSOAPTransport(url);
		this.id=id;
		this.i = i;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
		if(method.getName().equals("getId")) 
			return this.id;
		
		SoapObject object = new SoapObject(NAMESPACE, i+"_"+method.getName());
		object.addProperty("_this", this.id);
		if(method.getReturnType().equals(String.class)) 	
			return transport.callString(object);
		
		System.out.println("Return type: " + method.getReturnType().getComponentType());
		System.out.println("Generic Return type: " + method.getGenericReturnType());
		return transport.call(object);
	}
}
