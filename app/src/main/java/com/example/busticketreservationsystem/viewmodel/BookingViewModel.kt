package com.example.busticketreservationsystem.viewmodel

import androidx.lifecycle.ViewModel
import com.example.busticketreservationsystem.PassengerInfoModel
import com.example.busticketreservationsystem.entity.Bus

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

}