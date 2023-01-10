package com.example.busticketreservationsystem.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.busticketreservationsystem.model.entity.Bus
import com.example.busticketreservationsystem.model.entity.Partners
import com.example.busticketreservationsystem.model.entity.RecentlyViewed
import com.example.busticketreservationsystem.model.entity.Reviews

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
        "Maple Street Station",
        "Park Avenue Terminal",
        "Lakeside Loop",
        "Downtown Transfer Center",
        "Sunset Plaza",
        "Midtown Marketplace",
        "The Foothills Depot",
        "Mountain View Mall",
        "Pine Ridge Platform",
        "City Centre Station"
    )

    val droppingPoints = listOf(
        "Riverfront Road",
        "The Woodlands Park",
        "Rockville Corner",
        "Glenbrook Plaza",
        "Brookdale Junction",
        "Meadowlands Avenue",
        "Hampton Hills",
        "The Pines Terminal",
        "Tanglewood Mall",
        "Maplewood Meadows"
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