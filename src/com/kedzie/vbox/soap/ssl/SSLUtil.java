package com.kedzie.vbox.soap.ssl;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SSLUtil {
	private static TrustManagerFactory tmf;

	public static SSLSocketFactory getSSLSocketFactory(KeyStore keystore) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		if(tmf==null)
			tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(keystore);
		SSLContext context = SSLContext.getInstance("SSL");
		context.init(null, tmf.getTrustManagers(), null);
		return context.getSocketFactory();
	}

}
