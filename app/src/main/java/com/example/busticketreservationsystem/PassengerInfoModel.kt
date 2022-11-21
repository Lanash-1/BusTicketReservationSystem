package com.example.busticketreservationsystem

import com.example.busticketreservationsystem.enums.Gender

data class PassengerInfoModel(
    var name: String? = null,
    var age: Int? = null,
    var gender: Gender? = null
)
