package com.kedzie.vbox.api;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

import com.kedzie.vbox.MachineListActivity;

public class KSOAPTransport {
	private HttpTransportSE transport;
	
	public KSOAPTransport(String url) {
		this.transport = new HttpTransportSE(url);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> callStringArray(SoapObject object) throws IOException, XmlPullParserException {
		Vector<SoapPrimitive> vec = (Vector<SoapPrimitive>)call(object);
		List<String> s = new ArrayList<String>(vec.size());
		for(SoapPrimitive p : vec)
			s.add(p.toString());
		return s;
	}
	
	public String callString(SoapObject object) throws IOException, XmlPullParserException {
		return ((SoapPrimitive)call(object)).toString();
	}
	
	public SoapObject callObject(SoapObject object) throws IOException, XmlPullParserException {
		return (SoapObject)call(object);
	}
	
	public Object call(SoapObject object) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(object);
		transport.call(MachineListActivity.NAMESPACE+object.getName(), envelope);
		return envelope.getResponse();
	}

	/**
	 * Get a WS Proxy
	 * @param <T> the interface
	 * @param clazz interface
	 * @param id id of object
	 * @return the WS proxy
	 */
	public <T> T getProxy(Class<T> clazz, String id) {
		return clazz.cast( Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class [] { clazz }, new SOAPInvocationHandler(id, clazz.getSimpleName())));
	}
	
	private class SOAPInvocationHandler implements InvocationHandler {
		private String id;
		private String i;
		
		public SOAPInvocationHandler(String id, String i) {
			this.id=id;
			this.i = i;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
			if(method.getName().equals("getId")) 
				return this.id;
			Log.i("SOAP", method.getName());
			SoapObject object = new SoapObject(MachineListActivity.NAMESPACE, i+"_"+method.getName());
			object.addProperty("_this", this.id);
			if(method.getReturnType().equals(String.class)) 	
				return callString(object);
			else if(method.getReturnType().equals(IProgress.class))
				return getProxy(IProgress.class, callString(object));
			else if(IRemoteObject.class.isAssignableFrom(method.getReturnType())) {
				Log.i("SOAP", "Creating generic proxy: " + method.getReturnType());
				return getProxy(method.getReturnType(), callString(object));
			}
			Log.i("SOAP", "Return type: " + method.getReturnType().getComponentType());
			Log.i("SOAP", "Generic Return type: " + method.getGenericReturnType());
			return call(object);
		}
	}
}
