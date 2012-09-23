package com.kedzie.vbox.soap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.ksoap2.HeaderProperty;
import org.ksoap2.transport.ServiceConnection;

import android.util.Log;

import com.kedzie.vbox.app.Utils;

/**
 * Accepts untrusted server certificates.
 * @author Marek Kedzierski
 */
public class TrustedHttpsServiceConnection implements ServiceConnection {
	private static final String TAG = "HttpsServiceConnectionSE";
	
    private HttpsURLConnection connection;
    
    private TrustManager []trustAll =  new TrustManager[]{
    	    new X509TrustManager() {
    	        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
    	        	Log.i(TAG, "getAcceptedIssuers");
    	            return null;
    	        }
    	        public void checkClientTrusted( java.security.cert.X509Certificate[] certs, String authType) {
    	        	Log.i(TAG, String.format("checkClientTrusted( %1$s, %2$s )", Utils.arrayToString(certs), authType));
    	        }
    	        public void checkServerTrusted( java.security.cert.X509Certificate[] certs, String authType) {
    	        	Log.i(TAG, String.format("checkServerTrusted( %1$s, %2$s )", Utils.arrayToString(certs), authType));
    	        }
    	    }
    	};

    /**
     * Create the transport with the supplied parameters.
     * @param host the name of the host e.g. webservices.somewhere.com
     * @param port the http port to connect on
     * @param file the path to the file on the webserver that represents the
     * webservice e.g. /api/services/myservice.jsp
     * @param timeout the timeout for the connection in milliseconds
     * @throws IOException
     */
    public TrustedHttpsServiceConnection(String host, int port, String file, int timeout) throws IOException {
    	 try {
    	       SSLContext sc = SSLContext.getInstance("TLS");
    	       sc.init(null, trustAll, new java.security.SecureRandom());
    	       HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    	    } catch (Exception e) {
    	       Log.e("ServiceConnectionSE", "Error init SSL", e);
    	    }
        connection = (HttpsURLConnection) new URL(TrustedHttpsTransport.PROTOCOL, host, port, file).openConnection();
        ((HttpsURLConnection) connection).setHostnameVerifier(new AllowAllHostnameVerifier());
        updateConnectionParameters(timeout);
    }

    private void updateConnectionParameters(int timeout) {
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout); // even if we connect fine we want to time out if we cant read anything..
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
   }

    public void connect() throws IOException {
        connection.connect();
    }

    public void disconnect() {
        connection.disconnect();
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public List getResponseProperties() {
        List retList = new LinkedList();
        for(Map.Entry entry : connection.getHeaderFields().entrySet()) {
        	for(Object val : (List)entry.getValue())
            	retList.add(new HeaderProperty((String) entry.getKey(), (String) val));
        }
        return retList;
    }

    public void setRequestProperty(String key, String value) {
        connection.setRequestProperty(key, value);
    }

    public void setRequestMethod(String requestMethod) throws IOException {
        connection.setRequestMethod(requestMethod);
    }

    public void setFixedLengthStreamingMode(int contentLength) {
        connection.setFixedLengthStreamingMode(contentLength);
    }

    public OutputStream openOutputStream() throws IOException {
        return connection.getOutputStream();
    }

    public InputStream openInputStream() throws IOException {
        return connection.getInputStream();
    }

    public InputStream getErrorStream() {
        return connection.getErrorStream();
    }

    public String getHost() {
        return connection.getURL().getHost();
    }

    public int getPort() {
        return connection.getURL().getPort();
    }

    public String getPath() {
        return connection.getURL().getPath();
    }

    public void setSSLSocketFactory(SSLSocketFactory sf) {
        connection.setSSLSocketFactory(sf);
    }
}
