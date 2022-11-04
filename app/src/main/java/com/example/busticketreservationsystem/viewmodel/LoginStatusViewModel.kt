package com.example.busticketreservationsystem.viewmodel

import androidx.lifecycle.ViewModel
import com.example.busticketreservationsystem.enums.LoginStatus

class LoginStatusViewModel: ViewModel() {

    var status: LoginStatus = LoginStatus.NEW

}