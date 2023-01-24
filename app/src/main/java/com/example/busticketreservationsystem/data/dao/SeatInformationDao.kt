package com.example.busticketreservationsystem.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.busticketreservationsystem.data.entity.SeatInformation

@Dao
interface SeatInformationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(seatInformation: SeatInformation)

    @Query("select seatName from seat_information_table where busId like :busId and date=:date")
    fun getBookedSeats(busId: Int, date: String): List<String>

    @Query("delete from seat_information_table where bookingId like :bookingId")
    fun deleteSeatsOfBooking(bookingId: Int)

    @Query("select seatName from seat_information_table where bookingId like :bookingId")
    fun getSeatsOfParticularBooking(bookingId: Int): List<String>


}