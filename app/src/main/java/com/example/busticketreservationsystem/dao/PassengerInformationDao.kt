package com.example.busticketreservationsystem.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.busticketreservationsystem.entity.PassengerInformation

@Dao
interface PassengerInformationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(passengerInformation: PassengerInformation)
}