package com.kedzie.vbox

import android.app.NotificationManager
import android.content.Context
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.kedzie.vbox.api.IMachine
import com.kedzie.vbox.machine.MachineListViewModel
import com.kedzie.vbox.server.AppDatabase
import com.kedzie.vbox.server.EditServerViewModel
import com.kedzie.vbox.server.Server
import com.kedzie.vbox.soap.VBoxSvc
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    factory { (context: Context) -> context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    factory { (context: Context) -> LocalBroadcastManager.getInstance(context) }

    factory { (context: Context) -> PreferenceManager.getDefaultSharedPreferences(context) }

    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "servers").build() }

    viewModel{ (vmgr: VBoxSvc?, machine: IMachine?) -> MachineListViewModel(vmgr, machine, get()) }

    viewModel{ (server: Server) -> EditServerViewModel(server, get()) }
}