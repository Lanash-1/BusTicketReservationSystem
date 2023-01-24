package com.example.busticketreservationsystem.data.repository

import com.example.busticketreservationsystem.data.dao.BusAmenitiesDao
import com.example.busticketreservationsystem.data.entity.*
import com.example.busticketreservationsystem.enums.BusAmenities

interface AppRepository {

//    User

    fun insertUserData(user: User)

    fun updateUserData(user: User)

    fun getAccountCount(mobileNumber: String): Boolean

    fun isEmailExists(email: String): Boolean

    fun getUserAccount(mobileNumber: String): User

    fun getUserAccount(userId: Int): User

    fun isPasswordMatching(mobileNumber: String, password: String): Boolean

    fun updateUserPassword(password: String, mobileNumber: String)

    fun deleteUserAccount(user: User)

    fun getUserCount(): Int


//    Bus related Data

    fun insertPartnerData(partner: Partners)

    fun insertBusData(bus: Bus)

    fun getPartnerName(partnerId: Int): String

    fun insertBusAmenitiesData(amenity: com.example.busticketreservationsystem.data.entity.BusAmenities)

    fun getBusAmenities(busId: Int): List<String>

    fun getBusData(): List<Bus>

    fun getBus(busId: Int): Bus

    fun getPartnerData(): List<Partners>


//    Reviews related data

    fun getReviewData(busId: Int): List<Reviews>

    fun updateBusRating(busId: Int, count: Int, average: Double)

    fun getBusRatings(busId: Int): List<Int>

    fun getReviewOfUser(userId: Int, busId: Int): List<Reviews>

    fun insertReview(reviews: Reviews)

    fun updateReviewOfAUser(reviewId: Int, rating: Int, feedback: String)

//    Seats related data

    fun getBookedSeats(busId: Int, date: String): List<String>

    fun deleteSeatsOfBus(bookingId: Int)

    fun getSeatsOfParticularBooking(bookingId: Int): List<String>

//    Recently viewed data

    fun insertRecentlyViewed(recentlyViewed: RecentlyViewed)

    fun getRecentlyViewed(userId: Int): List<RecentlyViewed>

    fun isRecentlyViewedAvailable(userId: Int, busId: Int, date: String): Boolean

    fun removeRecentlyViewed(recentlyViewed: RecentlyViewed)

    fun getPartnerDetails(partnerId: Int): Partners

//    Booking related data

    fun getBookingCount(): Int

    fun insertBooking(booking: Bookings)

    fun insertPassengerInfo(passengerInformation: PassengerInformation)

    fun insertSeatInformation(seatInformation: SeatInformation)

    fun getBookingId(userId: Int): Int

    fun getUserBookings(userId: Int): List<Bookings>

    fun getUserBookings(userId: Int, ticketStatus: String): List<Bookings>

    fun updateTicketStatus(status: String, bookingId: Int)

    fun getPassengerInfo(): List<PassengerInformation>

    fun getParticularBooking(bookingId: Int): Bookings

    fun getBookedTicketPassengerInfo(bookingId: Int): List<PassengerInformation>

    fun getBusOfRoute(sourceLocation: String, destinationLocation: String): List<Bus>

    fun usersBusReview(userId: Int, busId: Int): Reviews

    fun isUserBooked(userId: Int, busId: Int): Boolean

}