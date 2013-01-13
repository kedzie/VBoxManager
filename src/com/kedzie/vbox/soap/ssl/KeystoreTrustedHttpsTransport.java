package com.kedzie.vbox.soap.ssl;

import java.io.IOException;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;

import com.kedzie.vbox.server.Server;

/**
 *
 */
public class KeystoreTrustedHttpsTransport extends HttpTransportSE{
	static final String PROTOCOL = "https";

	private ServiceConnection serviceConnection;
	private final Server server;
	private final int timeout;
	
	public KeystoreTrustedHttpsTransport (Server server, int timeout) {
		super(KeystoreTrustedHttpsTransport.PROTOCOL + "://" + server.getHost() + ":" + server.getPort());
		this.server=server;
		this.timeout = timeout;
	}

	/**
	 * Returns the HttpsServiceConnectionSE and creates it if necessary
	 * @see org.ksoap2.transport.HttpsTransportSE#getServiceConnection()
	 */
	public ServiceConnection getServiceConnection() throws IOException {
		serviceConnection = new TrustedHttpsServiceConnection(server.getHost(), server.getPort(), "", timeout, SSLUtil.getKeyStoreTrustManager());
		return serviceConnection;
	}
	
	public String getHost() {
		return server.getHost();
	}

	public int getPort() {
		return server.getPort();
	}

	public String getPath() {
		return "";
	}
}
