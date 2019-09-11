package com.kedzie.vbox.machine

import androidx.lifecycle.*
import com.kedzie.vbox.api.*
import com.kedzie.vbox.api.jaxb.VBoxEventType
import com.kedzie.vbox.machine.group.GroupTreeNode
import com.kedzie.vbox.machine.group.MachineTreeNode
import com.kedzie.vbox.machine.group.TreeNode
import com.kedzie.vbox.machine.group.VMGroup
import com.kedzie.vbox.server.AppDatabase
import com.kedzie.vbox.server.Server
import com.kedzie.vbox.soap.VBoxSvc
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import timber.log.Timber

class MachineListViewModel(val database: AppDatabase,
                           val cacheDatabase: CacheDatabase) : ViewModel(), KoinComponent {

    val selectedNode = MutableLiveData<TreeNode?>()

    val machine = selectedNode.map {
        when(it) {
            is MachineTreeNode -> { it.machine }
            else -> { null }
        }
    }

    val group = selectedNode.map {
        when(it) {
            is GroupTreeNode -> { it.group }
            else -> { null }
        }
    }

    val servers = database.serverDao().getServers()

    val vbox = MutableLiveData<IVirtualBox?>()

    val events = vbox.switchMap {
        liveData<IEvent>(viewModelScope.coroutineContext) {
            it?.let { vbox ->
                val source = vbox.getEventSource()
                val listener = source.createListener()

                try {
                    while (true) {
                        source.getEvent(listener, 0)?.let {
                            when (it.getType()) {
                                VBoxEventType.ON_MACHINE_STATE_CHANGED -> {
                                    IMachineStateChangedEventProxy(vbox.api, cacheDatabase, it.idRef)
                                }
                                VBoxEventType.ON_SESSION_STATE_CHANGED -> {
                                    ISessionStateChangedEventProxy(vbox.api, cacheDatabase, it.idRef)
                                }
                                VBoxEventType.ON_SNAPSHOT_DELETED -> {
                                    ISnapshotDeletedEventProxy(vbox.api, cacheDatabase, it.idRef)
                                }
                                VBoxEventType.ON_SNAPSHOT_TAKEN -> {
                                    ISnapshotTakenEventProxy(vbox.api, cacheDatabase, it.idRef)
                                }
                                else -> {
                                    null
                                }
                            }?.let { event ->
                                emit(event)
                            }
                            source.eventProcessed(listener, it)
                        }
                        delay(500L)
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error")
                } finally {
                    source.unregisterListener(listener)
                }
            }
        }
    }

    suspend fun queryMetrics(target: String) =
            vbox.value!!.getPerformanceCollector().queryMetrics(target, "*:")

    //TODO enable metrics based on prefs
    fun enableMetrics(period: Int, count: Int) = viewModelScope.launch {
        Timber.i("Configuring metrics: period = %d, count = %d", period, count)
        vbox.value!!.getPerformanceCollector().apply {
            enableMetrics(arrayOf("*:"))
            setupMetrics(arrayOf("*:"), period, count)
        }
    }

    fun deleteServer(server: Server) = database.serverDao().delete(server)
}