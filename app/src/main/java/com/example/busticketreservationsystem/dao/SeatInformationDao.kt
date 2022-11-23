package com.example.busticketreservationsystem.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.busticketreservationsystem.entity.SeatInformation

@Dao
interface SeatInformationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(seatInformation: SeatInformation)


    @Query("select seatName from seat_information_table where busId like :busId and date=:date")
    fun getBookedSeats(busId: Int, date: String): List<String>


}