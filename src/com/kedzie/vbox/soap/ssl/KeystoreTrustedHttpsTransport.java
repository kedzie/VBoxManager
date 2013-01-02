package com.kedzie.vbox.soap.ssl;

import java.io.FileInputStream;
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
	private static TrustManager []trust;
	
	public KeystoreTrustedHttpsTransport (String host, int port, String file, int timeout) {
		super(KeystoreTrustedHttpsTransport.PROTOCOL + "://" + host + ":" + port + file);
		this.host = host;
		this.port = port;
		this.file = file;
		this.timeout = timeout;
	}

	/**
	 * Returns the HttpsServiceConnectionSE and creates it if necessary
	 * @see org.ksoap2.transport.HttpsTransportSE#getServiceConnection()
	 */
	public ServiceConnection getServiceConnection() throws IOException {
		serviceConnection = new TrustedHttpsServiceConnection(host, port, file, timeout, getTrustManager());
		return serviceConnection;
	}
	
	private static TrustManager[] getTrustManager() {
		if(trust==null) {
			try {
				KeyStore keystore = KeyStore.getInstance("BKS");
				keystore.load(new FileInputStream("/sdcard/virtualbox.bks"), "virtualbox".toCharArray());
				TrustManagerFactory	tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				tmf.init(keystore);
				trust = tmf.getTrustManagers();
			} catch (Exception e) {
				Log.e(TAG, "Error loading KeyStore", e);
			}
		}
		return trust;
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
