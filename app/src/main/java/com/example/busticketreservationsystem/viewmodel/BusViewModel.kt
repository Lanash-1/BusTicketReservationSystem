package com.example.busticketreservationsystem.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.busticketreservationsystem.entity.Bus
import com.example.busticketreservationsystem.entity.Partners
import com.example.busticketreservationsystem.entity.RecentlyViewed
import com.example.busticketreservationsystem.entity.Reviews

class BusViewModel: ViewModel() {


    var busList = listOf<Bus>()

    var partnerList = listOf<Partners>()

    var reviewsList = listOf<Reviews>()

    var filteredBusList = listOf<Bus>()


    lateinit var selectedBus: Bus

    var selectedSeats = mutableListOf<String>()


    val boardingPoints = listOf(
        "location 1",
        "location 2",
        "location 3",
        "location 4",
        "this location",
        "that location",
        "other location",
        "another location"
    )

    val droppingPoints = listOf(
        "this location",
        "that location",
        "other location",
        "another location",
        "location 1",
        "location 2",
        "location 3",
        "location 4"
    )


    var boardingPoint = MutableLiveData("")

    var droppingPoint = MutableLiveData("")

    var recentlyViewedList = MutableLiveData<List<RecentlyViewed>>()
    var recentlyViewedBusList = listOf<Bus>()
    var recentlyViewedPartnerList = mutableListOf<String>()


}