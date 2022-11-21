package com.example.busticketreservationsystem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.busticketreservationsystem.data.AppDatabase
import com.example.busticketreservationsystem.entity.Bookings
import com.example.busticketreservationsystem.entity.PassengerInformation
import com.example.busticketreservationsystem.entity.SeatInformation

class BookingDbViewModel(
    application: Application
): AndroidViewModel(application) {

    private val appDb = AppDatabase.getDatabase(application.applicationContext)


    fun insertBooking(booking: Bookings) {
        appDb.bookingsDao().insert(booking)
    }

    fun insertPassengerInfo(passengerInformation: PassengerInformation){
        appDb.passengerInformationDao().insert(passengerInformation)
    }

    fun insertSeatInformation(seatInformation: SeatInformation){
        appDb.seatInformationDao().insert(seatInformation)
    }

    fun getBookingId(userId: Int): Int{
        val list = appDb.bookingsDao().getBookingId(userId)
        return list[list.size-1]
    }

}
