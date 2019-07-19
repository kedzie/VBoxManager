package com.kedzie.vbox.server

import androidx.lifecycle.LiveData
import androidx.room.*

@Database(version = 1,
        entities = [Server::class
        ])
abstract class AppDatabase : RoomDatabase() {
    abstract fun serverDao(): ServerDao
}

@Dao
interface ServerDao {

    @Query("SELECT * FROM servers")
    fun getServers(): LiveData<List<Server>>

    @Delete
    fun delete(server: Server) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(server: Server)
}