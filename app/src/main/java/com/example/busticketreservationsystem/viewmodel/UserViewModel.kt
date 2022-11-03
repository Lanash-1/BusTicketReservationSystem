package com.example.busticketreservationsystem.viewmodel

import androidx.lifecycle.ViewModel
import com.example.busticketreservationsystem.entity.User

class UserViewModel: ViewModel() {

    var user = User(
        0,
        "",
        "",
        "",
        "",
        0,
        ""
    )

    var userId = 0
    var userName = ""
    var emailId = ""
    var mobileNumber = ""
    var password = ""
    var age = 0
    var gender = ""


    var validEmailMessage: String? = ""

}