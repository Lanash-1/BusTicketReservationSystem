package com.example.busticketreservationsystem.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DateViewModel: ViewModel() {

    var date = 0
    var month = 0
    var year = 0

    var edited = MutableLiveData<Boolean>()


//    Date of birth related


    var birthDate = 0
    var birthMonth = 0
    var birthYear = 0

    var birthDateEdited = MutableLiveData<Boolean>()



//    travel Date related


    var travelDate = 0
    var travelMonth = 0
    var travelYear = 0

    var travelDateEdited = MutableLiveData<Boolean>()


//    edit profile date related

    var editedDate = 0
    var editedMonth = 0
    var editedYear = 0

    var dobEdited = MutableLiveData<Boolean>()


}