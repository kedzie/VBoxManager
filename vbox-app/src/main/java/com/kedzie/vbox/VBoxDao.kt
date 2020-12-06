package com.kedzie.vbox

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.kedzie.vbox.api.IMachineEntity

@Dao
@com.kedzie.vbox.soap.Dao
abstract class VBoxDao {

    @Query("select * from IMachine m left join IMachine_groups g on m.idRef = g.idRef where g.value = :group")
    abstract fun getMachinesByGroup(group: String): LiveData<List<IMachineEntity>>

    @Query("select count(*) from IMachine_groups where value = :group")
    abstract suspend fun getMachineCountByGroup(group: String): Int

    @Query("select value from IMachine_groups where value LIKE :like AND value NOT LIKE :notlike")
    abstract fun getGroupsByGroupInternal(like: String, notlike: String): LiveData<List<String>>

    @Query("select count(*) from IMachine_groups where value LIKE :like AND value NOT LIKE :notlike")
    abstract fun getGroupsByGroupCountInternal(like: String, notlike: String): LiveData<List<String>>

    fun getGroupsByGroup(group: String) = getGroupsByGroupInternal("$group/%", "$group/%/")

    fun getGroupCountByGroup(group: String) = getGroupsByGroupCountInternal("$group/%", "$group/%/")

}