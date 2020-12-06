package com.kedzie.vbox.machine.group

import androidx.lifecycle.*
import com.kedzie.vbox.api.CacheDatabase
import com.kedzie.vbox.api.IMachineEntity
import com.kedzie.vbox.api.IVirtualBox

class MachineGroupListViewModel(val vbox: IVirtualBox, val group: String, cacheDatabase: CacheDatabase) : ViewModel() {

    val machines = cacheDatabase.VBoxDao().getMachinesByGroup(group)

    val groups = cacheDatabase.VBoxDao().getGroupsByGroup(group).switchMap {
        liveData {
            emit(it.map { VMGroup(it, cacheDatabase.VBoxDao().getGroupCountByGroup(it), cacheDatabase.VBoxDao().getMachineCountByGroup(it)) })
        }
    }

    val treeNodes = MediatorLiveData<Pair<List<IMachineEntity>, List<VMGroup>>>().apply {
        addSource(machines) {
            value = it to (value?.second ?: emptyList())
        }
        addSource(groups) {
            value = (value?.first ?: emptyList()) to it
        }
    }

    val treeNode2 = MediatorLiveData<List<TreeNode>>().apply {
        addSource(machines, {

        })
        addSource(groups, {

        })
    }


    val treeNodes3: LiveData<List<TreeNode>> = cacheDatabase.VBoxDao().getMachinesByGroup(group).switchMap {
        liveData {
            val ret = mutableListOf<TreeNode>()

            ret.addAll(it.map { MachineTreeNode(it) })

            ret.addAll(cacheDatabase.VBoxDao().getGroupsByGroup(group).)

            emit(ret.toList())
        }
    }

}