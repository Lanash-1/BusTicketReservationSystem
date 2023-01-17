package com.example.busticketreservationsystem.data.entity

data class LocationModel(
    var state: String,
    var cities: List<String>,
    var areas: List<List<String>>
)