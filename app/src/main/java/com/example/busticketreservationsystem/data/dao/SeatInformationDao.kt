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

//    @Query("select seatName from seat_information_table where busId like :busId and date=:date")
//    fun getBookedSeats(busId: Int, date: String): List<String>

    @Query("select distinct seatName from seat_information_table join passenger_information_table on seat_information_table.bookingId = passenger_information_table.bookingId where passenger_information_table.ticketStatus like :ticketStatus and seat_information_table.busId like :busId and seat_information_table.date=:date")
    fun getBookedSeats(busId: Int, date: String, ticketStatus: String): List<String>

    @Query("delete from seat_information_table where bookingId like :bookingId")
    fun deleteSeatsOfBooking(bookingId: Int)

    @Query("select seatName from seat_information_table where bookingId like :bookingId")
    fun getSeatsOfParticularBooking(bookingId: Int): List<String>

    @Query("delete from seat_information_table where bookingId like :bookingId and seatName like :seatCode")
    fun removeSeatFromBus(bookingId: Int, seatCode: String)


}