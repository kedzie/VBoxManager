package com.kedzie.vbox.api;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
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

import com.kedzie.vbox.VBoxApplication;

public class KSOAPTransport {
	private static final String TAG = "vbox."+KSOAPTransport.class.getSimpleName();
	public static final String NAMESPACE = "http://www.virtualbox.org/";
	private HttpTransportSE transport;
	
	public KSOAPTransport(String url) { this.transport = new HttpTransportSE(url);	}
	
	public Object call(SoapObject object) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(object);
		transport.call(NAMESPACE+object.getName(), envelope);
		return envelope.getResponse();
	}

	public <T> T getProxy(Class<T> clazz, String id) {
		return clazz.cast( Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class [] { clazz }, new SOAPInvocationHandler(id, clazz)));
	}
	
	/**
	 * Invokes SOAP methods
	 */
	private class  SOAPInvocationHandler implements InvocationHandler {
		private String id;
		private String clazz;
		
		public SOAPInvocationHandler(String id, Class<?> type) {
			this.id=id;
			this.clazz = type.getSimpleName();
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
			if(method.getName().equals("getId")) 
				return this.id;
			
			String prefix = clazz;
			KSOAP typeAnnotation = method.getAnnotation(KSOAP.class);
			if(typeAnnotation!=null)  prefix = typeAnnotation.value();
			SoapObject request = new SoapObject(NAMESPACE, prefix+"_"+method.getName());
			request.addProperty("_this", this.id);

			if(args!=null) {
			for(int i=0; i<args.length; i++) {
				KSOAP arg = VBoxApplication.getAnnotation(KSOAP.class, method.getParameterAnnotations()[i]);
				if(arg==null) continue;
				if(method.getParameterTypes()[i].isArray()) {
					for(Object o : (Object[])args[i]) 
						request.addProperty(arg.value(), marshall(  method.getParameterTypes()[i].getComponentType(), o ) );
				} else {
					request.addProperty(arg.value(), marshall( method.getParameterTypes()[i], args[i]));
				}
			}	
			}
			Object ret = call(request);
			if(ret==null  ||  ret.toString().equals("anyType{}")) return null;
			
			if( method.getGenericReturnType() instanceof ParameterizedType ) {
				Class<?> pClazz = (Class<?>)((ParameterizedType)method.getGenericReturnType()).getActualTypeArguments()[0];
				List<Object> list = new ArrayList<Object>();
				for(Object sp : (Vector<Object>)ret)  
					list.add(  unmarshall(pClazz, sp));
				return list;
			}
			return unmarshall( method.getReturnType(), ret );
		}
		
		private Object marshall(Class<?> clazz, Object obj) {
			if(IRemoteObject.class.isAssignableFrom(clazz)) {
				 return ((IRemoteObject)obj).getId() ;
			} else if(clazz.isEnum())
				return new SoapPrimitive(NAMESPACE, clazz.getSimpleName(), obj.toString()  );
			else
				return obj;	
		}
	}
	
	private Object unmarshall(Class<?> returnType, Object ret) {
		if(returnType.equals(String.class)) 
			return ret.toString();
		if(IRemoteObject.class.isAssignableFrom(returnType)) 
			return getProxy(returnType, ret.toString());
		if(returnType.isEnum()) {
			for( Object element : returnType.getEnumConstants())
				if( element.toString().equals( ret.toString() ) ) return element;
		}
		return ret;
	}
}
