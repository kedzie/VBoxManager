package com.kedzie.vbox.soap.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import android.content.Context;
import android.util.Log;

import com.kedzie.vbox.server.Server;
import com.kedzie.vbox.task.DialogTask;

public class SSLUtil {
	private static final char[] KEYSTORE_PASSWORD = "virtualbox".toCharArray();
	private static final String KEYSTORE_PATH = "/sdcard/VirtualBox/";
	private static final String KEYSTORE_NAME = "virtualbox.bks";

	private static final String TAG = "SSLUtil";
	
	private static TrustManager []_trust; 
	private static KeyStore _keystore;

	public static TrustManager[] getKeyStoreTrustManager() {
		if(_trust==null) {
			Log.i(TAG, "Initializing TrustManagers");
			try {
				TrustManagerFactory	tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				tmf.init(SSLUtil.getKeystore());
				_trust = tmf.getTrustManagers();
			} catch (Exception e) {
				Log.e(TAG, "Error loading KeyStore", e);
			}
		}
		return _trust;
	}
	
	public static KeyStore getKeystore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {
		if(_keystore==null) {
			_keystore = KeyStore.getInstance("BKS");
			if(!new File(KEYSTORE_PATH+KEYSTORE_NAME).exists()) {
				Log.i(TAG, "Creating new Bouncy Castle keystore");
				new File(KEYSTORE_PATH).mkdirs();
				_keystore.load(null, KEYSTORE_PASSWORD);
				_keystore.store(new FileOutputStream(KEYSTORE_PATH+KEYSTORE_NAME), KEYSTORE_PASSWORD);
			} else {
				_keystore.load(new FileInputStream(KEYSTORE_PATH+KEYSTORE_NAME), KEYSTORE_PASSWORD);
			}
		}
		return _keystore;
	}
	
	public static void storeKeystore(KeyStore ks) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {
		Log.i(TAG, "Saving updated keystore");
		ks.store(new FileOutputStream(KEYSTORE_PATH+KEYSTORE_NAME), KEYSTORE_PASSWORD);
		_keystore = ks;
		_trust = null;  //make sure TrustManagers are using the updated keystore
	}
	
	public static class AddCertificateToKeystoreTask extends DialogTask<X509Certificate, Boolean> {
		
		private Server server;
		
		public AddCertificateToKeystoreTask(Context context, Server server) {
			super("AddCertificateToKeystoreTask", context, null, "Updating Keystore");
			this.server = server;
		}
		
		@Override
		protected Boolean work(X509Certificate... chain) throws Exception {
			Log.d(TAG, "Certificate Chain");
			Log.d(TAG, "==================");
			for(X509Certificate cert : chain)
				Log.d(TAG, String.format("Issuer: %1$s\tSubject: %2$s", cert.getIssuerDN().getName(), cert.getSubjectDN().getName()));
			Log.d(TAG, "==================");
			
			X509Certificate root = chain[chain.length-1];
			String alias = server.toString() + "-" + root.getIssuerX500Principal().getName();
			Log.d(TAG, "Certificate Alias: " + alias);
			KeyStore ks = SSLUtil.getKeystore();
			ks.setEntry(alias, new KeyStore.TrustedCertificateEntry(root), null);
			SSLUtil.storeKeystore(ks);
			return true;
		}
	}
}
