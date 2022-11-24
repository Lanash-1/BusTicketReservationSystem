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

    var filteredBusList = listOf<Bus>()


    lateinit var selectedBus: Bus

    var selectedSeats = mutableListOf<String>()


    val boardingPoints = listOf(
        "flower park",
        "central bus stand",
        "tech park",
        "location 1",
        "location 2",
        "location 3"
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


    var notAvailableSeats = listOf<String>()



    // ratings and reviews

    var ratingsList = listOf<Reviews>()

    var ratingCount: Int = 0

    var ratings = listOf<Int>()

    var averageRating: Double = 0.0

    lateinit var userReview: Reviews


}