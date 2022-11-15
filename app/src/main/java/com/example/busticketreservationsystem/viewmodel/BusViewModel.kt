package com.example.busticketreservationsystem.viewmodel

import androidx.lifecycle.ViewModel
import com.example.busticketreservationsystem.entity.Bus
import com.example.busticketreservationsystem.entity.Partners
import com.example.busticketreservationsystem.entity.Reviews

class BusViewModel: ViewModel() {

    var busList = listOf<Bus>()

    var partnerList = listOf<Partners>()

    var reviewsList = listOf<Reviews>()

    var filteredBusList = listOf<Bus>()


    lateinit var selectedBus: Bus

    var selectedSeats = mutableListOf<String>()

}