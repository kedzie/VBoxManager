package com.kedzie.vbox.soap;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import com.google.common.base.Throwables;
import com.kedzie.vbox.api.BaseProxy;
import com.kedzie.vbox.api.IDHCPServer;
import com.kedzie.vbox.api.IDisplay;
import com.kedzie.vbox.api.IEvent;
import com.kedzie.vbox.api.IMachine;
import com.kedzie.vbox.api.IMachineStateChangedEvent;
import com.kedzie.vbox.api.IManagedObjectRef;
import com.kedzie.vbox.api.IMedium;
import com.kedzie.vbox.api.INetworkAdapter;
import com.kedzie.vbox.api.ISession;
import com.kedzie.vbox.api.ISessionStateChangedEvent;
import com.kedzie.vbox.api.ISnapshotDeletedEvent;
import com.kedzie.vbox.api.ISnapshotTakenEvent;
import com.kedzie.vbox.api.IVirtualBox;
import com.kedzie.vbox.api.Screenshot;
import com.kedzie.vbox.api.jaxb.BitmapFormat;
import com.kedzie.vbox.api.jaxb.LockType;
import com.kedzie.vbox.api.jaxb.MachineState;
import com.kedzie.vbox.api.jaxb.VBoxEventType;
import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.app.Utils;
import com.kedzie.vbox.metrics.MetricQuery;
import com.kedzie.vbox.server.Server;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.ConnectException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import kotlinx.coroutines.CompletableDeferred;
import kotlinx.coroutines.Deferred;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import timber.log.Timber;

/**
 * VirtualBox JAX-WS API
 */
public class VBoxSvc implements Parcelable {
	public static final String BUNDLE = "vmgr";
	public static final String NAMESPACE = "http://www.virtualbox.org/";
	private static final ClassLoader LOADER = VBoxSvc.class.getClassLoader();

	public static final Parcelable.Creator<VBoxSvc> CREATOR = new Parcelable.Creator<VBoxSvc>() {
		public VBoxSvc createFromParcel(Parcel in) {
			VBoxSvc svc = new VBoxSvc((Server)in.readParcelable(LOADER));
			String vboxId = in.readString();
			svc._vbox = new IVirtualBoxProxy(svc, vboxId);
			return svc;
		}
		public VBoxSvc[] newArray(int size) {
			return new VBoxSvc[size];
		}
	};

	private Server _server;
	private IVirtualBox _vbox;
	OkHttpClient client;

	/**
	 * @param server	VirtualBox webservice server
	 */
	public VBoxSvc(Server server) {
		_server=server;
		Timber.i( "Initializing Virtualbox API");
		if(_server.isSSL()) {
			try {
				SSLContext sc = SSLContext.getInstance("TLS");
				sc.init(null, SSLUtil.getKeyStoreTrustManager(), new java.security.SecureRandom());
				client = new OkHttpClient.Builder()
                        .sslSocketFactory(sc.getSocketFactory())
                        .hostnameVerifier(new AllowAllHostnameVerifier())
                        .build();
			} catch (Exception e) {
				Timber.e(e, "error");
				throw new RuntimeException(e);
			}
		} else {
			client = new OkHttpClient.Builder().build();
		}
	}

	/**
	 * Copy constructor
	 * @param copy	The original {@link VBoxSvc} to copy
	 */
	public VBoxSvc(VBoxSvc copy) {
		this(copy._server);
		_vbox = new IVirtualBoxProxy(this, copy._vbox.getIdRef());
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

	protected static final String CONTENT_TYPE_XML_CHARSET_UTF_8 = "text/xml;charset=utf-8";
	protected static final String CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8 = "application/soap+xml;charset=utf-8";
	protected static final String USER_AGENT = "vbox-manager";
	public static final int DEFAULT_BUFFER_SIZE = 256*1024; // 256 Kb

	public static Call soapCall(OkHttpClient client, String url, String soapAction, SoapEnvelope envelope) throws IOException {
		if (soapAction == null) {
			soapAction = "\"\"";
		}

		Request.Builder builder = new Request.Builder()
				.url(url)
				.post(RequestBody.create(MediaType.parse("text/xml"), createRequestData(envelope)))
				.addHeader("content-type", "text/xml");

		builder.addHeader("User-Agent", USER_AGENT);
		// SOAPAction is not a valid header for VER12 so do not add
		// it
		// @see "http://code.google.com/p/ksoap2-android/issues/detail?id=67
		if (envelope.version != SoapSerializationEnvelope.VER12) {
			builder.addHeader("SOAPAction", soapAction);
		}

		if (envelope.version == SoapSerializationEnvelope.VER12) {
			builder.addHeader("Content-Type", CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8);
		} else {
			builder.addHeader("Content-Type", CONTENT_TYPE_XML_CHARSET_UTF_8);
		}

		return client.newCall(builder.build());
	}

	public Call soapCall(String soapAction, SoapEnvelope envelope) throws IOException {
		return soapCall(client, _server.toUriString(), soapAction, envelope);
	}

	/**
	 * Serializes the request.
	 */
	private static byte[] createRequestData(SoapEnvelope envelope) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
		XmlSerializer xw = new KXmlSerializer();
		xw.setOutput(bos, null);
		envelope.write(xw);
		xw.flush();
		bos.write('\r');
		bos.write('\n');
		bos.flush();
		return bos.toByteArray();
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

	/**
	 * Connect to <code>vboxwebsrv</code> & initialize the VBoxSvc API interface
	 * @return initialized {@link IVirtualBox} API interface
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public IVirtualBox logon() throws IOException  {
		try {
			return (_vbox = new IVirtualBoxProxy(this, null).logon(_server.getUsername(), _server.getPassword()));
		} catch(SoapFault e) {
			Timber.e(e, "Logon error");
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
			q.name= data.get("returnMetricNames").get(i);
			q.object= data.get("returnObjects").get(i);
			q.scale= Integer.valueOf(data.get("returnScales").get(i));
			q.unit= data.get("returnUnits").get(i);
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
				return new Screenshot(width, height, display.takeScreenShotToArray(0, width, height, BitmapFormat.PNG));
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
			return new Screenshot(width, height, session.getConsole().getDisplay().takeScreenShotToArray(0, width, height, BitmapFormat.PNG));
		} finally {
			session.unlockMachine();
		}
	}

	public static Screenshot readSavedScreenshot(IMachine machine, int screenId) throws IOException {
		Map<String, List<String>> info = machine.querySavedScreenshotInfo(screenId);
		List<BitmapFormat> formats = new ArrayList<>();
		for(String val : info.get("returnval")) {
			formats.add( BitmapFormat.fromValue(val) );
		}
		//prefer jpeg, otherwise pick first available screenshot
		BitmapFormat format = formats.contains(BitmapFormat.JPEG) ? BitmapFormat.JPEG : formats.get(0);
		Map<String, String> val = machine.readSavedScreenshotToArray(screenId, format);
		return new Screenshot(Integer.valueOf(val.get("width")), Integer.valueOf(val.get("height")), Base64.decode(val.get("returnval"), 0));
	}

	/**
	 * Load network properties
	 * @param adapter		the network adapter
	 * @param names  Property names to load, or empty for all
	 * @return
	 * @throws IOException
	 */
	public static Properties getProperties(INetworkAdapter adapter, String...names) {
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
	public static Properties getProperties(IMedium medium, String...names) {
		StringBuffer nameString = new StringBuffer();
		for(String name : names)
			Utils.appendWithComma(nameString, name);
		return getProperties(medium.getProperties(nameString.toString()) );
	}

	private static Properties getProperties(Map<String, List<String>> val) {
		List<String> returnNames = val.get("returnNames");
		List<String> values = val.get("returnval");
		Properties properties = new Properties();
		for(int i=0; i<returnNames.size(); i++)
			properties.put(returnNames.get(i), values.get(i));
		return properties;
	}

	/**
	 * Searches a DHCP server settings to be used for the given internal network name.
	 * @param name		server name
	 */
	public IDHCPServer findDHCPServerByNetworkName(String name) throws IOException {
		try {
			return getVBox().findDHCPServerByNetworkName(name);
		} catch(SoapFault e) {
			Timber.e("Couldn't find DHCP Server: %s", e.getMessage());
			return null;
		}
	}

	/**
	 * Ping a HTTPS server using SSL and prompt user to trust certificate
	 * @param handler {@link Handler} to prompt user to trust server certificate
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public void pingInteractiveTLS(Handler handler) throws IOException, NoSuchAlgorithmException, KeyManagementException {
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setAddAdornments(false);
		envelope.setOutputSoapObject(
				new SoapObject(NAMESPACE, "IManagedObjectRef_getInterfaceName").addProperty("_this", "0"));

		X509TrustManager trust = new X509TrustManager() {

			private X509TrustManager _keystoreTM = (X509TrustManager)SSLUtil.getKeyStoreTrustManager()[0];

			@Override public X509Certificate[] getAcceptedIssuers() { return null; }
			@Override public void checkClientTrusted(X509Certificate[] chain, String authType) {}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) {
				Timber.i("checkServerTrusted(%1$d, %2$s)", chain.length, authType);
				try {
					_keystoreTM.checkServerTrusted(chain, authType);
				} catch(CertificateException e) {
					Timber.w( "Untrusted Server %s",  e.getMessage());
					new BundleBuilder()
							.putParcelable(Server.BUNDLE, _server)
							.putBoolean("isTrusted", false)
							.putSerializable("certs", chain)
							.sendMessage(handler, 0);
					return;
				}
				new BundleBuilder()
						.putParcelable(Server.BUNDLE, _server)
						.putBoolean("isTrusted", true)
						.sendMessage(handler, 0);
			}
		};

		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, new TrustManager[]{trust}, new java.security.SecureRandom());
		OkHttpClient client = new OkHttpClient.Builder().sslSocketFactory(sc.getSocketFactory(), trust).build();

		soapCall(client, _server.toUriString(), NAMESPACE+"IManagedObjectRef_getInterfaceName", envelope);
	}
}
