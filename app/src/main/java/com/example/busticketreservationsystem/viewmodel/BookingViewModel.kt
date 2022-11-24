package com.example.busticketreservationsystem.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.busticketreservationsystem.PassengerInfoModel
import com.example.busticketreservationsystem.entity.Bookings
import com.example.busticketreservationsystem.entity.Bus
import com.example.busticketreservationsystem.entity.Partners
import com.example.busticketreservationsystem.entity.RecentlyViewed

class BookingViewModel: ViewModel() {

    var totalTicketCost: Double = 0.0

    var selectedSeats: List<String> = listOf()

    var passengerInfo: MutableList<PassengerInfoModel> = mutableListOf()

    var bookingEmail: String? = null
    var bookingMobile: String? = null

    lateinit var selectedBus: Bus

    var boardingLocation: String = ""
    var droppingLocation: String = ""

    var date: String = ""

    var contactEmail: String = ""
    var contactNumber: String = ""

//    var bookingHistory = MutableLiveData<List<Bookings>>()

    var bookingHistory = listOf<Bookings>()
    var bookedBusesList = listOf<Bus>()
    var bookedPartnerList = listOf<String>()

}