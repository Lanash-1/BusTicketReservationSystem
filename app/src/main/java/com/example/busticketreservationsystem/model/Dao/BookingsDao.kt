package com.example.busticketreservationsystem.model.Dao

import androidx.room.*
import com.example.busticketreservationsystem.enums.BookedTicketStatus
import com.example.busticketreservationsystem.model.entity.Bookings

@Dao
interface BookingsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(bookings: Bookings)

    @Query("SELECT bookingId FROM bookings_table WHERE userId LIKE :userId")
    fun  getBookingId(userId: Int): List<Int>

    @Query("SELECT * from bookings_table where userId like :userId")
    fun getUserBookings(userId: Int): List<Bookings>

    @Query("select * from bookings_table where userId like :userId and bookedTicketStatus like :ticketStatus")
    fun getUserBookings(userId: Int, ticketStatus: String): List<Bookings>

    @Query("select * from bookings_table where bookingId like :bookingId")
    fun getSingleBooking(bookingId: Int): Bookings

    @Query("update bookings_table set bookedTicketStatus=:status where bookingId like :bookingId")
    fun updateTicketStatus(status: String, bookingId: Int)

    @Delete
    fun deleteBooking(bookings: Bookings)

}