package com.example.busticketreservationsystem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.busticketreservationsystem.data.AppDatabase
import com.example.busticketreservationsystem.entity.*

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

    fun getBus(busId: Int): Bus{
        return appDb.busDao().getBus(busId)
    }

    fun getPartnerData(): List<Partners>{
        return appDb.partnersDao().getPartnersData()
    }

    fun getReviewData(): List<Reviews>{
        return appDb.reviewsDao().getReviewsData()
    }

    fun getRatingPeopleCount(busId: Int): Int{
        return appDb.reviewsDao().getReviewCount(busId)
    }

    fun getBusRatings(busId: Int): List<Int>{
        return appDb.reviewsDao().getRatingsOfABus(busId)
    }

    fun updateBusRating(peopleCount: Int, overallRating: Double, busId: Int){
        appDb.busDao().updateBusRating(peopleCount, overallRating, busId)
    }


    fun insertSeatInformation(seatInfo: SeatInformation){
        appDb.seatInformationDao().insert(seatInfo)
    }

    fun updateBusSeatAvailableCount(count: Int, busId: Int){
        appDb.busDao().updateAvailableSeats(count, busId)
    }

    fun insertRecentlyViewed(recentlyViewed: RecentlyViewed){
        appDb.recentlyViewedDao().insert(recentlyViewed)
    }

    fun getRecentlyViewed(userId: Int): List<RecentlyViewed>{
        return appDb.recentlyViewedDao().getRecentlyViewed(userId)
    }

    fun isRecentlyViewedAvailable(userId: Int, busId: Int, date: String): Boolean{
        return appDb.recentlyViewedDao().isAvailable(userId, busId, date) != 0
    }

    fun getPartnerName(partnerId: Int): String{
        return appDb.partnersDao().getPartnerName(partnerId)
    }

    fun removeRecentlyViewed(recentlyViewed: RecentlyViewed){
        appDb.recentlyViewedDao().deleteRecentlyViewed(recentlyViewed)
    }

    fun insertReview(reviews: Reviews){
        appDb.reviewsDao().insert(reviews)
    }

    fun getBookedSeats(busId: Int, date: String): List<String>{
        return appDb.seatInformationDao().getBookedSeats(busId, date)
    }

}