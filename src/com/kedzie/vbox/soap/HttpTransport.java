package com.kedzie.vbox.soap;

import java.io.IOException;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;
import org.ksoap2.transport.ServiceConnectionSE;

/**
 * Don't reuse {@link ServiceConnection}s
 */
public class HttpTransport extends HttpTransportSE {

    public HttpTransport(String url, int timeout) {
        super(url, timeout);
    }

    public ServiceConnection getServiceConnection() throws IOException {
        return new ServiceConnectionSE(proxy, url, timeout);
    }
}
