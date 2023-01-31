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

    fun isAdminEmail(inputEmail: String): Boolean{
        if(inputEmail == "admin@admin.com"){
            return true
        }
        return false
    }

    fun isValidMessage(message: String): Boolean {
        if(message.isNotEmpty()){
           return true
        }
        return false
    }

    fun getTimeStamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Calendar.getInstance().time)
    }


    fun getOnlyTime(timeStamp: String): String{
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(timeStamp)
        val cal = Calendar.getInstance()
        cal.time = date
        var hour = cal.get(Calendar.HOUR)
        val minute = cal.get(Calendar.MINUTE)
        var extension = ""
        when(cal.get(Calendar.AM_PM)){
            1 -> {
                extension = "pm"
            }
            0 -> {
                extension = "am"
            }
        }
        if(hour == 0){
            hour = 12
        }
        if(minute < 10){
            return "$hour:0$minute $extension"
        }
        return "$hour:$minute $extension"
    }

//    fun getDateExtended(timeStamp: String){
//
//    }


}