package com.example.busticketreservationsystem.viewmodel

import androidx.lifecycle.ViewModel

class SearchViewModel: ViewModel() {

    var sourceLocation = "Chennai"
    var destinationLocation = "Bangalore"

    var currentSearch = ""

    val location = listOf(
        "Chennai",
        "Bangalore",
        "Madurai",
        "Tenkasi",
        "Kerala",
        "Tiruppur",
        "Hyderabad",
        "Trichy"
    )

}