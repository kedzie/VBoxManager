package com.kedzie.vbox.machine.group

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.kedzie.vbox.api.CacheDatabase

class MachineGroupListViewModel(val group: String, cacheDatabase: CacheDatabase) : ViewModel() {

    val treeNodes: LiveData<List<TreeNode>> = cacheDatabase.VBoxDao().getMachinesByGroup(group).switchMap {
        liveData {
            val ret = mutableListOf<TreeNode>()

            emit(ret)
        }
    }

}