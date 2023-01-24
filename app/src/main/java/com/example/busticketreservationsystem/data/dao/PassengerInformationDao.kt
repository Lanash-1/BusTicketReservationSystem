package com.example.busticketreservationsystem.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.busticketreservationsystem.data.entity.PassengerInformation

@Dao
interface PassengerInformationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(passengerInformation: PassengerInformation)

    @Query("SELECT * FROM passenger_information_table WHERE bookingId LIKE :bookingId")
    fun getPassengerInfo(bookingId: Int): List<PassengerInformation>

    @Query("select * from passenger_information_table")
    fun getPassengersInformation(): List<PassengerInformation>

}