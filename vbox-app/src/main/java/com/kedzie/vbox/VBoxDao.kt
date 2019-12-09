package com.kedzie.vbox

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.kedzie.vbox.api.IMachineEntity

@Dao
@com.kedzie.vbox.soap.Dao
interface VBoxDao {

    @Query("select * from IMachine m left join IMachine_groups g on m.idRef = g.idRef where g.value = :group")
    fun getMachinesByGroup(group: String): LiveData<List<IMachineEntity>>
}