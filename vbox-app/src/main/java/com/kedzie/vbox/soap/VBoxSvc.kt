package com.kedzie.vbox.soap

import com.kedzie.vbox.server.Server
import okhttp3.*
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.kxml2.io.KXmlSerializer
import java.io.ByteArrayOutputStream

/**
 * VirtualBox JAX-WS API
 */
class VBoxSvc(val server: Server,
              private val client: OkHttpClient) {

    fun soapCall(soapAction: String, envelope: SoapEnvelope): Call {
        return soapCall(client, server.uriString, soapAction, envelope)
    }

    companion object {
        const val NAMESPACE = "http://www.virtualbox.org/"

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
