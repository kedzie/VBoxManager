package com.kedzie.vbox.soap;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import com.google.common.base.Objects;
import com.kedzie.vbox.BuildConfig;
import com.kedzie.vbox.api.IDHCPServer;
import com.kedzie.vbox.api.IDisplay;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IHost;
import com.kedzie.vbox.api.IHostNetworkInterface;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMachineStateChangedEvent;
import com.kedzie.vbox.api.IManagedObjectRef;
import com.kedzie.vbox.api.IMedium;
import com.kedzie.vbox.api.INetworkAdapter;
import com.kedzie.vbox.api.IProgress;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.ISessionStateChangedEvent;
import com.kedzie.vbox.api.ISnapshotDeletedEvent;
import com.kedzie.vbox.api.ISnapshotTakenEvent;
import com.kedzie.vbox.api.IVirtualBox;
import com.kedzie.vbox.api.Screenshot;
import com.kedzie.vbox.api.jaxb.LockType;
import com.kedzie.vbox.api.jaxb.MachineState;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.app.Tuple;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.metrics.MetricQuery;
import com.kedzie.vbox.server.Server;
import com.kedzie.vbox.soap.ssl.InteractiveTrustedHttpsTransport;
import com.kedzie.vbox.soap.ssl.KeystoreTrustedHttpsTransport;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.*;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nf.fr.eraasoft.pool.ObjectPool;
import nf.fr.eraasoft.pool.PoolException;
import nf.fr.eraasoft.pool.PoolSettings;
import nf.fr.eraasoft.pool.PoolableObjectBase;

/**
 * VirtualBox JAX-WS API
 * @apiviz.landmark
 * @apiviz.stereotype service
 * @apiviz.owns com.kedzie.vbox.api.IVirtualBox
 * @apiviz.owns com.kedzie.vbox.soap.HttpTransport
 * @apiviz.owns com.kedzie.vbox.soap.TrustedHttpsTransport
 * @apiviz.owns com.kedzie.vbox.server.Server
 * @apiviz.uses com.kedzie.vbox.soap.KSOAP
 * @apiviz.composedOf com.kedzie.vbox.soap.VBoxSvc$KSOAPInvocationHandler
 */
public class VBoxSvc implements Parcelable, Externalizable {
	private static final String TAG = "VBoxSvc";
	private static final int TIMEOUT = 20000;
	public static final String BUNDLE = "vmgr";
	public static final String NAMESPACE = "http://www.virtualbox.org/";
	private static final int THREAD_POOL_SIZE = 15;
	private static final int TRANSPORT_POOL_SIZE = 15;
	private static final ClassLoader LOADER = VBoxSvc.class.getClassLoader();
	/** Reflection cache.  Maps from [classname].[property] to [setter-method] */
	Map<String, Method> typeCache = new HashMap<String, Method>();

	public static final Parcelable.Creator<VBoxSvc> CREATOR = new Parcelable.Creator<VBoxSvc>() {
		public VBoxSvc createFromParcel(Parcel in) {
			VBoxSvc svc = new VBoxSvc((Server)in.readParcelable(LOADER));
			String vboxId = in.readString();
			svc._vbox = svc.getProxy(IVirtualBox.class, vboxId);
			svc.init();
			return svc;
		}
		public VBoxSvc[] newArray(int size) {
			return new VBoxSvc[size];
		}
	};
	
	/**
	 * Thread for making SOAP invocations
	 */
	public class AsynchronousThread implements Runnable {
		private String name;
		private SerializationEnvelope envelope;

		public AsynchronousThread(String name, SerializationEnvelope envelope) {
			this.name=name;
			this.envelope = envelope;
		}

		@Override
		public void run() {
				HttpTransportSE transport = null;
				try {
					transport = _transportPool.getObj();
					transport.call(name, envelope);
				} catch(Exception e) {
					Log.e(TAG, "PoolException", e);
				} finally {
					_transportPool.returnObj(transport);
				}
		}
	}

	/**
	 * Make remote calls to VBox JAXWS API based on method metadata from {@link KSOAP} annotations.
	 */
	public class KSOAPInvocationHandler implements InvocationHandler {

		/** Unique identifier (UIUD) of {@link IManagedObjectRef} */
		private String _uiud;

		/** Type of {@link IManagedObjectRef} */
		private Class<?> _type;

		/** cached property values */
		private Map<String, Object> _cache;

		public KSOAPInvocationHandler(String id, Class<?> type, Map<String,Object> cache) {
			_uiud=id;
			_type=type;
			_cache = cache!=null ? cache : new HashMap<String, Object>();
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args)throws Throwable {
			String name = method.getName();
			if(name.equals("hashCode")) return Objects.hashCode(_uiud);
			if(name.equals("toString")) {
				return _type.getSimpleName() + " #" + _uiud + Utils.toString("Cache", _cache);
			}
			if(name.equals("equals")) {
				if(args[0]==null) return false;
				if(!(args[0] instanceof IManagedObjectRef) || !_type.isAssignableFrom(args[0].getClass())) 
					return false;
				return Objects.equal(_uiud, ((IManagedObjectRef)args[0]).getIdRef());
			}
			if(name.equals("clearCache")) { 
				_cache.clear(); 
				return null; 
			}
			if(name.equals("clearCacheNamed")) { 
				for(String arg : (String[])args[0]) 
					_cache.remove(arg); 
				return null;
			}
			if(name.equals("getAPI")) return VBoxSvc.this;
			if(name.equals("describeContents")) return 0;
			if(name.equals("getCache")) return _cache;
			if(name.equals("getIdRef")) return _uiud;
			if(name.equals("writeToParcel")) {
				Parcel out = (Parcel)args[0];
				out.writeParcelable(VBoxSvc.this, 0);
				out.writeString(_uiud);
				out.writeMap(_cache);
				return null;
			}
			
			KSOAP ksoap = method.getAnnotation(KSOAP.class);
			if(ksoap==null)
				ksoap=method.getDeclaringClass().getAnnotation(KSOAP.class);

			String cacheKey = name;
			if(ksoap.cacheable()) {
				if(args!=null) {
					for(Object arg : args)
						cacheKey+="-"+arg.toString();
				}
				if(_cache.containsKey(cacheKey))
					return _cache.get(cacheKey);
			}
			
			SoapObject request = new SoapObject(NAMESPACE, (Utils.isEmpty(ksoap.prefix()) ? _type.getSimpleName() : ksoap.prefix())+"_"+method.getName());
			
			if (!Utils.isEmpty(ksoap.thisReference()))
				request.addProperty(ksoap.thisReference(), _uiud);
			
			if(args!=null) {
				for(int i=0; i<args.length; i++)
					marshal(request, Utils.getAnnotation(KSOAP.class, method.getParameterAnnotations()[i]),  method.getParameterTypes()[i],	method.getGenericParameterTypes()[i],	args[i]);
			}
			
			SerializationEnvelope envelope = new SerializationEnvelope(request);

			if(method.isAnnotationPresent(Asyncronous.class)) {
				_threadPoolExecutor.execute(new AsynchronousThread(NAMESPACE+request.getName(), envelope));
				return null;
			} else {
				HttpTransportSE transport = null;
				try {
					transport = _transportPool.getObj();
					transport.call(NAMESPACE+request.getName(), envelope);
					Object ret = envelope.getResponse(method.getReturnType(), method.getGenericReturnType());
					if(ksoap.cacheable()) 
						_cache.put(cacheKey, ret);
					if(name.startsWith("set"))		//update cache if we are calling a setter
						_cache.put("get"+name.substring(3), ret);
					return ret;
				} catch(PoolException e) {
					Log.e(TAG, "PoolException", e);
					throw new RuntimeException(e.getMessage(), e);
				} finally {
					_transportPool.returnObj(transport);
				}
			}
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
			if(clazz.isArray()) { //Arrays
				for(Object o : (Object[])obj)  
					marshal( request, ksoap, clazz.getComponentType(), gType,  o );
			} else if(Collection.class.isAssignableFrom(clazz)) { //Collections
				Class<?> pClazz = Utils.getTypeParameter(gType,0);
				for(Object o : (List<?>)obj) 
					marshal(request, ksoap, pClazz, gType,  o );
			} else if(!Utils.isEmpty(ksoap.type())) { //if annotation specifies SOAP datatype, i.e. unsignedint
				request.addProperty( ksoap.value(), new SoapPrimitive(ksoap.namespace(), ksoap.type(), obj.toString()));
			} else if(IManagedObjectRef.class.isAssignableFrom(clazz)) {
				request.addProperty(ksoap.value(),  ((IManagedObjectRef)obj).getIdRef() );
			} else if(clazz.isEnum()) {
				try {
					String text = (String) getMethod(clazz, "value").invoke(obj);
					request.addProperty(ksoap.value(),  new SoapPrimitive(NAMESPACE, clazz.getSimpleName(), text));
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
//				request.addProperty(ksoap.value(),  new SoapPrimitive(NAMESPACE, clazz.getSimpleName(), obj.toString() ));
			} else
				request.addProperty(ksoap.value(), obj);
		}
	}

	/**
	 * Handles unmarshalling of SOAP response based on {@link KSOAP} annotation metadata
	 */
	public class SerializationEnvelope extends SoapSerializationEnvelope {

		public SerializationEnvelope(SoapObject soapObject) {
			super(SoapEnvelope.VER11);
			setAddAdornments(false);
			setOutputSoapObject(soapObject);
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
			boolean IS_COLLECTION = Collection.class.isAssignableFrom(returnType);
			boolean IS_MAP = Map.class.isAssignableFrom(returnType);
			boolean IS_ARRAY = returnType.isArray() && !returnType.getComponentType().equals(byte.class);
			KvmSerializable ks = (KvmSerializable) bodyIn;
			if ((ks.getPropertyCount()==0 && !IS_COLLECTION && !IS_MAP)) {
				Log.w(TAG, "returning NULL because property count is 0");
				return null;
			}
			if(IS_MAP) {
				Type valueType = ((ParameterizedType)genericType).getActualTypeArguments()[1];
				if(!(valueType instanceof Class)) {  //Map<String, List<String>>
					Map<String, List<String>> map = new HashMap<String, List<String>>();
					PropertyInfo info = new PropertyInfo();
					for (int i = 0; i < ks.getPropertyCount(); i++) {
						ks.getPropertyInfo(i, null, info);
						if (!map.containsKey(info.getName()))
							map.put(info.getName(), new ArrayList<String>());
						map.get(info.getName()).add(ks.getProperty(i).toString());
					}
					return map;
				} else {		//Map<String,String>
					Map<String, String> map = new HashMap<String, String>();
					PropertyInfo info = new PropertyInfo();
					for (int i = 0; i < ks.getPropertyCount(); i++) {
						ks.getPropertyInfo(i, null, info);
						map.put(info.getName(), ks.getProperty(i).toString());
					}
					return map;
				}
			} else if(IS_COLLECTION) {
				Class<?> pClazz = Utils.getTypeParameter(genericType,0);
				Collection<Object> list = new ArrayList<Object>(ks.getPropertyCount());
				for (int i = 0; i < ks.getPropertyCount(); i++)
					list.add(unmarshal(pClazz, genericType, ks.getProperty(i)));
				return list;
			} else if(IS_ARRAY) {
				Class<?> pClazz = returnType.getComponentType();
				Object[] array = (Object[])Array.newInstance(pClazz, ks.getPropertyCount());
				for (int i = 0; i < ks.getPropertyCount(); i++)
					array[i] = unmarshal(pClazz, genericType, ks.getProperty(i));
				return array;
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
		public Object unmarshal(Class<?> returnType, Type genericType, Object ret) {
			if(ret==null || ret.toString().equals("anyType{}")) 
				return null;
			if(returnType.isArray() && returnType.getComponentType().equals(byte.class)) {
				return android.util.Base64.decode(ret.toString().getBytes(), android.util.Base64.DEFAULT);
			} else if(returnType.equals(Boolean.class) || returnType.equals(boolean.class)) {
				return Boolean.valueOf(ret.toString());
			} else if(returnType.equals(Integer.class) || returnType.equals(int.class)) {
				return Integer.valueOf(ret.toString());
			} else if(returnType.equals(Long.class) || returnType.equals(long.class)) {
				return Long.valueOf(ret.toString());
			} else if(returnType.equals(Short.class) || returnType.equals(short.class)) {
				return Short.valueOf(ret.toString());
			} else if(returnType.equals(String.class))
				return ret.toString();
			else if(IManagedObjectRef.class.isAssignableFrom(returnType))
				return getProxy(returnType, ret.toString());
			else if(returnType.isEnum()) {
				try {
					return getMethod(returnType, "fromValue").invoke(null, ret.toString());
				} catch (Exception e) {
					throw new RuntimeException(String.format("Error unmarshalling enum: %s.%s", returnType.getSimpleName(), ret.toString()));
				}
//				for( Object element : returnType.getEnumConstants()) {
//					if( element.toString().equals( ret.toString() ) )
//						return element;
//				}
			} else if(returnType.isAnnotationPresent(KSoapObject.class)) {
				try {
					if(BuildConfig.DEBUG) Log.v(TAG, "Unmarshalling Complex Object: " + returnType.getName());
					Object pojo = returnType.newInstance();
					SoapObject soapObject = (SoapObject)ret;
					PropertyInfo propertyInfo = new PropertyInfo();
					for(int i=0; i<soapObject.getPropertyCount(); i++) {
						soapObject.getPropertyInfo(i, propertyInfo);
						Method setterMethod = findSetterMethod(returnType, propertyInfo.getName());
						if(setterMethod==null) {
							Log.w(TAG, String.format("complex object property not found: %s.%s", returnType, propertyInfo.getName()));
							continue;
						}
						Class<?> propertyType = setterMethod.getParameterTypes()[0];
						Object value = unmarshal(propertyType, propertyType, propertyInfo.getValue());
						if(BuildConfig.DEBUG) Log.v(TAG, String.format("Setting property: %1$s.%2$s = %3$s", returnType.getSimpleName(), propertyInfo.getName(), value));
						setterMethod.invoke(pojo, value);
					}
					return pojo;
				} catch (Exception e) {
					Log.e(TAG, "Error unmarshalling complex object: " + returnType.getName(), e);
				} 
			}
			return ret;
		}

		/**
		 * Find and cache the setter method for a particular property
		 * @param clazz			object type
		 * @param property	property name
		 * @return	the setter method
		 */
		private Method findSetterMethod(Class<?> clazz, String property) {
			String key = clazz.getSimpleName()+"."+property;
			if(typeCache.containsKey(key)) 
				return typeCache.get(key);
			String setterMethodName = "set"+property.substring(0, 1).toUpperCase(Locale.ENGLISH)+property.substring(1);
			for(Method method : clazz.getMethods()) 
				if(method.getName().equals(setterMethodName)) {
					typeCache.put(key, method);
					return method;
				}
			Log.w(TAG, "No Setter Found: " + setterMethodName);
			return null;
		}
	}

	/**
	 * Get method from reflection cache
	 * @param clazz	class
	 * @param name method name
	 * @return	the method
	 */
	Method getMethod(Class<?> clazz, String name) {
		String key = clazz.getSimpleName()+"."+name;
		if(!typeCache.containsKey(key)) {
			for(Method m : clazz.getMethods()) {
				if(m.getName().equals(name)) {
					typeCache.put(key, m);
					break;
				}
			}
		}
		return typeCache.get(key);
	}

	private Server _server;
	private IVirtualBox _vbox;
	private ObjectPool<HttpTransportSE> _transportPool;
	private ExecutorService _threadPoolExecutor;

	/**
	 * @param server	VirtualBox webservice server
	 */
	public VBoxSvc(Server server) {
		_server=server;
		init();
	}

	/**
	 * Copy constructor
	 * @param copy	The original {@link VBoxSvc} to copy
	 */
	public VBoxSvc(VBoxSvc copy) {
		this(copy._server);
		_vbox = getProxy(IVirtualBox.class, copy._vbox.getIdRef());
	}
	
	private void init() {
		_threadPoolExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		PoolSettings<HttpTransportSE> poolSettings = new PoolSettings<HttpTransportSE>(
		                new PoolableObjectBase<HttpTransportSE>() {
		                	@Override public void activate(HttpTransportSE t) {}
		                	@Override
		                	public HttpTransportSE make() {
		                		return _server.isSSL() ? 
		                				new KeystoreTrustedHttpsTransport(_server, TIMEOUT) : 
		                				new HttpTransport(_server, TIMEOUT);
		                	}
		                });
		poolSettings.min(0).max(TRANSPORT_POOL_SIZE);
		_transportPool = poolSettings.pool();
	}

	public IVirtualBox getVBox() {
		return _vbox;
	}

	public void setVBox(IVirtualBox box) {
		_vbox=box;
	}

	public Server getServer() {
		return _server;
	}
	
	public ExecutorService getExecutor() {
	    return _threadPoolExecutor;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(_server, flags);
		dest.writeString(_vbox.getIdRef());
	}

	@Override
	public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
		Log.w(TAG, "===========VBoxSvc has been SERIALIZED===========");
		_server=(Server)input.readObject();
		init();
		String vboxId = input.readUTF();
		_vbox = getProxy(IVirtualBox.class, vboxId);
	}

	@Override
	public void writeExternal(ObjectOutput output) throws IOException {
		Log.w(TAG, "===========VBoxSvc has been SERIALIZED===========");
		output.writeObject(_server);
		output.writeUTF(_vbox.getIdRef());
	}

	/**
	 * Create remote-invocation proxy w/o cached properties
	 * @param clazz 		type of {@link IManagedObjectRef}
	 * @param id			UIUD of {@link IManagedObjectRef}
	 * @return 				remote invocation proxy
	 */
	public <T> T getProxy(Class<T> clazz, String id) {
		return getProxy(clazz, id, null);
	}

	/**
	 * Create remote-invocation proxy w/cached properties
	 * @param clazz 		type of {@link IManagedObjectRef}
	 * @param id 			UIUD of {@link IManagedObjectRef}
	 * @param cache			cached properties
	 * @return 				remote invocation proxy
	 */
	public <T> T getProxy(Class<T> clazz, String id, Map<String, Object> cache) {
		T proxy = clazz.cast( Proxy.newProxyInstance(LOADER, new Class [] { clazz }, new KSOAPInvocationHandler(id, clazz, cache)));
		if(IEvent.class.equals(clazz)) {
			VBoxEventType type = ((IEvent)proxy).getType();
			if(type.equals(VBoxEventType.ON_MACHINE_STATE_CHANGED))
				return clazz.cast(getProxy( IMachineStateChangedEvent.class, id, cache ));
			else if(type.equals(VBoxEventType.ON_SESSION_STATE_CHANGED))
				return clazz.cast(getProxy( ISessionStateChangedEvent.class, id, cache ));
			else if(type.equals(VBoxEventType.ON_SNAPSHOT_DELETED))
				return clazz.cast(getProxy( ISnapshotDeletedEvent.class, id, cache ));
			else if(type.equals(VBoxEventType.ON_SNAPSHOT_TAKEN))
				return clazz.cast(getProxy( ISnapshotTakenEvent.class, id, cache ));
		}
		return proxy;
	}

	/**
	 * Connect to <code>vboxwebsrv</code> & initialize the VBoxSvc API interface
	 * @return initialized {@link IVirtualBox} API interface
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public IVirtualBox logon() throws IOException  {
		try {
			return (_vbox = getProxy(IVirtualBox.class, null).logon(_server.getUsername(), _server.getPassword()));
		} catch(SoapFault e) {
			Log.e(TAG, "Logon error", e);
			throw new ConnectException("Authentication Error");
		}
	}

	/**
	 * Logoff from VirtualBox API
	 * @throws IOException 
	 */
	public void logoff() throws IOException {
		if(_vbox!=null)
			_vbox.logoff();
		_vbox=null;
	}

	/**
	 * Query metric data for specified {@link IManagedObjectRef}
	 * @param object object to get metrics for
	 * @param metrics specify which metrics/accumulations to query. * for all
	 * @return  {@link Map} from metric name to {@link MetricQuery}
	 * @throws IOException
	 */
	public Map<String, MetricQuery> queryMetrics(String object, String...metrics) throws IOException {
		Map<String, List<String>> data= _vbox.getPerformanceCollector().queryMetricsData(metrics, new String[] { object });

		Map<String, MetricQuery> ret = new HashMap<String, MetricQuery>();
		for(int i=0; i<data.get("returnMetricNames").size(); i++) {
			MetricQuery q = new MetricQuery();
			q.name=(String)data.get("returnMetricNames").get(i);
			q.object=(String)data.get("returnObjects").get(i);
			q.scale=Integer.valueOf(data.get("returnScales").get(i));
			q.unit=(String)data.get("returnUnits").get(i);
			int start = Integer.valueOf( data.get("returnDataIndices").get(i));
			int length = Integer.valueOf( data.get("returnDataLengths").get(i));

			q.values= new int[length];
			int j=0;
			for(String s : data.get("returnval").subList(start, start+length)) 
				q.values[j++] = Integer.valueOf(s)/q.scale;
			ret.put(q.name, q);
		}
		return ret;
	}

	public Screenshot takeScreenshot(IMachine machine) throws IOException {
		if(machine.getState().equals(MachineState.RUNNING) || machine.getState().equals(MachineState.SAVED)) {
			ISession session = _vbox.getSessionObject();
			machine.lockMachine(session, LockType.SHARED);
			try {
				IDisplay display = session.getConsole().getDisplay();
				Map<String, String> res = display.getScreenResolution(0);
				int width =  Integer.valueOf(res.get("width"));
				int height = Integer.valueOf(res.get("height"));
				return new Screenshot(width, height, display.takeScreenShotPNGToArray(0, width, height));
			} finally {
				session.unlockMachine();
			}
		}
		return null;
	}

	public Screenshot takeScreenshot(IMachine machine, int width, int height) throws IOException {
		ISession session = _vbox.getSessionObject();
		machine.lockMachine(session, LockType.SHARED);
		try {
			IDisplay display = session.getConsole().getDisplay();
			Map<String, String> res = display.getScreenResolution(0);
			float screenW = Float.valueOf(res.get("width"));
			float screenH = Float.valueOf(res.get("height"));
			if(screenW > screenH) {
				float aspect = screenH/screenW;
				height =(int) (aspect*width);
			} else if(screenH > screenW){
				float aspect = screenW/screenH;
				width =(int) (aspect*height);
			}
			return new Screenshot(width, height, session.getConsole().getDisplay().takeScreenShotPNGToArray(0, width, height));
		} finally {
			session.unlockMachine();
		}
	}

	public Screenshot readSavedScreenshot(IMachine machine, int screenId) throws IOException {
		Map<String, String> val = machine.readSavedScreenshotPNGToArray(screenId);
		return new Screenshot(Integer.valueOf(val.get("width")), Integer.valueOf(val.get("height")), Base64.decode(val.get("returnval"), 0));
	}

	public Screenshot readSavedThumbnail(IMachine machine, int screenId) throws IOException {
		Map<String, String> val = machine.readSavedThumbnailPNGToArray(screenId);
		return new Screenshot(Integer.valueOf(val.get("width")), Integer.valueOf(val.get("height")), Base64.decode(val.get("returnval"), 0));
	}

	/**
	 * Load network properties
	 * @param adapter		the network adapter
	 * @param names  Property names to load, or empty for all
	 * @return
	 * @throws IOException
	 */
	public Properties getProperties(INetworkAdapter adapter, String...names) throws IOException {
		StringBuffer nameString = new StringBuffer();
		for(String name : names)
			Utils.appendWithComma(nameString, name);
		return getProperties(adapter.getProperties(nameString.toString()));
	}

	/**
	 * Load medium properties
	 * @param medium		the medium
	 * @param names  Property names to load, or empty for all
	 * @return	properties
	 * @throws IOException
	 */
	public Properties getProperties(IMedium medium, String...names) throws IOException {
		StringBuffer nameString = new StringBuffer();
		for(String name : names)
			Utils.appendWithComma(nameString, name);
		return getProperties(medium.getProperties(nameString.toString()) );
	}

	private Properties getProperties(Map<String, List<String>> val, String...names) throws IOException {
		List<String> returnNames = val.get("returnNames");
		List<String> values = val.get("returnval");
		Properties properties = new Properties();
		for(int i=0; i<returnNames.size(); i++)
			properties.put(returnNames.get(i), values.get(i));
		return properties;
	}

	public Tuple<IHostNetworkInterface, IProgress> createHostOnlyNetworkInterface(IHost host) throws IOException {
		Map<String, String> val = host.createHostOnlyNetworkInterface();
		IHostNetworkInterface networkInterface = getProxy(IHostNetworkInterface.class, val.get("hostInterface"));
		IProgress progress = getProxy(IProgress.class, val.get("returnval"));
		return new Tuple<IHostNetworkInterface, IProgress>(networkInterface, progress);
	}

	/**
	 * Searches a DHCP server settings to be used for the given internal network name. 
	 * <p><dl><dt><b>Expected result codes:</b></dt><dd><table><tbody><tr>
	 * <td>{@link IVirtualBox#E_INVALIDARG}</td><td>Host network interface <em>name</em> already exists.  </td></tr>
	 * </tbody></table></dd></dl></p>
	 * @param name		server name
	 * @param server	DHCP server settings
	 */
	public IDHCPServer findDHCPServerByNetworkName(String name) throws IOException {
		try {
			return getVBox().findDHCPServerByNetworkName(name);
		} catch(SoapFault e) {
			Log.e(TAG, "Couldn't find DHCP Server: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Ping a HTTPS server using SSL and prompt user to trust certificate
	 * @param handler {@link Handler} to prompt user to trust server certificate
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public void ping(Handler handler) throws IOException, XmlPullParserException {
		SerializationEnvelope envelope = new SerializationEnvelope(
				new SoapObject(NAMESPACE, "IManagedObjectRef_getInterfaceName").addProperty("_this", "0"));
		new InteractiveTrustedHttpsTransport(_server, TIMEOUT, handler).call(NAMESPACE+"IManagedObjectRef_getInterfaceName", envelope);
	}
}
