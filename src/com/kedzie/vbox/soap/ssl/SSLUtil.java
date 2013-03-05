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

import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.kedzie.vbox.R;
import com.kedzie.vbox.VBoxApplication;
import com.kedzie.vbox.server.Server;
import com.kedzie.vbox.task.DialogTask;

/**
 * Manipulate/load/save truststore to filesystem.  Create TrustManagers to use the keystore.
 */
public class SSLUtil {
	private static final String TAG = "SSLUtil";
	
	private static final char[] KEYSTORE_PASSWORD = "virtualbox".toCharArray();
	private static final String KEYSTORE_NAME = "virtualbox.bks";

	/**
	 * Add certificate to the keystore and save
	 */
	public static class AddCertificateToKeystoreTask extends DialogTask<X509Certificate, Void> {

		private Server server;

		public AddCertificateToKeystoreTask(SherlockFragmentActivity context, Server server) {
			super(context, null, R.string.progress_updating_keystore);
			this.server = server;
		}

		@Override
		protected Void work(X509Certificate... chain) throws Exception {
			X509Certificate root = chain[chain.length-1];
			String alias = server.toString() + "-" + String.format("Issuer: %1$s\tSubject: %2$s", root.getIssuerDN().getName(), root.getSubjectDN().getName());
			Log.d(TAG, "Created new certificate entry alias: " + alias);
			SSLUtil.getKeystore().setEntry(alias, new KeyStore.TrustedCertificateEntry(root), null);
			SSLUtil.storeKeystore();
			return null;
		}
	}

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

	public static KeyStore getKeystore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException {
		if(mKeystore==null) {
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
		return VBoxApplication.getInstance().getExternalFilesDir(null);
	}
	
	private static String getKeystorePath() {
		return getKeystoreFolder().toString()+"/"+KEYSTORE_NAME;
	}
}
