package com.example.busticketreservationsystem.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.busticketreservationsystem.entity.*

class BusViewModel: ViewModel() {


//    bus data
    var busList = listOf<Bus>()

    var busAmenities = listOf<String>()

    var partnerList = listOf<Partners>()





//    bus results data's

    var filteredBusList = listOf<Bus>()

    var filteredBusAmenities = listOf<List<String>>()

    lateinit var selectedBus: Bus

    var selectedBusPosition: Int = 0

    lateinit var selectedPartner: Partners

    var selectedSeats = mutableListOf<String>()

    var notAvailableSeats = listOf<String>()





//    boarding and dropping

    val boardingPoints = listOf(
        "flower park",
        "central bus stand",
        "tech park",
    )

    val droppingPoints = listOf(
        "location 1",
        "location 2",
        "location 3",
        "location 4"
    )


    var boardingPoint = MutableLiveData("")

    var droppingPoint = MutableLiveData("")





//    recently viewed

    var recentlyViewedList = MutableLiveData<List<RecentlyViewed>>()
    var recentlyViewedBusList = listOf<Bus>()
    var recentlyViewedPartnerList = listOf<String>()





    // ratings and reviews

    var ratingsList = listOf<Reviews>()

    var ratingCount: Int = 0

    var ratings = listOf<Int>()

    var averageRating: Double = 0.0

    var userReview = listOf<Reviews>()



}