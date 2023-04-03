package com.example.busticketreservationsystem.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.busticketreservationsystem.enums.LoginStatus

class LoginStatusViewModel: ViewModel() {

    lateinit var status: LoginStatus

    var isUserEnteredPassword: Boolean = false
    var isUserReEnteredPassword: Boolean = false

    var bitmapValue: Bitmap? = null

}