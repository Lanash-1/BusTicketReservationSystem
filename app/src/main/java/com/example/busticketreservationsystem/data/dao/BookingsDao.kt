package com.example.busticketreservationsystem.data.dao

import androidx.room.*
import com.example.busticketreservationsystem.data.entity.Bookings

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

    @Query("select count(*) from bookings_table where busId like :busId")
    fun getParticularBusBooking(busId: Int): Int

    @Query("select count(*) from bookings_table")
    fun getBookingCount(): Int

    @Query("select count(*) from bookings_table where busId like :busId and userId like :userId")
    fun getUserBooking(userId: Int, busId: Int): Int

}