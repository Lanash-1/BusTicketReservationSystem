package com.example.busticketreservationsystem.viewmodel

import androidx.lifecycle.ViewModel

class SearchViewModel: ViewModel() {

    var sourceLocation = "Chennai"
    var destinationLocation = "Bangalore"

    var dateOfTravel = "Nov 12"

    var currentSearch = ""
}