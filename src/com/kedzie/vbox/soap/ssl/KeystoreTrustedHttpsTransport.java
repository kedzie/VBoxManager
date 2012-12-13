package com.kedzie.vbox.soap.ssl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;

import android.util.Log;

/**
 * HttpsTransportSE is a simple transport for https protocal based connections. It creates a #HttpsServiceConnectionSE
 * with the provided parameters.
 *
 * @author Manfred Moser <manfred@simpligility.com>
 */
public class KeystoreTrustedHttpsTransport extends HttpTransportSE{
	private static final String TAG = "KeystoreTrustedHttpsTransport";
	static final String PROTOCOL = "https";

	private ServiceConnection serviceConnection;
	private final String host;
	private final int port;
	private final String file;
	private final int timeout;
	
	private static TrustManager []trust =  new TrustManager[]{
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
    	        }
    	    }
    	};
	
	public KeystoreTrustedHttpsTransport (String host, int port, String file, int timeout) {
		super(KeystoreTrustedHttpsTransport.PROTOCOL + "://" + host + ":" + port + file);
		this.host = host;
		this.port = port;
		this.file = file;
		this.timeout = timeout;
		
		if(trust==null) {
			try {
				KeyStore keystore = KeyStore.getInstance("BKS");
				keystore.load(null, "password".toCharArray());
				TrustManagerFactory	tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				tmf.init(keystore);
				trust = tmf.getTrustManagers();
			} catch (Exception e) {
				Log.e(TAG, "Error loading KeyStore", e);
			}
		}
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
