package com.kedzie.vbox.soap;

import android.util.Log;

import com.kedzie.vbox.VBoxApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Manipulate/load/save truststore to filesystem.  Create TrustManagers to use the keystore.
 */
public class SSLUtil {
	private static final String TAG = "SSLUtil";

	private static final char[] KEYSTORE_PASSWORD = "virtualbox".toCharArray();
	private static final String KEYSTORE_NAME = "virtualbox.bks";

	private static TrustManager[] mTrustManagers;
	private static KeyStore mKeystore;

	public static TrustManager[] getKeyStoreTrustManager() {
		if(mTrustManagers==null) {
			Log.i(TAG, "Initializing TrustManagers");
			try {
				TrustManagerFactory	tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				tmf.init(SSLUtil.getKeystore());
				mTrustManagers = tmf.getTrustManagers();
			} catch (Exception e) {
				Log.e(TAG, "Error initializing TrustManagers", e);
			}
		}
		return mTrustManagers;
	}

	public static KeyStore getKeystore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, NoSuchProviderException, IOException {
		if(mKeystore==null) {
//			mKeystore = KeyStore.getInstance("AndroidKeyStore");
//			mKeystore.load(null);
//			Enumeration<String> aliases = mKeystore.aliases();
//			while(aliases.hasMoreElements()) {
//				Timber.d("Alias: " + aliases.nextElement());
//			}
			mKeystore = KeyStore.getInstance("BKS");
			if(!new File(getKeystorePath()).exists()) {
				Log.i(TAG, "Creating new Bouncy Castle keystore");
				getKeystoreFolder().mkdirs();
				mKeystore.load(null, KEYSTORE_PASSWORD);
				mKeystore.store(new FileOutputStream(getKeystorePath()), KEYSTORE_PASSWORD);
			} else {
				mKeystore.load(new FileInputStream(getKeystorePath()), KEYSTORE_PASSWORD);
			}
		}
		return mKeystore;
	}

	public static void storeKeystore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {
		Log.i(TAG, "Saving updated keystore: " + getKeystorePath());
		mKeystore.store(new FileOutputStream(getKeystorePath()), KEYSTORE_PASSWORD);
		mTrustManagers = null;  //make sure TrustManagers are using the updated keystore
	}

	private static File getKeystoreFolder() {
		return VBoxApplication.getInstance().getFilesDir();
	}

	private static String getKeystorePath() {
		return getKeystoreFolder().toString()+"/"+KEYSTORE_NAME;
	}
}
