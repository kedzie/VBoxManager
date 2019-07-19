package com.kedzie.vbox.server

import android.app.Activity
import android.app.AlertDialog
import com.kedzie.vbox.soap.SSLUtil
import com.kedzie.vbox.soap.VBoxSvc
import kotlinx.coroutines.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import timber.log.Timber
import java.io.IOException
import java.security.KeyStore
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.coroutines.resume


suspend fun CoroutineScope.login(activity: Activity, server: Server, callback: (VBoxSvc) -> Unit) {
    if (server.isSSL) {
        val chain = suspendCancellableCoroutine<Array<X509Certificate>?> {
            val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
            envelope.isAddAdornments = false
            envelope.setOutputSoapObject(
                    SoapObject(VBoxSvc.NAMESPACE, "IManagedObjectRef_getInterfaceName")
                            .addProperty("_this", "0"))

            val trust = object : X509TrustManager {
                private val keystoreTM = SSLUtil.getKeyStoreTrustManager()[0] as X509TrustManager

                override fun getAcceptedIssuers(): Array<X509Certificate>? = null

                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                    Timber.i("checkServerTrusted(%d, %s)", chain.size, authType)
                    try {
                        keystoreTM.checkServerTrusted(chain, authType)
                        it.resume(null)
                    } catch (e: CertificateException) {
                        Timber.w("Untrusted Server %s", e.message)
                        it.resume(chain)
                        return
                    }
                }
            }

            val sc = SSLContext.getInstance("TLS")
            sc.init(null, arrayOf<TrustManager>(trust), java.security.SecureRandom())
            val client = OkHttpClient.Builder().sslSocketFactory(sc.socketFactory, trust).build()

            VBoxSvc.soapCall(client, server.uriString, "$VBoxSvc.NAMESPACEIManagedObjectRef_getInterfaceName", envelope).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Timber.w(e, "SSL ping error")
                }

                override fun onResponse(call: Call, response: Response) {
                    Timber.d("SSL ping success")
                }

            })
        }
        if (chain == null) {
            //we have no untrusted certs, login
            val vmgr = VBoxSvc(server)
            vmgr.logon()
            callback(vmgr)
        } else {
            val root = chain[chain.size - 1]
            val text = String.format("Issuer: %1\$s\nSubject: %2\$s", root.issuerDN.name, root.subjectDN.name)

            AlertDialog.Builder(activity)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Unrecognized Certificate")
                    .setMessage("Do you trust this certificate?\n$text")
                    .setPositiveButton("Trust") { dialog, which ->
                        //                try {
                        //                  Intent intent = KeyChain.createInstallIntent().putExtra(KeyChain.EXTRA_CERTIFICATE, root.getEncoded());
                        //                  Utils.startActivityForResult(activity, intent, REQUEST_CODE_KEYCHAIN);
                        //                } catch (CertificateEncodingException e) {
                        //                  Timber.e(e, "Error encoding certificate");
                        //                }
                        launch(Dispatchers.IO) {
                            val root = chain[chain.size - 1]
                            val alias = String.format("%1\$s-$2\$d", server.toString(), root.subjectDN.hashCode())
                            Timber.d( "Created new certificate entry alias: $alias")
                            SSLUtil.getKeystore().setEntry(alias, KeyStore.TrustedCertificateEntry(root), null)
                            SSLUtil.storeKeystore()

                            withContext(Dispatchers.Main) {
                                val vmgr = VBoxSvc(server)
                                vmgr.logon()
                                callback(vmgr)
                            }
                        }
                        dialog.dismiss()
                    }.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }.show()
        }
    }
    else {
        val vmgr = VBoxSvc(server)
        vmgr.logon()
        callback(vmgr)
    }
}

