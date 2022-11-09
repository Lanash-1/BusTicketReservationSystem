package com.example.busticketreservationsystem.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.busticketreservationsystem.entity.Bus
import com.example.busticketreservationsystem.entity.User

@Dao
interface BusDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBusData(list: List<Bus>)

}