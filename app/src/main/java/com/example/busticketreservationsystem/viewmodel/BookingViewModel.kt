package com.example.busticketreservationsystem.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.busticketreservationsystem.PassengerInfoModel
import com.example.busticketreservationsystem.entity.*

class BookingViewModel: ViewModel() {


    var totalTicketCost: Double = 0.0

    var selectedSeats: MutableList<String> = mutableListOf()

    var passengerInfo: MutableList<PassengerInfoModel> = mutableListOf()

    var bookingEmail: String? = null
    var bookingMobile: String? = null

    lateinit var selectedBus: Bus

    var boardingLocation: String = ""
    var droppingLocation: String = ""

    var date: String = ""

    var contactEmail: String = ""
    var contactNumber: String = ""


//    booking history

    var bookingHistory = listOf<Bookings>()
    var bookedBusesList = listOf<Bus>()
    var bookedPartnerList = listOf<String>()
    var bookedPassengerInfo = listOf<PassengerInformation>()
    var bookedPartnerDetail = listOf<Partners>()

    lateinit var filteredBookedPartnerDetailList: MutableList<Partners>

    var filteredBookingHistory = listOf<Bookings>()
    var filteredBookedBusesList = listOf<Bus>()
    var filteredBookedPartnerList = listOf<String>()

    var selectedTicket: Int = 0

    var tabPosition = MutableLiveData<Int>()


//    booked ticket details

    lateinit var bookedBus: Bus
    lateinit var bookedPartner: Partners
    var bookedPassengers = listOf<PassengerInformation>()




}