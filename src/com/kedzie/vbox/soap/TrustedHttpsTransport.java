package com.kedzie.vbox.soap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;

/**
 * HttpsTransportSE is a simple transport for https protocal based connections. It creates a #HttpsServiceConnectionSE
 * with the provided parameters.
 *
 * @author Manfred Moser <manfred@simpligility.com>
 */
public class TrustedHttpsTransport extends HttpTransportSE{

    static final String PROTOCOL = "https";

    private ServiceConnection serviceConnection = null;
    private final String host;
    private final int port;
    private final String file;
    private final int timeout;

    public TrustedHttpsTransport (String host, int port, String file, int timeout) {
        super(TrustedHttpsTransport.PROTOCOL + "://" + host + ":" + port + file);
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
//        if (serviceConnection == null) 
            serviceConnection = new TrustedHttpsServiceConnection(host, port, file, timeout);
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
