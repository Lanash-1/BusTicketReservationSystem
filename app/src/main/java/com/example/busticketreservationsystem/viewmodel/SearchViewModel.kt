package com.example.busticketreservationsystem.viewmodel

import androidx.lifecycle.ViewModel

class SearchViewModel: ViewModel() {

    var sourceLocation = ""
    var destinationLocation = ""

    var currentSearch = ""

    val location = listOf(
        "Chennai",
        "Bangalore",
        "Madurai",
        "Tenkasi",
        "Kerala",
        "Tiruppur",
        "Hyderabad",
        "Trichy",
        "Sivagangai",
        "Kanchipuram",
        "Pune",
        "Mumbai",
        "Delhi",
        "Coimbatore"
    )

    val popularCities = listOf(
        "Pune",
        "Mumbai",
        "Delhi",
        "Coimbatore",
        "Chennai",
        "Bangalore",
        "Hyderabad"
    )

    var date = 0
    var month = 0
    var year = 0

}