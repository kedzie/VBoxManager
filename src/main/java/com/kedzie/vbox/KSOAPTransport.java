package com.kedzie.vbox;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import android.os.Parcel;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IMachineStateChangedEvent;
import com.kedzie.vbox.api.IRemoteObject;
import com.kedzie.vbox.api.ISessionStateChangedEvent;
import com.kedzie.vbox.api.jaxb.VBoxEventType;

public class KSOAPTransport {
//	private static final String TAG = "vbox."+KSOAPTransport.class.getSimpleName();
	private static final int TIMEOUT = 60000;
	private static final String NAMESPACE = "http://www.virtualbox.org/";
	private HttpTransportSE transport;
	private String _url;
	
	public KSOAPTransport(String url) {
		_url=url;
		this.transport = new HttpTransportSE(url, TIMEOUT);	
	}
	
	public <T> T getProxy(Class<T> clazz, String id) {
		return getJavaProxy(clazz, id, null);
	}
	
	public <T> T getProxy(Class<T> clazz, String id, Map<String, Object> cache) {
		return getJavaProxy(clazz, id, cache);
	}
	
	private <T> T getJavaProxy(Class<T> clazz, String id, Map<String, Object> cache) {
		T proxy = clazz.cast( Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class [] { clazz }, new SOAPInvocationHandler(id, clazz, cache)));
		if(IEvent.class.equals(clazz)) {
			VBoxEventType type = ((IEvent)proxy).getType();
			if(type.equals(VBoxEventType.ON_MACHINE_STATE_CHANGED)) 
				return clazz.cast(getJavaProxy( IMachineStateChangedEvent.class, id, cache ));
			else if(type.equals(VBoxEventType.ON_MACHINE_STATE_CHANGED))	
				return clazz.cast(getJavaProxy( ISessionStateChangedEvent.class, id, cache ));
		}
		return proxy;
	}
	
	/** 
	 * Invokes SOAP methods 
	 */
	private class  SOAPInvocationHandler implements InvocationHandler {
		private String id;
		private Class<?> type;
		private Map<String, Object> _cache = new HashMap<String, Object>();
		
		public SOAPInvocationHandler(String id, Class<?> type, Map<String,Object> cache) {
			this.id=id;
			this.type=type;
			if(cache!=null) _cache=cache;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
			synchronized( KSOAPTransport.class ) {
			if(method.getName().equals("getIdRef")) return this.id;
			if(method.getName().equals("equals")) return id.equals( ((IRemoteObject)args[0]).getIdRef());
			if(method.getName().equals("hashCode")) return id.hashCode();
			if(method.getName().equals("toString")) return id.toString();
			if(method.getName().equals("clearCache")) { _cache.clear(); return null; }
			if(method.getName().equals("describeContents")) { return 0; }
			if(method.getName().equals("writeToParcel")) {
				Parcel out = (Parcel)args[0];
				out.writeString(id);
				out.writeString(_url);
				out.writeMap(_cache);
				return null; 
			}
			KSOAP methodKSOAP = method.getAnnotation(KSOAP.class)==null ? type.getAnnotation(KSOAP.class) : method.getAnnotation(KSOAP.class);
			if(methodKSOAP!=null && methodKSOAP.cache() && method.getName().startsWith("get") && _cache.containsKey(method.getName()))	return _cache.get(method.getName());
			
			SoapObject request = new SoapObject(NAMESPACE, (methodKSOAP==null || methodKSOAP.prefix().equals("") ? type.getSimpleName() : methodKSOAP.prefix())+"_"+method.getName());
			
			if(methodKSOAP==null) 
				request.addProperty("_this", this.id);	
			else if ( !"".equals(methodKSOAP.thisReference()))  
				request.addProperty(methodKSOAP.thisReference(), this.id);

			if(args!=null) {
				for(int i=0; i<args.length; i++) 
					marshal(request, VBoxApplication.getAnnotation(KSOAP.class, method.getParameterAnnotations()[i]),  method.getParameterTypes()[i],	method.getGenericParameterTypes()[i],	args[i]);
			}
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11); 
			envelope.setOutputSoapObject(request);
			transport.call(NAMESPACE+request.getName(), envelope);
			Object ret = envelope.getResponse(method.getReturnType(), method.getGenericReturnType());
			ret = buildProxies( method.getReturnType(), method.getGenericReturnType(), ret );
			if(methodKSOAP!=null && methodKSOAP.cache() && method.getName().startsWith("get")) _cache.put(method.getName(), ret);
			return ret;
			}
		}

		/**
		 * Add an argument to a SOAP request
		 * @param request  SOAP request
		 * @param ksoap   parameter annotation with marshalling configuration
		 * @param clazz     <code>Class</code> of parameter
		 * @param gType   Generic type of parameter
		 * @param obj  object to marshall
		 */
		private void marshal(SoapObject request, KSOAP ksoap, Class<?> clazz, Type gType, Object obj) {
			if(clazz.isArray()) { 
				for(Object o : (Object[])obj)  
					marshal( request, ksoap, clazz.getComponentType(), gType,  o );
			} else if(Collection.class.isAssignableFrom(clazz)) {
				Class<?> pClazz = (Class<?>) ((ParameterizedType)gType).getActualTypeArguments()[0];
				for(Object o : (List<?>)obj)  
					marshal(request, ksoap,pClazz, gType,  o );
			} else if(!ksoap.type().equals("")) 
				request.addProperty( ksoap.value(), new SoapPrimitive(ksoap.namespace(), ksoap.type(), obj.toString()));
			else if(IRemoteObject.class.isAssignableFrom(clazz))	
				request.addProperty(ksoap.value(),  ((IRemoteObject)obj).getIdRef() );
			else if(clazz.isEnum())	
				request.addProperty(ksoap.value(),  new SoapPrimitive(NAMESPACE, clazz.getSimpleName(), obj.toString() ));
			else 
				request.addProperty(ksoap.value(), obj);	
		}
		
		/**
		 * Convert String ids to remote proxies
		 * @param returnType  desired type to create
		 * @param pClazz generic type variable
		 * @param ret  object to unmarshal
		 * @return   unmarshaled object
		 */
		private Object buildProxies(Class<?> returnType, Type genericType, Object ret) {
			if(ret==null) return null;
			if( Collection.class.isAssignableFrom(returnType) && genericType instanceof ParameterizedType) {
				 Class<?> pClazz = (Class<?>)((ParameterizedType)genericType).getActualTypeArguments()[0];
				 if(IRemoteObject.class.isAssignableFrom(pClazz)) {
					List<Object> list = new ArrayList<Object>();
					for(Object sp : (Collection<?>)ret)  
						list.add(  buildProxies(pClazz, pClazz, sp) );
					return list;
				} 
			} else if( returnType.isArray() && IRemoteObject.class.isAssignableFrom(returnType.getComponentType())) {
				List<?> list = (List<?>)ret;
				Object[] array = new Object[list.size()];
				for(int k=0; k<list.size(); k++)	
					array[k] = buildProxies(returnType.getComponentType(), genericType, list.get(k));
				 return array;
			}
			if(IRemoteObject.class.isAssignableFrom(returnType))	
				return getProxy(returnType, ret.toString());
			return ret;
		}
	}
}
