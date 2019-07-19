package com.kedzie.vbox.server

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kedzie.vbox.soap.VBoxSvc
import kotlinx.coroutines.launch
import timber.log.Timber

class EditServerViewModel(val server: Server, private val database: AppDatabase) : ViewModel() {

    fun saveServer() = viewModelScope.launch {
        Timber.d("Saving server %s", server)
        database.serverDao().insert(server)
    }

    fun deleteServer() = viewModelScope.launch {
        Timber.d("Deleting server %s", server)
        database.serverDao().delete(server)
    }
}