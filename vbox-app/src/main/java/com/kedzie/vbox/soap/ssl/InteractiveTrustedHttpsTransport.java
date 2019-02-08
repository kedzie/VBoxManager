package com.kedzie.vbox.soap.ssl;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;

import android.os.Handler;
import android.util.Log;

import com.kedzie.vbox.app.BundleBuilder;
import com.kedzie.vbox.server.Server;

import timber.log.Timber;

/**
 * Uses {@link TrustManager} which sends certificates to handler for user response
 */
public class InteractiveTrustedHttpsTransport extends HttpTransportSE{
	private static final String TAG = "InteractiveTrustedHttpsTransport";
	static final String PROTOCOL = "https";

	private ServiceConnection serviceConnection = null;
	protected final Server server;
	private final int timeout;
	protected Handler handler;

	private TrustManager []trust =  new TrustManager[]{
			new X509TrustManager() {

				private X509TrustManager _keystoreTM = (X509TrustManager)SSLUtil.getKeyStoreTrustManager()[0];

				@Override public X509Certificate[] getAcceptedIssuers() { return null; }
				@Override public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException{}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException{
					Timber.i("checkServerTrusted(%1$d, %2$s)", chain.length, authType);
					try {
						_keystoreTM.checkServerTrusted(chain, authType);
					} catch(CertificateException e) {
						Timber.w( "Untrusted Server %s",  e.getMessage());
						new BundleBuilder()
							.putParcelable(Server.BUNDLE, server)
							.putBoolean("isTrusted", false)
							.putSerializable("certs", chain)
							.sendMessage(handler, 0);
						return;
					}
					new BundleBuilder()
						.putParcelable(Server.BUNDLE, server)
						.putBoolean("isTrusted", true)
						.sendMessage(handler, 0);
				}
			}
	};

	public InteractiveTrustedHttpsTransport (Server server, int timeout, Handler handler) {
		super(InteractiveTrustedHttpsTransport.PROTOCOL + "://" + server.getHost() + ":" + server.getPort());
		this.server = server;
		this.timeout = timeout;
		this.handler=handler;
	}

	/**
	 * Returns the HttpsServiceConnectionSE and creates it if necessary
	 * @see org.ksoap2.transport.HttpsTransportSE#getServiceConnection()
	 */
	public ServiceConnection getServiceConnection() throws IOException {
		serviceConnection = new TrustedHttpsServiceConnection(server.getHost(), server.getPort(), "", timeout, trust);
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
