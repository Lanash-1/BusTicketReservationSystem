package com.example.busticketreservationsystem.data.repository

import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.entity.*

class AppRepositoryImpl(
    private val appDb: AppDatabase
): AppRepository {

    //    User

    override fun insertUserData(user: User){
        appDb.userDao().insert(user)
    }

    override fun updateUserData(user: User){
        appDb.userDao().updateUserData(user.userId, user.username, user.emailId, user.mobileNumber, user.password, user.dob, user.gender)
    }

    override fun getAccountCount(mobileNumber: String): Boolean{
        return appDb.userDao().getAccountCount(mobileNumber) != 0
    }

    override fun isEmailExists(email: String): Boolean{
        return appDb.userDao().getEmailCount(email) == 0
    }

    override fun getUserAccount(mobileNumber: String): User {
        return appDb.userDao().getUserAccount(mobileNumber)
    }

    override fun getUserAccount(userId: Int): User {
        return appDb.userDao().getUserAccountByUserID(userId)
    }

    override fun isPasswordMatching(mobileNumber: String, password: String): Boolean {
        return appDb.userDao().getPassword(mobileNumber) == password
    }

    override fun updateUserPassword(password: String, mobileNumber: String) {
        appDb.userDao().updateUserPassword(password, mobileNumber)
    }

    override fun deleteUserAccount(user: User){
        appDb.userDao().deleteUserAccount(user)
    }


//    Bus related Data


    override fun insertPartnerData(list: List<Partners>){
        appDb.partnersDao().insertPartnerData(list)
    }

    override fun insertBusData(list: List<Bus>){
        appDb.busDao().insertBusData(list)
    }

    override fun getPartnerName(partnerId: Int): String{
        return appDb.partnersDao().getPartnerName(partnerId)
    }

    override fun insertBusAmenitiesData(list: List<BusAmenities>){
        appDb.busAmenitiesDao().insert(list)
    }

    override fun getBusAmenities(busId: Int): List<String> {
        return appDb.busAmenitiesDao().getBusAmenities(busId)
    }

    override fun getBusData(): List<Bus>{
        return appDb.busDao().getBusData()
    }

    override fun getBus(busId: Int): Bus {
        return appDb.busDao().getBus(busId)
    }

    override fun getPartnerData(): List<Partners>{
        return appDb.partnersDao().getPartnersData()
    }

    override fun getBusOfRoute(sourceLocation: String, destinationLocation: String): List<Bus> {
        return appDb.busDao().getBusOfRoute(sourceLocation, destinationLocation)
    }


//    Reviews related data

    override fun getReviewData(busId: Int): List<Reviews>{
        return appDb.reviewsDao().getReviewsData(busId)
    }

    override fun updateBusRating(busId: Int, count: Int, average: Double){
        appDb.busDao().updateRating(busId, count, average)
    }

    override fun getBusRatings(busId: Int): List<Int>{
        return appDb.reviewsDao().getRatingsOfABus(busId)
    }

    override fun getReviewOfUser(userId: Int, busId: Int): List<Reviews>{
        return appDb.reviewsDao().getReviewByUser(busId, userId)
    }

    override fun insertReview(reviews: Reviews){
        appDb.reviewsDao().insert(reviews)
    }

    override fun updateReviewOfAUser(reviewId: Int, rating: Int, feedback: String){
        appDb.reviewsDao().updateReviews(reviewId, rating, feedback)
    }

    override fun usersBusReview(userId: Int, busId: Int): Reviews {
        return appDb.reviewsDao().getUsersBusReview(userId, busId)
    }

    override fun isUserBooked(busId: Int): Boolean {
        return appDb.bookingsDao().getParticularBusBooking(busId) > 0
    }


//    Seats related data

    override fun getBookedSeats(busId: Int, date: String): List<String>{
        return appDb.seatInformationDao().getBookedSeats(busId, date)
    }

    override fun deleteSeatsOfBus(bookingId: Int){
        appDb.seatInformationDao().deleteSeatsOfBooking(bookingId)
    }

    override fun getSeatsOfParticularBooking(bookingId: Int): List<String>{
        return appDb.seatInformationDao().getSeatsOfParticularBooking(bookingId)
    }

//    Recently viewed data

    override fun insertRecentlyViewed(recentlyViewed: RecentlyViewed){
        appDb.recentlyViewedDao().insert(recentlyViewed)
    }

    override fun getRecentlyViewed(userId: Int): List<RecentlyViewed>{
        return appDb.recentlyViewedDao().getRecentlyViewed(userId)
    }

    override fun isRecentlyViewedAvailable(userId: Int, busId: Int, date: String): Boolean{
        return appDb.recentlyViewedDao().isAvailable(userId, busId, date) != 0
    }

    override fun removeRecentlyViewed(recentlyViewed: RecentlyViewed){
        appDb.recentlyViewedDao().deleteRecentlyViewed(recentlyViewed)
    }

    override fun getPartnerDetails(partnerId: Int): Partners {
        return appDb.partnersDao().getPartnerDetails(partnerId)
    }

//    Booking related data

    override fun insertBooking(booking: Bookings) {
        appDb.bookingsDao().insert(booking)
    }

    override fun insertPassengerInfo(passengerInformation: PassengerInformation){
        appDb.passengerInformationDao().insert(passengerInformation)
    }

    override fun insertSeatInformation(seatInformation: SeatInformation){
        appDb.seatInformationDao().insert(seatInformation)
    }

    override fun getBookingId(userId: Int): Int{
        val list = appDb.bookingsDao().getBookingId(userId)
        return list[list.size-1]
    }


    override fun getUserBookings(userId: Int): List<Bookings>{
        return appDb.bookingsDao().getUserBookings(userId)
    }

    override fun getUserBookings(userId: Int, ticketStatus: String): List<Bookings> {
        return appDb.bookingsDao().getUserBookings(userId, ticketStatus)
    }

    override fun updateTicketStatus(status: String, bookingId: Int){
        appDb.bookingsDao().updateTicketStatus(status, bookingId)
    }

    override fun getPassengerInfo(): List<PassengerInformation> {
        return appDb.passengerInformationDao().getPassengersInformation()
    }

    override fun getParticularBooking(bookingId: Int): Bookings {
        return appDb.bookingsDao().getSingleBooking(bookingId)
    }

    override fun getBookedTicketPassengerInfo(bookingId: Int): List<PassengerInformation> {
        return appDb.passengerInformationDao().getPassengerInfo(bookingId)
    }




}