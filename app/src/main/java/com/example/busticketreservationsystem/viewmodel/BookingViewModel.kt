package com.example.busticketreservationsystem.viewmodel

import androidx.lifecycle.ViewModel

class BookingViewModel: ViewModel() {

    var totalTicketCost: Double = 0.0

    var selectedSeats: List<String> = listOf()

}