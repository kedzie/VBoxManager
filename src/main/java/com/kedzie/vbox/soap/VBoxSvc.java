package com.kedzie.vbox.soap;

import java.io.IOException;
import java.lang.annotation.Annotation;
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

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IMachineStateChangedEvent;
import com.kedzie.vbox.api.IManagedObjectRef;
import com.kedzie.vbox.api.ISessionStateChangedEvent;
import com.kedzie.vbox.api.IVirtualBox;
import com.kedzie.vbox.api.jaxb.VBoxEventType;

/**
 * VirtualBox JAX-WS API
 */
public class VBoxSvc implements Parcelable {
	private static final String TAG = "VBoxSvc";
	protected static final int TIMEOUT = 60000;
	public static final String BUNDLE = "vmgr", NAMESPACE = "http://www.virtualbox.org/";

	protected String _url;
	protected IVirtualBox _vbox;
	protected HttpTransportSE  _transport;
	
	public VBoxSvc(String url) { 
		_url=url; 
		_transport = new HttpTransportSE(_url, TIMEOUT);
	}

	public VBoxSvc(VBoxSvc copy) {
		this(copy._url);
		_vbox = getProxy(IVirtualBox.class, copy._vbox.getIdRef());
	}
	
	/**
	 * Create remote-invocation proxy w/o cached properties
	 * @param clazz 		type of {@link ManagedObjectRef}
	 * @param id			UIUD of {@link ManagedObjectRef}
	 * @return 				remote invocation proxy
	 */
	protected <T> T getProxy(Class<T> clazz, String id) {
		return getProxy(clazz, id, null);
	}
	
	/**
	 * Create remote-invocation proxy w/cached properties
	 * @param clazz 		type of {@link ManagedObjectRef}
	 * @param id 			UIUD of {@link ManagedObjectRef}
	 * @param 				cached properties
	 * @return 				remote invocation proxy
	 */
	protected <T> T getProxy(Class<T> clazz, String id, Map<String, Object> cache) {
		T proxy = clazz.cast( Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class [] { clazz }, new KSOAPInvocationHandler(id, clazz, cache)));
		if(IEvent.class.equals(clazz)) {
			VBoxEventType type = ((IEvent)proxy).getType();
			if(type.equals(VBoxEventType.ON_MACHINE_STATE_CHANGED)) 
				return clazz.cast(getProxy( IMachineStateChangedEvent.class, id, cache ));
			else if(type.equals(VBoxEventType.ON_SESSION_STATE_CHANGED))	
				return clazz.cast(getProxy( ISessionStateChangedEvent.class, id, cache ));
		}
		return proxy;
	}
	
	/**
	 * Connect to {@link vboxwebsrv} & initialize the VBoxSvc API interface
	 * @param username username
	 * @param password password
	 * @return initialized {@link IVirtualBox} API interface
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public IVirtualBox logon(String username, String password) throws IOException, XmlPullParserException {
		return (_vbox = getProxy(IVirtualBox.class, null).logon(username, password));
	}

	/**
	 * Query metric data for specified {@link ManagedObject}
	 * @param object object to get metrics for
	 * @param metrics specify which metrics/accumulations to query. * for all
	 * @return  {@link Map} from metric name to another {@link Map} containing metric data
	 * @throws IOException
	 */
	public Map<String, Map<String,Object>> queryMetricsData(String object, String...metrics) throws IOException {
		Map<String, List<String>> data= _vbox.getPerformanceCollector().queryMetricsData(metrics, new String[] { object });
		List<Integer> vals = new ArrayList<Integer>(data.get("returnval").size());
		for(String s : data.get("returnval")) { 
			vals.add(Integer.valueOf(s));
		}
		Map<String, Map<String,Object>> ret = new HashMap<String, Map<String, Object>>();
		for(int i=0; i<data.get("returnMetricNames").size(); i++) {
			Map<String, Object> metric = new HashMap<String, Object>();
			for(Map.Entry<String, List<String>> entry : data.entrySet())
				metric.put(entry.getKey().substring(6), entry.getValue().get(i) );
					int start = Integer.valueOf(metric.remove("DataIndices").toString());
					int length = Integer.valueOf(metric.remove("DataLengths").toString());
					List<Integer> metricValues = new ArrayList<Integer>(vals.size());
					metricValues.addAll( vals.subList(start, start+length) );
					metric.put("val", metricValues);
					ret.put(  metric.get("MetricNames").toString(), metric );
		}
		return ret;
	}

	public IVirtualBox getVBox() { 
		return _vbox;	
	}
	
	public String getURL() { 
		return _url;
	}
	
	/**
	 * Make remote calls to VBox JAXWS API based on method metadata from {@link KSOAP} annotations.
	 */
	public class KSOAPInvocationHandler implements InvocationHandler {
		/** managed object UIUD */
		private String uiud;
		/** type of {@link IManagedObjectRef} */
		private Class<?> type;
		/** cached property values */
		private Map<String, Object> _cache = new HashMap<String, Object>();

		public KSOAPInvocationHandler(String id, Class<?> type, Map<String,Object> cache) {
			this.uiud=id;
			this.type=type;
			if(cache!=null)
				_cache=cache;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
			synchronized( VBoxSvc.class ) {
				if(method.getName().equals("getIdRef")) return this.uiud;
				if(method.getName().equals("hashCode")) return uiud==null ? 0 : uiud.hashCode();
				if(method.getName().equals("toString")) return type.getSimpleName() + "#" + uiud.toString();
				if(method.getName().equals("getInterface")) return type;
				if(method.getName().equals("equals")) {
					if(!(args[0] instanceof IManagedObjectRef) || !args[0].getClass().equals(type)) return false;
					return this.uiud.equals(((IManagedObjectRef)args[0]).getIdRef());
				}
				if(method.getName().equals("clearCache")) { _cache.clear(); return null; }
				if(method.getName().equals("getVBoxAPI")) return VBoxSvc.this;
				
				KSOAP methodKSOAP = method.getAnnotation(KSOAP.class)==null ? type.getAnnotation(KSOAP.class) : method.getAnnotation(KSOAP.class);
				if(method.getAnnotation(Cacheable.class)!=null && _cache.containsKey(method.getName()))	{
					Log.d(TAG, "returning cached value");
					return _cache.get(method.getName());
				}
				SoapObject request = new SoapObject(NAMESPACE, (methodKSOAP==null || methodKSOAP.prefix().equals("") ? type.getSimpleName() : methodKSOAP.prefix())+"_"+method.getName());
				if(methodKSOAP==null) 
					request.addProperty("_this", this.uiud);	
				else if ( !"".equals(methodKSOAP.thisReference()))  
					request.addProperty(methodKSOAP.thisReference(), this.uiud);
				if(args!=null) {
					for(int i=0; i<args.length; i++) 
						marshal(request, getAnnotation(KSOAP.class, method.getParameterAnnotations()[i]),  method.getParameterTypes()[i],	method.getGenericParameterTypes()[i],	args[i]);
				}
				SerializationEnvelope envelope = new SerializationEnvelope(); 
				envelope.setOutputSoapObject(request);
				_transport.call(NAMESPACE+request.getName(), envelope);
				Object ret = envelope.getResponse(method.getReturnType(), method.getGenericReturnType());
				if(method.getAnnotation(Cacheable.class)!=null ) _cache.put(method.getName(), ret);
				return ret;
			}
		}
		
		@SuppressWarnings("unchecked")
		public <T extends Annotation> T getAnnotation(Class<T> clazz, Annotation []a) {
			for(Annotation at : a) 
				if(at.annotationType().equals(clazz)) 
					return (T)at;
			return null;
		}

		/**
		 * Add an argument to a SOAP request
		 * @param request  SOAP request
		 * @param ksoap   parameter annotation with marshalling configuration
		 * @param clazz     {@link Class} of parameter
		 * @param gType   Generic type of parameter
		 * @param obj  object to marshall
		 */
		private void marshal(SoapObject request, KSOAP ksoap, Class<?> clazz, Type gType, Object obj) {
			if(obj==null) return;
			if(clazz.isArray()) { 
				for(Object o : (Object[])obj)  marshal( request, ksoap, clazz.getComponentType(), gType,  o );
			} else if(Collection.class.isAssignableFrom(clazz)) {
				Class<?> pClazz = (Class<?>) ((ParameterizedType)gType).getActualTypeArguments()[0];
				for(Object o : (List<?>)obj) marshal(request, ksoap,pClazz, gType,  o );
			} else if(!ksoap.type().equals("")) {
				request.addProperty( ksoap.value(), new SoapPrimitive(ksoap.namespace(), ksoap.type(), obj.toString()));
			} else if(IManagedObjectRef.class.isAssignableFrom(clazz)) {	
				request.addProperty(ksoap.value(),  ((IManagedObjectRef)obj).getIdRef() );
			} else if(clazz.isEnum())	{
				request.addProperty(ksoap.value(),  new SoapPrimitive(NAMESPACE, clazz.getSimpleName(), obj.toString() ));
			} else {
				request.addProperty(ksoap.value(), obj);
			}
		}
	}

	/**
	 * Handles unmarshalling of SOAP response based on {@link KSOAP} annotation metadata
	 */
	public class SerializationEnvelope extends SoapSerializationEnvelope {

		public SerializationEnvelope() {
			super(SoapEnvelope.VER11);
		}
		
		/**
		 * Unmarshall SoapEnvelope to correct type
		 * @param returnType   type to umarshall
		 * @param genericType  parameterized type
		 * @return  unmarshalled return value
		 * @throws SoapFault
		 */
		public Object getResponse(Class<?> returnType, Type genericType) throws SoapFault {
			if (bodyIn instanceof SoapFault) throw (SoapFault) bodyIn;
			boolean isCollection = Collection.class.isAssignableFrom(returnType);
			boolean isMap = Map.class.isAssignableFrom(returnType);
			KvmSerializable ks = (KvmSerializable) bodyIn;
			if ((ks.getPropertyCount()==0 && !isCollection && !isMap) || (ks.getPropertyCount() == 1 && ks.getProperty(0).toString().equals("anyType{}"))) 
				return null;
			if(isMap) {
				Map<String, List<String>> map = new HashMap<String, List<String>>();
				PropertyInfo info = new PropertyInfo();
				for (int i = 0; i < ks.getPropertyCount(); i++) {
					ks.getPropertyInfo(i, null, info);
					if (!map.containsKey(info.getName())) 
						map.put(info.getName(), new ArrayList<String>());
					map.get(info.getName()).add(   ks.getProperty(i).toString() );
				}
				return map;
			} 
			if(isCollection) {
				Class<?> pClazz = (Class<?>)((ParameterizedType)genericType).getActualTypeArguments()[0];
				Collection<Object> list = new ArrayList<Object>(ks.getPropertyCount());
				for (int i = 0; i < ks.getPropertyCount(); i++)
					list.add( unmarshal(pClazz, genericType, ks.getProperty(i)) );
				return list;
			}
			return unmarshal(returnType, genericType, ks.getProperty(0));
		}
		
		/**
		 * convert string return value to correct type
		 * @param returnType remote method return type
		 * @param genericType remote method return type (parameterized)
		 * @param ret  marshalled value
		 * @return unmarshalled return value
		 */
		private Object unmarshal(Class<?> returnType, Type genericType, Object ret) {
			if(ret==null) return null;
			if(returnType.isArray() && returnType.getComponentType().equals(byte.class))
				return android.util.Base64.decode(ret.toString().getBytes(), android.util.Base64.DEFAULT);
			if(returnType.equals(Boolean.class)) 	
				return Boolean.valueOf(ret.toString());
			else if(returnType.equals(Integer.class)) 	
				return Integer.valueOf(ret.toString());
			else if(returnType.equals(Long.class)) 	
				return Long.valueOf(ret.toString());
			else if(returnType.equals(String.class))	
				return ret.toString();
			else if(IManagedObjectRef.class.isAssignableFrom(returnType))	
				return getProxy(returnType, ret.toString());
			else if(returnType.isEnum()) {
				for( Object element : returnType.getEnumConstants()) 
					if( element.toString().equals( ret.toString() ) ) 
						return element;
			}
			return ret;
		}
	}
	
	public static final Parcelable.Creator<VBoxSvc> CREATOR = new Parcelable.Creator<VBoxSvc>() {
		public VBoxSvc createFromParcel(Parcel in) {
			VBoxSvc svc = new VBoxSvc(in.readString());
			svc._vbox = svc.getProxy(IVirtualBox.class, in.readString());
			return svc; 
		}
		public VBoxSvc[] newArray(int size) {   return new VBoxSvc[size];  }
	};
	
	@Override
	public int describeContents() {  
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) { 
		dest.writeString(_url); 
		dest.writeString(_vbox.getIdRef()); 
	}
}