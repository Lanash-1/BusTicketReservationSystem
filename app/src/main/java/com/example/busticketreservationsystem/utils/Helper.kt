package com.example.busticketreservationsystem.utils

import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.*

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

    fun getDuration(startTime: String, reachTime: String): String {

        val format = SimpleDateFormat("hh:mm")
        val date1: Date = format.parse(startTime)
        val date2: Date = format.parse(reachTime)

        val millis: Long = date2.time - date1.time
        val hours = (millis / (1000 * 60 * 60)).toInt()
        val mins = (millis / (1000 * 60) % 60).toInt()

        val diff = "$hours.$mins"

        println("Duration : $diff")

        return diff
    }


}