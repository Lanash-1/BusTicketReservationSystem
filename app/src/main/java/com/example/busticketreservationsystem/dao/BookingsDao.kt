package com.example.busticketreservationsystem.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.busticketreservationsystem.entity.Bookings

@Dao
interface BookingsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(bookings: Bookings)

    @Query("SELECT bookingId FROM bookings_table WHERE userId LIKE :userId")
    fun  getBookingId(userId: Int): List<Int>


    @Query("SELECT * from bookings_table where userId like :userId")
    fun getUserBookings(userId: Int): List<Bookings>

    @Query("select * from bookings_table where bookingId like :bookingId")
    fun getSingleBooking(bookingId: Int): Bookings

}