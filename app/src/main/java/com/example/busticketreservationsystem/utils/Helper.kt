package com.example.busticketreservationsystem.utils

import android.util.Patterns

open class Helper {

    fun validEmail(email: String): String{
        return if(email.isNotEmpty()){
            if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                ""
            }else{
                "Invalid Email Address"
            }
        }else{
            "Email Should not be empty"
        }
    }


}