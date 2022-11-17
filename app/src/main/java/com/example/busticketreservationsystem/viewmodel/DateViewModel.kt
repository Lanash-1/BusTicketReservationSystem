package com.example.busticketreservationsystem.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DateViewModel: ViewModel() {

    var date = 0
    var month = 0
    var year = 0

    var edited = MutableLiveData<Boolean>()

    var travelEdited = MutableLiveData<Boolean>()

}