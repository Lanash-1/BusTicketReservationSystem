package com.example.busticketreservationsystem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.busticketreservationsystem.data.AppDatabase
import com.example.busticketreservationsystem.entity.Bus
import com.example.busticketreservationsystem.entity.BusAmenities
import com.example.busticketreservationsystem.entity.Partners
import com.example.busticketreservationsystem.entity.Reviews

class BusDbViewModel(application: Application): AndroidViewModel(application) {

    private val appDb = AppDatabase.getDatabase(application.applicationContext)

    fun insertPartnerData(list: List<Partners>){
        appDb.partnersDao().insertPartnerData(list)
    }

    fun insertBusData(list: List<Bus>){
        appDb.busDao().insertBusData(list)
    }

    fun insertBusAmenitiesData(list: List<BusAmenities>){
        appDb.busAmenitiesDao().insert(list)
    }

    fun getBusData(): List<Bus>{
        return appDb.busDao().getBusData()
    }

    fun getPartnerData(): List<Partners>{
        return appDb.partnersDao().getPartnersData()
    }

    fun getReviewData(): List<Reviews>{
        return appDb.reviewsDao().getReviewsData()
    }



}