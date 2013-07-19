package com.kedzie.vbox.soap;

import java.io.IOException;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;
import org.ksoap2.transport.ServiceConnectionSE;

import com.kedzie.vbox.server.Server;

/**
 * Don't reuse {@link ServiceConnection}s
 */
public class HttpTransport extends HttpTransportSE {

    public HttpTransport(Server server, int timeout) {
    	super(String.format("http://%s:%s", server.getHost(), server.getPort()), timeout);
    }

    public ServiceConnection getServiceConnection() throws IOException {
        return new ServiceConnectionSE(proxy, url, timeout);
    }
}
