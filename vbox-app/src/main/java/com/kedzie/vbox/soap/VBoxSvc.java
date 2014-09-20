package com.kedzie.vbox.soap;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.kedzie.vbox.BuildConfig;
import com.kedzie.vbox.api.*;
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

    private Map<String, Constructor<? extends BaseProxy>> proxyCache = new HashMap<String, Constructor<? extends BaseProxy>>();
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
        Log.i(TAG, "Initializing Virtualbox API");
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

    /**
     * Make HTTP request using transport from the pool
     * @param request
     * @param envelope
     * @throws IOException
     */
    public void httpCall(String request, SoapSerializationEnvelope envelope) throws IOException {
        HttpTransportSE transport = null;
        try {
            transport = _transportPool.getObj();
            transport.call(request, envelope);
        } catch (Throwable e) {
            Log.e(TAG, "Exception", e);
            Throwables.propagateIfInstanceOf(e, IOException.class);
        } finally {
            if(transport!=null)
                _transportPool.returnObj(transport);
        }
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
		Log.w(TAG, "===========VBoxSvc being SERIALIZED===========");
		output.writeObject(_server);
		output.writeUTF(_vbox.getIdRef());
	}

	/**
	 * Create remote-invocation proxy w/o cached properties
	 * @param clazz 		type of {@link IManagedObjectRef}
	 * @param id			UIUD of {@link IManagedObjectRef}
	 * @return 				remote invocation proxy
	 */
	public <T extends IManagedObjectRef> T getProxy(Class<T> clazz, String id) {
		return getProxy(clazz, id, null);
	}

    /**
     * Create remote-invocation proxy w/cached properties
     * @param clazz 		type of {@link IManagedObjectRef}
     * @param id 			UIUD of {@link IManagedObjectRef}
     * @param cache			cached properties
     * @return 				remote invocation proxy
     */
    public <T extends IManagedObjectRef> T getProxy(Class<T> clazz, String id, Map<String, Object> cache) {
        String proxyClassName = clazz.getName()+"$$Proxy";
        try {
            synchronized (proxyCache) {
                if (!proxyCache.containsKey(proxyClassName)) {
                    Class<? extends BaseProxy> proxyClazz = (Class<? extends BaseProxy>) Class.forName(proxyClassName);
                    Constructor<? extends BaseProxy> constructor = proxyClazz.getDeclaredConstructor(VBoxSvc.class, String.class, Class.class, Map.class);
                    proxyCache.put(proxyClassName, constructor);
                }
            }
            T proxy = clazz.cast(proxyCache.get(proxyClassName).newInstance(this, id, clazz, cache));

            if(IEvent.class.equals(clazz)) {
                VBoxEventType type = ((IEvent)proxy).getType();
                if(type.equals(VBoxEventType.ON_MACHINE_STATE_CHANGED))
                    return clazz.cast(getProxy(IMachineStateChangedEvent.class, id, cache));
                else if(type.equals(VBoxEventType.ON_SESSION_STATE_CHANGED))
                    return clazz.cast(getProxy(ISessionStateChangedEvent.class, id, cache));
                else if(type.equals(VBoxEventType.ON_SNAPSHOT_DELETED))
                    return clazz.cast(getProxy(ISnapshotDeletedEvent.class, id, cache));
                else if(type.equals(VBoxEventType.ON_SNAPSHOT_TAKEN))
                    return clazz.cast(getProxy(ISnapshotTakenEvent.class, id, cache));
            }
            return proxy;
        } catch (Throwable e) {
            throw Throwables.propagate(e);
        }
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

	private Properties getProperties(Map<String, List<String>> val) throws IOException {
		List<String> returnNames = val.get("returnNames");
		List<String> values = val.get("returnval");
		Properties properties = new Properties();
		for(int i=0; i<returnNames.size(); i++)
			properties.put(returnNames.get(i), values.get(i));
		return properties;
	}

	public Tuple<IHostNetworkInterface, IProgress> createHostOnlyNetworkInterface(IHost host) throws IOException {
		Map<String, String> val = host.createHostOnlyNetworkInterface();
        return new Tuple<IHostNetworkInterface, IProgress>(
                getProxy(IHostNetworkInterface.class, val.get("hostInterface")),
                getProxy(IProgress.class, val.get("returnval")));
	}

	/**
	 * Searches a DHCP server settings to be used for the given internal network name. 
	 * <p><dl><dt><b>Expected result codes:</b></dt><dd><table><tbody><tr>
	 * <td>{@link IVirtualBox#E_INVALIDARG}</td><td>Host network interface <em>name</em> already exists.  </td></tr>
	 * </tbody></table></dd></dl></p>
	 * @param name		server name
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
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setAddAdornments(false);
        envelope.setOutputSoapObject(
                new SoapObject(NAMESPACE, "IManagedObjectRef_getInterfaceName").addProperty("_this", "0"));
		new InteractiveTrustedHttpsTransport(_server, TIMEOUT, handler).call(NAMESPACE+"IManagedObjectRef_getInterfaceName", envelope);
	}
}
