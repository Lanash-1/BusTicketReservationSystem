package com.example.busticketreservationsystem.listeners

import com.example.busticketreservationsystem.enums.Gender

interface PassengerInfoChangeListener {

    fun onPassengerNameChanged(position: Int, name: String)
    fun onPassengerAgeChanged(position: Int, age: Int?)
    fun onPassengerGenderSelected(position: Int, gender: Gender)

}