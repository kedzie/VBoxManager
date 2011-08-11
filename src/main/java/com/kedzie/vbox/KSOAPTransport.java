package com.kedzie.vbox;

import java.io.IOException;
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
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import com.kedzie.vbox.api.IRemoteObject;

public class KSOAPTransport {
	public static final String NAMESPACE = "http://www.virtualbox.org/";
	private HttpTransportSE transport;
	
	public KSOAPTransport(String url) { 
		this.transport = new HttpTransportSE(url);	
	}
	
	public Object call(SoapObject object) throws IOException, XmlPullParserException {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11) {
			@Override
			public Object getResponse() throws SoapFault {
				if (bodyIn instanceof SoapFault) throw (SoapFault)bodyIn;
				KvmSerializable ks = (KvmSerializable)bodyIn;
				
				if(ks.getPropertyCount()==0 || (ks.getPropertyCount()==1 && ks.getProperty(0).toString().equals("anyType{}"))) return null;
				else if(ks.getPropertyCount()==1)	
					return ks.getProperty(0).toString();
				
				Map<String, List<String>> map = new HashMap<String, List<String>>();
				for(int i=0;i<ks.getPropertyCount();i++){
					PropertyInfo info = new PropertyInfo();
					ks.getPropertyInfo(i, null, info);
					if(!map.containsKey(info.getName())) map.put(info.getName(), new ArrayList<String>());
					((ArrayList<String>)map.get(info.getName())).add(ks.getProperty(i).toString());
				}
				if(map.keySet().size()==1) return (List<String>)map.get(map.keySet().iterator().next());
				return map;
			}
		};
		envelope.setOutputSoapObject(object);
		transport.call(NAMESPACE+object.getName(), envelope);
		return envelope.getResponse();
	}

	public <T> T getProxy(Class<T> clazz, String id) {
		return clazz.cast( Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class [] { clazz }, new SOAPInvocationHandler(id, clazz)));
	}
	
	/** Invokes SOAP methods */
	private class  SOAPInvocationHandler implements InvocationHandler {
		private String id;
		private Class<?> type;
		private String clazz;
		private Map<String, Object> _cache = new HashMap<String, Object>();
		
		public SOAPInvocationHandler(String id, Class<?> type) {
			this.id=id;
			this.type=type;
			this.clazz = type.getSimpleName();
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
			if(method.getName().equals("getIdRef")) return this.id;
			if(method.getName().equals("equals")) return id.equals( ((IRemoteObject)args[0]).getIdRef());
			if(method.getName().equals("hashCode")) return id.hashCode();
			if(method.getName().equals("toString")) return id.toString();
			if(method.getName().equals("clearCache")) { _cache.clear(); return null; }
			KSOAP methodKSOAP = method.getAnnotation(KSOAP.class)==null ? type.getAnnotation(KSOAP.class) : method.getAnnotation(KSOAP.class);
			if(methodKSOAP!=null && methodKSOAP.cache() && method.getName().startsWith("get") && _cache.containsKey(method.getName()))	return _cache.get(method.getName());
			
			SoapObject request = new SoapObject(NAMESPACE, (methodKSOAP==null || methodKSOAP.prefix().equals("") ? clazz : methodKSOAP.prefix())+"_"+method.getName());
			
			if(methodKSOAP==null) 
				request.addProperty("_this", this.id);	
			else if ( !"".equals(methodKSOAP.thisReference()))  
				request.addProperty(methodKSOAP.thisReference(), this.id);

			if(args!=null) {
				for(int i=0; i<args.length; i++) marshall(request, VBoxApplication.getAnnotation(KSOAP.class, method.getParameterAnnotations()[i]),  method.getParameterTypes()[i],	method.getGenericParameterTypes()[i],	args[i]);
			}
			Object ret = unmarshall( method.getReturnType(), method.getGenericReturnType(), call(request) );
			if(methodKSOAP!=null && methodKSOAP.cache() && method.getName().startsWith("get")) _cache.put(method.getName(), ret);
			return ret;
		}

		private void marshall(SoapObject request, KSOAP ksoap, Class<?> clazz, Type gType, Object obj) {
			if(clazz.isArray()) { 
				for(Object o : (Object[])obj)  marshall( request, ksoap, clazz.getComponentType(), gType,  o );
			} else if(Collection.class.isAssignableFrom(clazz)) {
				Class<?> pClazz = (Class<?>) ((ParameterizedType)gType).getActualTypeArguments()[0];
				for(Object o : (List<?>)obj)  marshall(request, ksoap,pClazz, gType,  o );
			} else if(!ksoap.type().equals("")) request.addProperty( ksoap.value(), new SoapPrimitive(ksoap.namespace(), ksoap.type(), obj.toString()));
			else if(IRemoteObject.class.isAssignableFrom(clazz))	request.addProperty(ksoap.value(),  ((IRemoteObject)obj).getIdRef() );
			else if(clazz.isEnum())	request.addProperty(ksoap.value(),  new SoapPrimitive(NAMESPACE, clazz.getSimpleName(), obj.toString() ));
			else request.addProperty(ksoap.value(), obj);	
		}
		
		private Object unmarshall(Class<?> returnType, Type type, Object ret) {
			if(ret==null) return null;
			if(returnType.equals(Integer.class)) 	return Integer.valueOf(ret.toString());
			else if( Collection.class.isAssignableFrom(returnType) && type instanceof ParameterizedType ) {
				Class<?> pClazz = (Class<?>)((ParameterizedType)type).getActualTypeArguments()[0];
				List<Object> list = new ArrayList<Object>();
				if(Collection.class.isAssignableFrom(ret.getClass()))  for(Object sp : (Collection<?>)ret)  list.add(  unmarshall(pClazz, type, sp) );
				else list.add( unmarshall(pClazz, type, ret) );
				return list;
			} else if( returnType.isArray()) {
				Object[] array = new Object[((List<?>)ret).size()];
				for(int k=0; k<((List<?>)ret).size(); k++)	array[k] = unmarshall(returnType.getComponentType(), type, ((List<?>)ret).get(k)); 
				return array;
			} 	else if(returnType.equals(Boolean.class)) 	return Boolean.valueOf(ret.toString());
			else if(returnType.equals(String.class))	return ret.toString();
			else if(IRemoteObject.class.isAssignableFrom(returnType))	return getProxy(returnType, ret.toString());
			else if(returnType.isEnum()) 
				for( Object element : returnType.getEnumConstants()) 
					if( element.toString().equals( ret.toString() ) ) 
						return element;
			return ret;
		}
	}
}
