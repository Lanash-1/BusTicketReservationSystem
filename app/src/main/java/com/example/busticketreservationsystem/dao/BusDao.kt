package com.example.busticketreservationsystem.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.busticketreservationsystem.entity.Bus
import com.example.busticketreservationsystem.entity.User

@Dao
interface BusDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBusData(list: List<Bus>)

    @Query("SELECT * FROM bus_table")
    fun getBusData(): List<Bus>

}