package com.example.busticketreservationsystem.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.busticketreservationsystem.entity.PassengerInformation

@Dao
interface PassengerInformationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(passengerInformation: PassengerInformation)

    @Query("SELECT * FROM passenger_information_table WHERE bookingId LIKE :bookingId")
    fun getPassengerInfo(bookingId: Int): List<PassengerInformation>

}