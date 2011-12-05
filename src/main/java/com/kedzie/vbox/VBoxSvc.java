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

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Parcel;
import android.os.Parcelable;

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
	private static final int TIMEOUT = 60000;
	private static final String NAMESPACE = "http://www.virtualbox.org/";

	public static final Parcelable.Creator<VBoxSvc> CREATOR = new Parcelable.Creator<VBoxSvc>() {
		public VBoxSvc createFromParcel(Parcel in) {
			VBoxSvc svc = new VBoxSvc(in.readString());
			svc._vbox = svc.getProxy(IVirtualBox.class, in.readString());
			return svc; 
		}
		public VBoxSvc[] newArray(int size) {  
			return new VBoxSvc[size]; 
		}
	};
	
	/**
	 * Make remote calls to VBox JAXWS API based on method metadata from <code>KSOAP</code> annotations.
	 */
	public class KSOAPInvocationHandler implements InvocationHandler {
		/** managed object UIUD */
		private String id;
		/** type of managed object */
		private Class<?> type;
		/** cached property values */
		private Map<String, Object> _cache = new HashMap<String, Object>();

		public KSOAPInvocationHandler(String id, Class<?> type, Map<String,Object> cache) {
			this.id=id;
			this.type=type;
			if(cache!=null) _cache=cache;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
			synchronized( VBoxSvc.class ) {
				if(method.getName().equals("getIdRef")) return this.id;
				if(method.getName().equals("equals")) return id.equals( ((IManagedObjectRef)args[0]).getIdRef());
				if(method.getName().equals("hashCode")) return id.hashCode();
				if(method.getName().equals("toString")) return id.toString();
				if(method.getName().equals("clearCache")) { _cache.clear(); return null; }
				if(method.getName().equals("getVBoxAPI")) return VBoxSvc.this;
				
				KSOAP methodKSOAP = method.getAnnotation(KSOAP.class)==null ? type.getAnnotation(KSOAP.class) : method.getAnnotation(KSOAP.class);
				if(method.getAnnotation(Cacheable.class)!=null && _cache.containsKey(method.getName()))	
					return _cache.get(method.getName());

				SoapObject request = new SoapObject(NAMESPACE, (methodKSOAP==null || methodKSOAP.prefix().equals("") ? type.getSimpleName() : methodKSOAP.prefix())+"_"+method.getName());

				if(methodKSOAP==null) 
					request.addProperty("_this", this.id);	
				else if ( !"".equals(methodKSOAP.thisReference()))  
					request.addProperty(methodKSOAP.thisReference(), this.id);

				if(args!=null) {
					for(int i=0; i<args.length; i++) 
						marshal(request, VBoxApplication.getAnnotation(KSOAP.class, method.getParameterAnnotations()[i]),  method.getParameterTypes()[i],	method.getGenericParameterTypes()[i],	args[i]);
				}
				VBoxSerializationEnvelope envelope = new VBoxSerializationEnvelope(); 
				envelope.setOutputSoapObject(request);
				_transport.call(NAMESPACE+request.getName(), envelope);
				Object ret = envelope.getResponse(VBoxSvc.this, method.getReturnType(), method.getGenericReturnType());
				if(method.getAnnotation(Cacheable.class)!=null ) _cache.put(method.getName(), ret);
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
						else if(IManagedObjectRef.class.isAssignableFrom(clazz))	
							request.addProperty(ksoap.value(),  ((IManagedObjectRef)obj).getIdRef() );
						else if(clazz.isEnum())	
							request.addProperty(ksoap.value(),  new SoapPrimitive(NAMESPACE, clazz.getSimpleName(), obj.toString() ));
						else 
							request.addProperty(ksoap.value(), obj);	
		}
	}
	
	/** vboxwebsrv URL.  i.e.  vboxserver:18083 */
	protected String _url;
	/** Main interface to VirtualBox API */
	protected IVirtualBox _vbox;
	/** KSOAP Transport */
	private HttpTransportSE  _transport;

	/**
	 * @param url Webservice URL
	 */
	public VBoxSvc(String url) { 
		_url=url; 
		_transport = new HttpTransportSE(_url, TIMEOUT);
	}

	/**
	 * Copy constructor
	 * @param copy <code>VBoxSvc</code> to copy
	 */
	public VBoxSvc(VBoxSvc copy) {
		this(copy._url);
		_vbox = getProxy(IVirtualBox.class, copy._vbox.getIdRef());
	}
	
	/**
	 * Create remote-invocation proxy w/o cached properties
	 * @param clazz 		type of <code>ManagedObjectRef</code>
	 * @param id			UIUD of <code>ManagedObjectRef</code> 
	 * @return 				remote invocation proxy
	 */
	public <T> T getProxy(Class<T> clazz, String id) {
		return getProxy(clazz, id, null);
	}
	
	/**
	 * Create remote-invocation proxy w/cached properties
	 * @param clazz 		type of <code>ManagedObjectRef</code>
	 * @param id 			UIUD of <code>ManagedObjectRef</code>
	 * @param 				cached properties
	 * @return 				remote invocation proxy
	 */
	public <T> T getProxy(Class<T> clazz, String id, Map<String, Object> cache) {
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
	 * Connect to <code>vboxwebsrv</code> & initialize the VBoxSvc API interface
	 * @param username username
	 * @param password password
	 * @return initialized <code>IVirtualBox</code> API interface
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public IVirtualBox logon(String username, String password) throws IOException, XmlPullParserException {
		return (_vbox = getProxy(IVirtualBox.class, null).logon(username, password));
	}

	/**
	 * Query metric data for specified <code>ManagedObject</code>
	 * @param object object to get metrics for
	 * @param count # of samples
	 * @param period time resolution
	 * @param metrics specify which metrics/accumulations to query. * for all
	 * @return  <code>Map</code> from metric name to another <code>Map</code> containing metric data
	 * @throws IOException
	 */
	public Map<String, Map<String,Object>> queryMetricsData(String object, int count, int period, String...metrics) throws IOException {
		Map<String, List<String>> data= _vbox.getPerformanceCollector().queryMetricsData(metrics, new String[] { object });
		List<Integer> vals = new ArrayList<Integer>(data.get("returnval").size());
		for(String s : data.get("returnval")) 
			vals.add(Integer.valueOf(s));
				Map<String, Map<String,Object>> ret = new HashMap<String, Map<String, Object>>();
				for(int i=0; i<data.get("returnMetricNames").size(); i++) {
					Map<String, Object> metric = new HashMap<String, Object>();
					for(Map.Entry<String, List<String>> entry : data.entrySet())
						metric.put(entry.getKey().substring(6), entry.getValue().get(i) );
							int start = Integer.valueOf(metric.remove("DataIndices").toString());
							int length = Integer.valueOf(metric.remove("DataLengths").toString());
							List<Integer> metricValues = new ArrayList<Integer>(count);
							for(int t=0; t<count-length; t++)	
								metricValues.add(0);
							metricValues.addAll( vals.subList(start, start+length) );
							metric.put("val", metricValues);
							ret.put(  metric.get("MetricNames").toString(), metric );
				}
				return ret;
	}
	
	/**
	 * @return Main interface to VirtualBox API
	 */
	public IVirtualBox getVBox() { 
		return _vbox;	
	}
	
	/**
	 * @return vboxwebsrv URL.  i.e.  vboxserver:18083 
	 */
	public String getURL() { 
		return _url;
	}
	
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
