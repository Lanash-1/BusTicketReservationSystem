package com.example.busticketreservationsystem.viewmodel.livedata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busticketreservationsystem.enums.BookedTicketStatus
import com.example.busticketreservationsystem.data.entity.*
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.bookingdetails.PassengerInfoModel
import com.example.busticketreservationsystem.utils.Helper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class BookingViewModel(
    private val repository: AppRepositoryImpl
): ViewModel() {

    private val helper = Helper()

//    Booking History list related data

    var bookingHistoryBusList = listOf<Bus>()
    var bookingHistoryBookingList = listOf<Bookings>()
    var bookingHistoryPartnerList = listOf<String>()


    var filteredBookingList = listOf<Bookings>()
    var filteredBusList = listOf<Bus>()
    var filteredPartnerList = listOf<String>()
    lateinit var filteredTicketStatus: BookedTicketStatus

    var bookingDataFetched = MutableLiveData<Boolean>()


    fun fetchBookingHistory(userId: Int, ticketStatus: String){
        viewModelScope.launch(Dispatchers.IO) {
            val busList = mutableListOf<Bus>()
            var bookings = listOf<Bookings>()
            val partnerName = mutableListOf<String>()
            val fetchJob = launch {
                bookings = repository.getUserBookings(userId, ticketStatus)
                for(i in bookings){
                    busList.add(repository.getBus(i.busId))
                }
                for(i in busList){
                    partnerName.add(repository.getPartnerName(i.partnerId))
                }
            }
            fetchJob.join()
            withContext(Dispatchers.Main){
                bookingHistoryBookingList = bookings
                bookingHistoryBusList = busList
                bookingHistoryPartnerList = partnerName

                bookingDataFetched.value = true
            }
        }
    }


    var upcomingBooking = mutableListOf<Bookings>()
    var completedBooking = mutableListOf<Bookings>()
    var cancelledBooking = mutableListOf<Bookings>()

    var upcomingPartner = mutableListOf<String>()
    var completedPartner = mutableListOf<String>()
    var cancelledPartner = mutableListOf<String>()

    var upcomingBus = mutableListOf<Bus>()
    var completedBus = mutableListOf<Bus>()
    var cancelledBus = mutableListOf<Bus>()


    val isUserBookingFetched = MutableLiveData<Boolean>(null)

    fun fetchBookingOfUser1(userId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val upcomingBookings = mutableListOf<Bookings>()
            val completedBookings = mutableListOf<Bookings>()
            val cancelledBookings = mutableListOf<Bookings>()

            val upcomingPartners = mutableListOf<String>()
            val completedPartners = mutableListOf<String>()
            val cancelledPartners = mutableListOf<String>()

            val upcomingBuses = mutableListOf<Bus>()
            val completedBuses = mutableListOf<Bus>()
            val cancelledBuses = mutableListOf<Bus>()

            var userBookingsUpcoming: List<Bookings>
            var userBookingsCompleted: List<Bookings>
            var userBookingsCancelled: List<Bookings>

            val job = launch {
                userBookingsUpcoming = repository.getUserBookings(userId)
                userBookingsCompleted = repository.getUserBookings(userId)
                userBookingsCancelled = repository.getUserBookings(userId)


                for(booking in userBookingsUpcoming.indices){
                    val upcomingCount = repository.checkPassengerTicketStatus(BookedTicketStatus.UPCOMING.name, userBookingsUpcoming[booking].bookingId)
                    val completedCount = repository.checkPassengerTicketStatus(BookedTicketStatus.COMPLETED.name, userBookingsCompleted[booking].bookingId)
                    val cancelledCount = repository.checkPassengerTicketStatus(BookedTicketStatus.CANCELLED.name, userBookingsCancelled[booking].bookingId)
                    if(upcomingCount > 0){
//                        booking.noOfTicketsBooked = upcomingCount
                        val upcomingNewBooking = userBookingsUpcoming[booking]
                        upcomingNewBooking.noOfTicketsBooked = upcomingCount
                        upcomingBookings.add(upcomingNewBooking)
//                        upcomingBookings[booking].noOfTicketsBooked = upcomingCount
                    }
                    if(completedCount > 0){
//                        booking.noOfTicketsBooked = completedCount
                        val completedNewBooking = userBookingsCompleted[booking]
                        completedNewBooking.noOfTicketsBooked = completedCount
                        completedBookings.add(completedNewBooking)
//                        completedBookings[booking].noOfTicketsBooked = completedCount

                    }
                    if(cancelledCount > 0){
//                        booking.noOfTicketsBooked = cancelledCount
                        val cancelledNewBooking = userBookingsCancelled[booking]
                        cancelledNewBooking.noOfTicketsBooked = cancelledCount
                        cancelledBookings.add(cancelledNewBooking)

                    }
                }

                for(booking in upcomingBookings){
                    upcomingBuses.add(repository.getBus(booking.busId))
                }
                for(booking in completedBookings){
                    completedBuses.add(repository.getBus(booking.busId))
                }
                for(booking in cancelledBookings){
                    cancelledBuses.add(repository.getBus(booking.busId))
                }

                for(bus in upcomingBuses.indices){
                    upcomingBookings[bus].totalCost = (upcomingBookings[bus].noOfTicketsBooked * upcomingBuses[bus].perTicketCost)
                    upcomingPartners.add(repository.getPartnerName(upcomingBuses[bus].partnerId))
                }
                for(bus in completedBuses.indices){
                    completedBookings[bus].totalCost = (completedBookings[bus].noOfTicketsBooked * completedBuses[bus].perTicketCost)
                    completedPartners.add(repository.getPartnerName(completedBuses[bus].partnerId))
                }
                for(bus in cancelledBuses.indices){
                    cancelledBookings[bus].totalCost = (cancelledBookings[bus].noOfTicketsBooked * cancelledBuses[bus].perTicketCost)
                    cancelledPartners.add(repository.getPartnerName(cancelledBuses[bus].partnerId))
                }
            }
            job.join()
            withContext(Dispatchers.Main){
                upcomingBooking = upcomingBookings
                upcomingBus = upcomingBuses
                upcomingPartner = upcomingPartners

                completedBooking = completedBookings
                completedBus = completedBuses
                completedPartner = completedPartners

                cancelledBooking = cancelledBookings
                cancelledBus = cancelledBuses
                cancelledPartner = cancelledPartners

                isUserBookingFetched.value = true
            }
        }
    }

    fun fetchBookingOfUser(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val busList = mutableListOf<Bus>()
            var bookings = listOf<Bookings>()
            val partnerName = mutableListOf<String>()
            val fetchJob = launch {
                bookings = repository.getUserBookings(userId)
                for(i in bookings){
                    busList.add(repository.getBus(i.busId))
                }
                for(i in busList){
                    partnerName.add(repository.getPartnerName(i.partnerId))
                }
            }
            fetchJob.join()
            withContext(Dispatchers.Main){
                bookingHistoryBookingList = bookings
                bookingHistoryBusList = busList
                bookingHistoryPartnerList = partnerName

                bookingDataFetched.value = true
            }
        }
    }

    fun updateBookingHistoryList(userId: Int, currentDate: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            var bookingsList = listOf<Bookings>()
//            val job = launch {
                val sdf = SimpleDateFormat("dd/MM/yyyy")
                bookingsList = repository.getUserBookings(userId, BookedTicketStatus.UPCOMING.name)
                for(booking in bookingsList){
                    val strDate: Date = sdf.parse(booking.date)
                    if(currentDate.compareTo(strDate) > 0){
                        repository.updateTicketStatus(BookedTicketStatus.COMPLETED.name, booking.bookingId)
                    }
                }
//            }
        }
    }


//    Booked ticket details related

    var selectedTicket = 0

    lateinit var bookedBus: Bus
    lateinit var booking: Bookings
    lateinit var bookedPartner: Partners
    lateinit var bookedSeatInformation: List<String>
    lateinit var bookedPassengerInformation: List<PassengerInformation>

    var bookedTicketDataFetched = MutableLiveData<Boolean>(null)
    var isTicketCancelled = MutableLiveData<Boolean>()

    fun cancelBookedTicket(bookingId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val job = launch {
                repository.updateTicketStatus(BookedTicketStatus.CANCELLED.name, bookingId)
                repository.deleteSeatsOfBus(bookingId)

            }
            job.join()
            withContext(Dispatchers.Main){
                isTicketCancelled.value = true
            }
        }
    }

//    lateinit var bookedBus: Bus
//    lateinit var booking: Bookings
//    lateinit var bookedPartner: Partners
//    lateinit var bookedSeatInformation: List<String>
//    lateinit var bookedPassengerInformation: List<PassengerInformation>

    fun fetchBookedTicketDetails(bookingId: Int, ticketStatus: String){
        viewModelScope.launch(Dispatchers.IO) {
            lateinit var partner: Partners
            lateinit var bus: Bus
            lateinit var bookings: Bookings
            var passengerInformation = listOf<PassengerInformation>()
            val job = launch {
                bookings = repository.getParticularBooking(bookingId)
                bus = repository.getBus(bookings.busId)
                partner = repository.getPartnerDetails(bus.partnerId)
                passengerInformation = repository.getBookedTicketPassengerInformation(bookingId, ticketStatus)
                bookings.noOfTicketsBooked = passengerInformation.size
                bookings.totalCost = passengerInformation.size * bus.perTicketCost
            }
            job.join()
            withContext(Dispatchers.Main){
                bookedPartner = partner
                bookedBus = bus
                booking = bookings
                bookedPassengerInformation = passengerInformation

                bookedTicketDataFetched.value = true
            }
        }
    }

//    fun fetchBookedTicketDetails(bookingId: Int) {
//        viewModelScope.launch(Dispatchers.IO) {
//            lateinit var bus: Bus
//            lateinit var bookingDetails: Bookings
//            lateinit var partner: Partners
//            lateinit var seats: List<String>
//            lateinit var passengerInfo: List<PassengerInformation>
//            val fetchJob = launch {
//                bookingDetails = repository.getParticularBooking(bookingId)
//                bus = repository.getBus(bookingDetails.busId)
//                partner = repository.getPartnerDetails(bus.partnerId)
//                seats = repository.getSeatsOfParticularBooking(bookingDetails.bookingId)
//                passengerInfo = repository.getBookedTicketPassengerInfo(bookingDetails.bookingId)
//            }
//            fetchJob.join()x
//            withContext(Dispatchers.Main) {
//                booking = bookingDetails
//                bookedBus = bus
//                bookedPartner = partner
//                bookedSeatInformation = seats
//                bookedPassengerInformation = passengerInfo
//
//                bookedTicketDataFetched.value = true
//
//            }
//        }
//    }


//    Ticket booking process related information and operations

        var totalTicketCost = 0.0
        var selectedSeats = mutableListOf<String>()

        lateinit var selectedBus: Bus


        var contactMobileNumber: String? = null
        var contactEmailId: String? = null

        var boardingLocation = ""
        var droppingLocation = ""

        var seatInformationList = mutableListOf<SeatInformation>()
        var passengerInformationList = mutableListOf<PassengerInformation>()

        var passengerInfo: MutableList<PassengerInfoModel> = mutableListOf()

        var isTicketBooked = MutableLiveData<Boolean>(null)

        var insertedBookingId = 0

        fun insertBookingOperation(booking: Bookings, selectedDate: String, userId: Int) {
//        insert booking and insert seat information and insert passenger information

            viewModelScope.launch(Dispatchers.IO) {
                var bookingId: Int = 0
                val job = launch {
                    repository.insertBooking(booking)
                    bookingId = repository.getBookingId(userId)
                    for (i in selectedSeats.indices) {
                        repository.insertSeatInformation(
                            SeatInformation(
                                0,
                                selectedBus.busId,
                                bookingId,
                                selectedSeats[i],
                                selectedDate
                            )
                        )
                    }
                    for (i in 0 until passengerInfo.size) {
                        repository.insertPassengerInfo(
                            PassengerInformation(
                                0,
                                bookingId,
                                passengerInfo[i].name!!,
                                passengerInfo[i].age!!,
                                passengerInfo[i].gender!!.name,
                                selectedSeats[i],
                                BookedTicketStatus.UPCOMING.name
                            )
                        )
                    }
                    fetchBookedTicketDetails(bookingId, BookedTicketStatus.UPCOMING.name)
                }
                job.join()
                withContext(Dispatchers.Main) {
                    insertedBookingId = bookingId
                    isTicketBooked.value = true
                }
            }
        }

        fun updateTicketStatus() {
            viewModelScope.launch(Dispatchers.IO) {
                val sdf = SimpleDateFormat("dd/MM/yyyy")
//            val bookingsList = repository.getAllUpcomingBookings(BookedTicketStatus.UPCOMING.name)
                val allBookings = repository.getAllBookings()
                for (booking in allBookings) {
                    val strDate: Date = sdf.parse(booking.date)
                    if (helper.getCurrentDate()!!.compareTo(strDate) > 0) {
//                    repository.updateTicketStatus(BookedTicketStatus.COMPLETED.name, booking.bookingId)
                        repository.updatePassengerTicketStatus(
                            BookedTicketStatus.UPCOMING.name,
                            booking.bookingId
                        )
                    }
                }
            }
        }

    var isPassengerTicketCancelled = MutableLiveData<Boolean>(null)

    fun cancelPassengerTickets(booking: Bookings) {
        viewModelScope.launch(Dispatchers.IO) {
            val job = launch {
                for(passengerId in passengerIdToBeCancelled.indices){
                    repository.cancelPassengerTicket(passengerIdToBeCancelled[passengerId].passengerId)
                    repository.removeBookedSeats(booking.bookingId, passengerIdToBeCancelled[passengerId].passengerSeatCode)
                }
                val perTicketCost = booking.totalCost / booking.noOfTicketsBooked
                booking.noOfTicketsBooked = booking.noOfTicketsBooked - passengerIdToBeCancelled.size
                booking.totalCost = booking.totalCost - (passengerIdToBeCancelled.size * perTicketCost)
                println("TOTAL COST AFTER = ${booking.totalCost}")
                repository.updateBookingData(booking.bookingId, booking.totalCost, booking.noOfTicketsBooked)
            }
            job.join()
            withContext(Dispatchers.Main){
                isPassengerTicketCancelled.value = true
            }
        }
    }


    val passengerCheckedStatus = mutableListOf<Boolean>()
    val passengerIdToBeCancelled = mutableListOf<PassengerInformation>()



//    viewpager and tablayout position

    var currentScreenPosition: Int = 0

}