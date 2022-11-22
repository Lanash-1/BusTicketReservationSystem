package com.example.busticketreservationsystem.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.busticketreservationsystem.entity.SeatInformation

@Dao
interface SeatInformationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(seatInformation: SeatInformation)


}