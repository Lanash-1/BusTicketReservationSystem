package com.example.busticketreservationsystem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.busticketreservationsystem.data.AppDatabase
import com.example.busticketreservationsystem.entity.*

class BusDbViewModel(application: Application): AndroidViewModel(application) {

    private val appDb = AppDatabase.getDatabase(application.applicationContext)

//    bus data

    fun insertPartnerData(list: List<Partners>){
        appDb.partnersDao().insertPartnerData(list)
    }

    fun insertBusData(list: List<Bus>){
        appDb.busDao().insertBusData(list)
    }

    fun getPartnerName(partnerId: Int): String{
        return appDb.partnersDao().getPartnerName(partnerId)
    }

    fun insertBusAmenitiesData(list: List<BusAmenities>){
        appDb.busAmenitiesDao().insert(list)
    }

    fun getBusAmenities(busId: Int): List<String> {
        return appDb.busAmenitiesDao().getBusAmenities(busId)
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


//    reviews data

    fun getReviewData(busId: Int): List<Reviews>{
        return appDb.reviewsDao().getReviewsData(busId)
    }

    fun updateBusRating(busId: Int, count: Int, average: Double){
        appDb.busDao().updateRating(busId, count, average)
    }

    fun getBusRatings(busId: Int): List<Int>{
        return appDb.reviewsDao().getRatingsOfABus(busId)
    }

    fun getReviewOfUser(userId: Int, busId: Int): List<Reviews>{
        return appDb.reviewsDao().getReviewByUser(busId, userId)
    }

    fun insertReview(reviews: Reviews){
        appDb.reviewsDao().insert(reviews)
    }

    fun updateReviewOfAUser(reviewId: Int, rating: Int, feedback: String){
        appDb.reviewsDao().updateReviews(reviewId, rating, feedback)
    }


//    seats data

    fun getBookedSeats(busId: Int, date: String): List<String>{
        return appDb.seatInformationDao().getBookedSeats(busId, date)
    }

    fun deleteSeatsOfBus(bookingId: Int){
        appDb.seatInformationDao().deleteSeatsOfBooking(bookingId)
    }

    fun getSeatsOfParticularBooking(bookingId: Int): List<String>{
        return appDb.seatInformationDao().getSeatsOfParticularBooking(bookingId)
    }


//    recently viewed data

    fun insertRecentlyViewed(recentlyViewed: RecentlyViewed){
        appDb.recentlyViewedDao().insert(recentlyViewed)
    }

    fun getRecentlyViewed(userId: Int): List<RecentlyViewed>{
        return appDb.recentlyViewedDao().getRecentlyViewed(userId)
    }

    fun isRecentlyViewedAvailable(userId: Int, busId: Int, date: String): Boolean{
        return appDb.recentlyViewedDao().isAvailable(userId, busId, date) != 0
    }

    fun removeRecentlyViewed(recentlyViewed: RecentlyViewed){
        appDb.recentlyViewedDao().deleteRecentlyViewed(recentlyViewed)
    }

    fun getPartnerDetails(partnerId: Int): Partners {
        return appDb.partnersDao().getPartnerDetails(partnerId)
    }


}