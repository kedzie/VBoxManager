package com.kedzie.vbox

import android.app.NotificationManager
import android.content.Context
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.kedzie.vbox.api.CacheDatabase
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.machine.MachineListActivity
import com.kedzie.vbox.machine.MachineListViewModel
import com.kedzie.vbox.server.AppDatabase
import com.kedzie.vbox.server.EditServerViewModel
import com.kedzie.vbox.server.Server
import com.kedzie.vbox.soap.SSLUtil
import com.kedzie.vbox.soap.VBoxSvc
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.http.conn.ssl.AllowAllHostnameVerifier
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import javax.net.ssl.SSLContext

val appModule = module {

    factory { (context: Context) -> context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    factory { (context: Context) -> LocalBroadcastManager.getInstance(context) }

    factory { (context: Context) -> PreferenceManager.getDefaultSharedPreferences(context) }

    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "servers").build() }

    single { Room.databaseBuilder(androidContext(), CacheDatabase::class.java, "cache").build() }

    factory { (server: Server) -> if (server.isSSL) {
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
    } }

    factory { (server: Server) -> VBoxSvc(server, get { parametersOf(server) })}

    viewModel{ (server: Server) -> EditServerViewModel(server, get()) }

    viewModel{ MachineListViewModel(get(), get()) }

}