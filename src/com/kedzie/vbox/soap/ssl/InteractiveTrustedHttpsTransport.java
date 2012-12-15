package com.kedzie.vbox.soap.ssl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;

import android.os.Handler;
import android.util.Log;

import com.kedzie.vbox.app.BundleBuilder;

/**
 * Uses {@link TrustManager} which sends certificates to handler for user response
 */
public class InteractiveTrustedHttpsTransport extends HttpTransportSE{
	private static final String TAG = "InteractiveTrustedHttpsTransport";
	static final String PROTOCOL = "https";

	private ServiceConnection serviceConnection = null;
	private final String host;
	private final int port;
	private final String file;
	private final int timeout;
	private Handler handler;
	private TrustManager []trust =  new TrustManager[]{
    	    new X509TrustManager() {
    	        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
    	        	Log.i(TAG, "getAcceptedIssuers");
    	            return null;
    	        }
    	        public void checkClientTrusted( java.security.cert.X509Certificate[] certs, String authType) {
    	        	Log.i(TAG, String.format("checkClientTrusted(%1$d, %2$s)", certs.length, authType));
    	        }
    	        public void checkServerTrusted( java.security.cert.X509Certificate[] certs, String authType) {
    	        	Log.i(TAG, String.format("checkServerTrusted(%1$d, %2$s)", certs.length, authType));
    	        	new BundleBuilder()
    	        		.putSerializable("certs", certs)
    	        		.putString("authType", authType)
    	        		.sendMessage(handler, 0);
    	        }
    	    }
    	};

	public InteractiveTrustedHttpsTransport (String host, int port, String file, int timeout, Handler handler) {
		super(InteractiveTrustedHttpsTransport.PROTOCOL + "://" + host + ":" + port + file);
		this.host = host;
		this.port = port;
		this.file = file;
		this.timeout = timeout;
		this.handler=handler;
	}

	/**
	 * Returns the HttpsServiceConnectionSE and creates it if necessary
	 * @see org.ksoap2.transport.HttpsTransportSE#getServiceConnection()
	 */
	public ServiceConnection getServiceConnection() throws IOException {
		serviceConnection = new TrustedHttpsServiceConnection(host, port, file, timeout, trust);
		return serviceConnection;
	}

	public String getHost() {
		String retVal = null;
		try {
			retVal = new URL(url).getHost();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return retVal;
	}

	public int getPort() {
		int retVal = -1;
		try {
			retVal = new URL(url).getPort();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return retVal;
	}

	public String getPath() {
		String retVal = null;
		try {
			retVal = new URL(url).getPath();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return retVal;
	}
}
