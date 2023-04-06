package com.example.busticketreservationsystem.utils

import android.text.Editable
import android.util.Patterns
import com.example.busticketreservationsystem.enums.BusSeatType
import com.example.busticketreservationsystem.enums.BusTypes
import com.example.busticketreservationsystem.enums.Gender
import com.example.busticketreservationsystem.enums.SeatPosition
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

open class Helper {

    companion object {

        private val colors = listOf(
            "#1A1E38",   // Navy blue
            "#0B6623",   // Forest green
            "#7B1FA2",   // Rich purple
            "#FF5722",   // Burnt orange
            "#8B0000",   // Dark red
            "#43464B",   // Steel grey
            "#0A3D91",   // Royal blue
            "#006064",   // Deep teal
            "#FFC107",   // Golden yellow
            "#C62828"    // Brick red
        )

        fun getRandomColor(): String {
            return colors.random()
        }

    }

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

        val format = SimpleDateFormat("HH:mm")
        val date1: Date = format.parse(startTime)
        val date2: Date = format.parse(reachTime)

        val millis: Long = date2.time - date1.time
        val hours = (millis / (1000 * 60 * 60)).toInt()
        val mins = (millis / (1000 * 60) % 60).toInt()

        val diff = "${hours}.${getNumberFormat(mins)}"

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

    fun compareToCurrentDate(date: String): Boolean{
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val time = Calendar.getInstance().time
        val current = sdf.format(time)
        val currentDate = sdf.parse(current)
        val strDate: Date = sdf.parse(date)
        if(currentDate.compareTo(strDate) > 0){
            return true
        }
        return false
    }

    fun greaterThanCurrentDate(date: String): Boolean{
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val time = Calendar.getInstance().time
        val current = sdf.format(time)
        val currentDate = sdf.parse(current)
        val strDate: Date = sdf.parse(date)
        if(strDate > currentDate){
            return true
        }
        return false
    }

    fun validTiming(startTime: String, reachTime: String): Boolean {
        val format = SimpleDateFormat("HH:mm")
        val date1: Date = format.parse(startTime)
        val date2: Date = format.parse(reachTime)

        if(date1 >= date2){
            return false
        }
        return true
    }

    fun checkBusTimingIsGreater(time: String, date: String): Boolean{
        val current = Calendar.getInstance().time
        val timeFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formattedTime: String = timeFormat.format(current)
        val format = SimpleDateFormat("HH:mm")

        val busTime: Date = format.parse(time)
        val currentTime: Date = format.parse(formattedTime)

        return if(greaterThanCurrentDate(date)){
            true
        }else{
            busTime >= currentTime
        }
    }

    fun getCurrentDate(): Date? {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val time = Calendar.getInstance().time
        val current = sdf.format(time)

        return sdf.parse(current)
    }

    fun getCurrentDateString(): String{
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val time = Calendar.getInstance().time
        val current = sdf.format(time)
        return current
    }

    fun getDateExpandedFormat(inputDate: String): String{
        val normalFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val date = normalFormatter.parse(inputDate)
        val expandedFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
        val formattedDate = expandedFormatter.format(date!!)
        println("DaTE = $formattedDate")
        return formattedDate
    }

    fun getPreviousDate(inputDateStr: String): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val inputDate = dateFormat.parse(inputDateStr)

        val calendar = Calendar.getInstance()
        calendar.time = inputDate

        // Get the previous date
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val prevDate = dateFormat.format(calendar.time)
        println("Previous date: $prevDate")

        return prevDate
    }

    fun getNextDate(inputDateStr: String): String{
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val inputDate = dateFormat.parse(inputDateStr)

        val calendar = Calendar.getInstance()
        calendar.time = inputDate

        // Get the next date
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val nextDate = dateFormat.format(calendar.time)
        println("Next date: $nextDate")



        return nextDate
    }

//    fun getPreviousExpandedDate(inputDate: String): String{
//
//    }

    fun validPassword(passwordText: Editable?): Boolean {
        if(passwordText != null){
            if(passwordText.length < 8)
            {
                return false
            }
            if(!passwordText.matches(".*[A-Z].*".toRegex()))
            {
                return false
            }
            if(!passwordText.matches(".*[a-z].*".toRegex()))
            {
                return false
            }
            if(!passwordText.matches(".*[@#\$%^&+=].*".toRegex()))
            {
                return false
            }
        }else{
            return false
        }
        return true
    }


    fun getSeatNumber(seatCode: String): Int{
        return seatCode.substring(2).toInt()
    }

    fun getSeatPosition(seatCode: String): String{
        return seatCode.take(2)
    }

    fun getNumberFormat(number: Int): String {
        if(number < 10){
            return "0$number"
        }
        return number.toString()
    }

    fun getDeck(seat: String): String {
        return when(getSeatPosition(seat)){
            SeatPosition.LL.name,
            SeatPosition.LR.name -> {
                ", Lower deck"
            }
            SeatPosition.UL.name,
            SeatPosition.UR.name -> {
                ", Upper deck"
            }
            else -> {
                ""
            }
        }

    }

    fun getGenderText(gender: String): String {
        when(gender){
            Gender.FEMALE.name -> {
                return "Female"
            }
            Gender.MALE.name -> {
                return "Male"
            }
        }
        return ""
    }

    fun getBusTypeText(busType: String): String {
        return when(busType){
            BusTypes.AC_SEATER.name -> {
                "A/C Seater"
            }
            BusTypes.NON_AC_SEATER.name -> {
                "Non A/C Seater"
            }
            BusTypes.SLEEPER.name -> {
                "Sleeper"
            }
            BusTypes.SEATER_SLEEPER.name -> {
                "Seater/Sleeper"
            }
            else -> {
                ""
            }
        }
    }

    fun getBusType(busType: String): BusTypes {
        return when(busType){
            BusTypes.AC_SEATER.name -> {
                BusTypes.AC_SEATER
            }
            BusTypes.NON_AC_SEATER.name -> {
                BusTypes.NON_AC_SEATER
            }
            BusTypes.SLEEPER.name -> {
                BusTypes.SLEEPER
            }
            BusTypes.SEATER_SLEEPER.name -> {
                BusTypes.SEATER_SLEEPER
            }
            else -> {
                BusTypes.AC_SEATER
            }
        }
    }


}