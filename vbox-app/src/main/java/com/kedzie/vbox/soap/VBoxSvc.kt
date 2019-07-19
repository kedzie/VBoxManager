package com.kedzie.vbox.soap

import android.os.Parcel
import android.os.Parcelable
import com.kedzie.vbox.BuildConfig
import com.kedzie.vbox.api.IVirtualBox
import com.kedzie.vbox.api.IVirtualBoxProxy
import com.kedzie.vbox.server.Server
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.http.conn.ssl.AllowAllHostnameVerifier
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.kxml2.io.KXmlSerializer
import org.xmlpull.v1.XmlPullParserException
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.net.ssl.SSLContext

/**
 * VirtualBox JAX-WS API
 */
class VBoxSvc(val server: Server) : Parcelable {

    lateinit var vbox: IVirtualBox
    private var client: OkHttpClient

    init {
        Timber.i("Initializing Virtualbox API")
        client = if (this.server.isSSL) {
            val sc = SSLContext.getInstance("TLS")
            sc.init(null, SSLUtil.getKeyStoreTrustManager(), java.security.SecureRandom())
            OkHttpClient.Builder()
                    .sslSocketFactory(sc.socketFactory)
                    .hostnameVerifier(AllowAllHostnameVerifier()).apply {
                        if (BuildConfig.DEBUG) {
                            addInterceptor(HttpLoggingInterceptor().apply {
                                level = HttpLoggingInterceptor.Level.BODY
                            })
                        }
                    }
                    .build()
        } else {
            OkHttpClient.Builder().apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }.build()
        }
    }

    /**
     * Copy constructor
     * @param copy    The original [VBoxSvc] to copy
     */
    constructor(copy: VBoxSvc) : this(copy.server) {
        copy.vbox?.let {
            vbox = IVirtualBoxProxy(this, it.idRef)
        }
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(server, flags)
        dest.writeValue(vbox?.idRef)
    }

    /**
     * Connect to `vboxwebsrv` & initialize the VBoxSvc API interface
     * @return initialized [IVirtualBox] API interface
     * @throws IOException
     * @throws XmlPullParserException
     */
    suspend fun logon(): IVirtualBox {
        vbox = IVirtualBoxProxy(this, "").logon(server.username, server.password)
        return vbox!!
    }

    /**
     * Logoff from VirtualBox API
     * @throws IOException
     */

    suspend fun logoff() {
        vbox?.let {
            it.logoff()
        }
    }

    fun soapCall(soapAction: String, envelope: SoapEnvelope): Call {
        return soapCall(client, server.uriString, soapAction, envelope)
    }

    companion object {
        const val BUNDLE = "vmgr"
        const val NAMESPACE = "http://www.virtualbox.org/"
        private val LOADER = VBoxSvc::class.java.classLoader

        @JvmField
        val CREATOR: Parcelable.Creator<VBoxSvc> = object : Parcelable.Creator<VBoxSvc> {
            override fun createFromParcel(p: Parcel): VBoxSvc {
                val svc = VBoxSvc(p.readParcelable<Parcelable>(LOADER) as Server)
                p.readValue(LOADER)?.let {
                    svc.vbox = IVirtualBoxProxy(svc, it as String)
                }
                return svc
            }

            override fun newArray(size: Int): Array<VBoxSvc?> {
                return arrayOfNulls(size)
            }
        }

        private const val CONTENT_TYPE_XML_CHARSET_UTF_8 = "text/xml;charset=utf-8"
        private const val CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8 = "application/soap+xml;charset=utf-8"
        private const val USER_AGENT = "vbox-manager"
        private const val DEFAULT_BUFFER_SIZE = 256 * 1024 // 256 Kb

        fun soapCall(client: OkHttpClient, url: String, soapAction: String, envelope: SoapEnvelope): Call {
            val builder = Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MediaType.parse("text/xml"), createRequestData(envelope)))
                    .addHeader("content-type", "text/xml")

            builder.addHeader("User-Agent", USER_AGENT)
            // SOAPAction is not a valid header for VER12 so do not add
            // it
            // @see "http://code.google.com/p/ksoap2-android/issues/detail?id=67
            if (envelope.version != SoapSerializationEnvelope.VER12) {
                builder.addHeader("SOAPAction", soapAction)
            }

            builder.addHeader("Content-Type",
                    if (envelope.version == SoapSerializationEnvelope.VER12) {
                        CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8
                    } else {
                        CONTENT_TYPE_XML_CHARSET_UTF_8
                    })

            return client.newCall(builder.build())
        }

        /**
         * Serializes the request.
         */
        private fun createRequestData(envelope: SoapEnvelope): ByteArray {
            val bos = ByteArrayOutputStream(DEFAULT_BUFFER_SIZE)
            val xw = KXmlSerializer()
            xw.setOutput(bos, null)
            envelope.write(xw)
            xw.flush()
            bos.write('\r'.toInt())
            bos.write('\n'.toInt())
            bos.flush()
            return bos.toByteArray()
        }
    }
}
