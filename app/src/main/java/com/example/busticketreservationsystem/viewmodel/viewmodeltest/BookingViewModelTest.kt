package com.example.busticketreservationsystem.viewmodel.viewmodeltest

import android.view.Display
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busticketreservationsystem.enums.BookedTicketStatus
import com.example.busticketreservationsystem.model.entity.*
import com.example.busticketreservationsystem.model.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.view.ui.PassengerInfoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookingViewModelTest(
    private val repository: AppRepositoryImpl
): ViewModel() {

//    Booking History list related data

    var bookingHistoryBusList = listOf<Bus>()
    var bookingHistoryBookingList = listOf<Bookings>()

    var bookingDataFetched = MutableLiveData<Boolean>()


    fun fetchBookingHistory(userId: Int, ticketStatus: String){
        viewModelScope.launch(Dispatchers.IO) {
            val busList = mutableListOf<Bus>()
            var bookings = listOf<Bookings>()
            val fetchJob = launch {
                bookings = repository.getUserBookings(userId, ticketStatus)
                for(i in bookings){
                    busList.add(repository.getBus(i.busId))
                }
            }
            fetchJob.join()
            withContext(Dispatchers.Main){
                bookingHistoryBookingList = bookings
                bookingHistoryBusList = busList

                bookingDataFetched.value = true
            }
        }
    }


//    Booked ticket details related

    var selectedTicket = 0

    lateinit var bookedBus: Bus
    lateinit var booking: Bookings
    lateinit var bookedPartner: Partners
    lateinit var bookedSeatInformation: List<String>
    lateinit var bookedPassengerInformation: List<PassengerInformation>

    var bookedTicketDataFetched = MutableLiveData<Boolean>()
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

    fun fetchBookedTicketDetails(bookingId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            lateinit var bus: Bus
            lateinit var bookingDetails: Bookings
            lateinit var partner: Partners
            lateinit var seats: List<String>
            lateinit var passengerInfo: List<PassengerInformation>
            val fetchJob = launch {
                bookingDetails = repository.getParticularBooking(bookingId)
                bus = repository.getBus(bookingDetails.busId)
                partner = repository.getPartnerDetails(bus.partnerId)
                seats = repository.getSeatsOfParticularBooking(bookingDetails.bookingId)
                passengerInfo = repository.getBookedTicketPassengerInfo(bookingDetails.bookingId)
            }
            fetchJob.join()
            withContext(Dispatchers.Main){
                booking = bookingDetails
                bookedBus = bus
                bookedPartner = partner
                bookedSeatInformation = seats
                bookedPassengerInformation = passengerInfo

                bookedTicketDataFetched.value = true
            }
        }
    }



//    Ticket booking process related information and operations

    var totalTicketCost = 0.0
    var selectedSeats = listOf<String>()

    lateinit var selectedBus: Bus


    var contactMobileNumber: String? = null
    var contactEmailId: String? = null

    var boardingLocation = ""
    var droppingLocation = ""

    var seatInformationList = mutableListOf<SeatInformation>()
    var passengerInformationList = mutableListOf<PassengerInformation>()

    var passengerInfo: MutableList<PassengerInfoModel> = mutableListOf()

    var isTicketBooked = MutableLiveData<Boolean>()



    fun insertBookingOperation(booking: Bookings, selectedDate: String, userId: Int) {
//        insert booking and insert seat information and insert passenger information

        viewModelScope.launch(Dispatchers.IO) {
            var bookingId: Int
            val job = launch {
                repository.insertBooking(booking)
                bookingId = repository.getBookingId(userId)
                for(i in selectedSeats.indices){
                    repository.insertSeatInformation(SeatInformation(0, selectedBus.busId, bookingId, selectedSeats[i], selectedDate))
                }
                for(i in 0 until passengerInfo.size){
                    repository.insertPassengerInfo(PassengerInformation(0, bookingId, passengerInfo[i].name!!, passengerInfo[i].age!!, passengerInfo[i].gender!!.name, selectedSeats[i]))
                }
                fetchBookedTicketDetails(bookingId)
            }
            job.join()
            withContext(Dispatchers.Main){
                isTicketBooked.value = true
            }
        }
    }




}