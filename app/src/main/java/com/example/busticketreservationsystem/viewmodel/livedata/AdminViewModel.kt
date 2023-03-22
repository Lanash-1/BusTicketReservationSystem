package com.example.busticketreservationsystem.viewmodel.livedata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busticketreservationsystem.data.entity.Bookings
import com.example.busticketreservationsystem.data.entity.Bus
import com.example.busticketreservationsystem.data.entity.Partners
import com.example.busticketreservationsystem.data.entity.User
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.enums.BookedTicketStatus
import com.example.busticketreservationsystem.enums.BusTypes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminViewModel(
    private val repository: AppRepositoryImpl
): ViewModel() {

//    Analytics related data


    var selectedBookingId: Int = 0


    var transitionPosition: Int = 0
    var partnerCount = MutableLiveData(0)
    var busCount = 0
    var userCount = 0
    var ticketCount = 0

    fun fetchAnalyticsData() {
        viewModelScope.launch(Dispatchers.IO) {
            var pCount = 0
            var bCount = 0
            var uCount = 0
            var tCount = 0
            val job = launch {
                pCount = repository.getPartnerData().size
                bCount = repository.getBusData().size
                uCount = repository.getUserCount()
                tCount = repository.getBookingCount()
            }
            job.join()
            withContext(Dispatchers.Main){
                ticketCount = tCount
                userCount = uCount
                busCount = bCount
                partnerCount.value = pCount
            }
        }
    }


    var partnersList = MutableLiveData<List<Partners>>()

    var selectedPartner = Partners(0, "", 0, "", "")

    fun fetchPartnersData() {
        viewModelScope.launch(Dispatchers.IO) {
            var list = listOf<Partners>()
            val job = launch {
                list = repository.getPartnerData()
            }
            job.join()
            withContext(Dispatchers.Main){
                partnersList.value = list
            }
        }
    }

    fun updatePartnerDetails(partner: Partners) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePartnerDetails(partner)
        }
    }



//    Bus list related operations

    var allBuses = MutableLiveData<List<Bus>>()

    var selectedBus: Bus? = null

    fun fetchAllBuses(){
        viewModelScope.launch(Dispatchers.IO) {
            var busList = listOf<Bus>()
            val job = launch {
                busList = repository.getBusData()
            }
            job.join()
            withContext(Dispatchers.Main){
                allBuses.value = busList
            }
        }
    }


    var bookedTicketCount = MutableLiveData<Int>()

    fun fetchBookedTicketCount(partnerId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            var count = 0
            val job = launch {
                count = repository.getTicketCount(partnerId)
            }
            job.join()
            withContext(Dispatchers.Main){
                bookedTicketCount.value = count
            }
        }
    }


    var partnerBuses = MutableLiveData<List<Bus>>()

    fun getBusOfPartner(partnerId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            var buses = listOf<Bus>()
            val job = launch {
                buses = repository.getBusOfPartner(partnerId)
            }
            job.join()
            withContext(Dispatchers.Main){
                allBuses.value = buses
            }
        }
    }


    var userList = MutableLiveData<List<User>>()
    fun fetchAllUser() {
        viewModelScope.launch(Dispatchers.IO) {
            var users = listOf<User>()
            val job = launch {
                users = repository.getAllUsers()
            }
            job.join()
            withContext(Dispatchers.Main){
                userList.value = users
            }
        }
    }


    var upcomingCount = 0
    var completedCount = 0
    var cancelledCount = MutableLiveData<Int>(0)
//    fun fetchUserTicketCount(userId: Int) {
//        viewModelScope.launch(Dispatchers.IO) {
//            var upcoming = 0
//            var completed = 0
//            var cancelled = 0
//            val job = launch {
//                val ticketList = repository.getUserBookings(userId)
//                upcoming = ticketList.filter {
//                    it.bookedTicketStatus == BookedTicketStatus.UPCOMING.name
//                }.size
//                completed = ticketList.filter {
//                    it.bookedTicketStatus == BookedTicketStatus.COMPLETED.name
//                }.size
//                cancelled = ticketList.filter {
//                    it.bookedTicketStatus == BookedTicketStatus.CANCELLED.name
//                }.size
//            }
//            job.join()
//            withContext(Dispatchers.Main){
//                upcomingCount = upcoming
//                completedCount = completed
//                cancelledCount.value = cancelled
//            }
//        }
//
//    }




//    Add bus related operation

    var busName = ""
    var busEmail = ""
    var busMobile = ""

    var sourceState = ""
    var sourceCity = ""
    var destinationState = ""
    var destinationCity = ""

    var busType = ""
    var perTicketCost = 0

    var startTime = ""
    var reachTime = ""

    var amenities = listOf<String>()

    lateinit var newBus: Bus


//    add partner Related operation

    var partner = Partners(0, "", 0, "", "")

    var partnerName = ""
    var partnerEmail = ""
    var partnerMobile = ""


//    Analytics related data




//    chat related data

    var chatUserId: Int = 0


//    user

    lateinit var selectedUser: User

    var isUserFetched = MutableLiveData<Boolean>()
    fun fetchUserDetails(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            lateinit var user: User
            val job = launch {
                user = repository.getUserAccount(userId)
            }
            job.join()
            withContext(Dispatchers.Main){
                selectedUser = user
                isUserFetched.value = true
            }
        }
    }

    var bookedTicketList = listOf<Bookings>()
    var bookedBusList = listOf<Bus>()
    var bookedPartnerList = MutableLiveData<List<String>>()

    var filteredBookingList = listOf<Bookings>()
    var filteredBusList = listOf<Bus>()
    var filteredPartnerList = listOf<String>()
//    var filteredTicketStatus:

    fun fetchAllTickets() {
        viewModelScope.launch(Dispatchers.IO) {
            var bookings = listOf<Bookings>()
            val busList = mutableListOf<Bus>()
            val partnerName = mutableListOf<String>()
            val job = launch {
                bookings = repository.fetchAllTickets()
                for(booking in bookings){
                    busList.add(repository.getBus(booking.busId))
                }
                for(bus in busList){
                    partnerName.add(repository.getPartnerName(bus.partnerId))
                }
            }
            job.join()
            withContext(Dispatchers.Main){
                bookedTicketList = bookings
                bookedBusList = busList
                bookedPartnerList.value = partnerName
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

    lateinit var filteredTicketStatus: BookedTicketStatus


    val isBookingFetched = MutableLiveData<Boolean>(null)


    fun fetchAllBookings(){
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

            var userBookings = listOf<Bookings>()
            val job = launch {
                userBookings = repository.getAllBookings()
                for(booking in userBookings){
                    if(repository.checkPassengerTicketStatus(BookedTicketStatus.UPCOMING.name, booking.bookingId) > 0){
                        upcomingBookings.add(booking)
                    }
                    if(repository.checkPassengerTicketStatus(BookedTicketStatus.COMPLETED.name, booking.bookingId) > 0){
                        completedBookings.add(booking)
                    }
                    if(repository.checkPassengerTicketStatus(BookedTicketStatus.CANCELLED.name, booking.bookingId) > 0){
                        cancelledBookings.add(booking)
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

                for(bus in upcomingBuses){
                    upcomingPartners.add(repository.getPartnerName(bus.partnerId))
                }
                for(bus in completedBuses){
                    completedPartners.add(repository.getPartnerName(bus.partnerId))
                }
                for(bus in cancelledBuses){
                    cancelledPartners.add(repository.getPartnerName(bus.partnerId))
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

                isBookingFetched.value = true
            }
        }
    }



//  new add bus operations

//    adminViewModel.partner.partnerId,
    //                adminViewModel.busName,
//                adminViewModel.sourceCity,
//                adminViewModel.destinationCity,
//                adminViewModel.perTicketCost.toDouble(),
//                adminViewModel.busType,
//                totalSeatCount,
//                totalSeatCount,
//                adminViewModel.startTime,
//                adminViewModel.reachTime,
//                duration,
//                0.0,
//                0

    var selectedPartnerId: Int = -1
    var newBusName: String = ""
    var ticketCost: Int = 0
    var selectedBoardingState: String = ""
    var selectedBoardingCity: String = ""
    var selectedDroppingState: String = ""
    var selectedDroppingCity: String = ""
    var busStartingTime: String = ""
    var busDroppingTime: String = ""
    var newBusType: BusTypes? = null
    var hasUpperDeck = MutableLiveData<Boolean>(null)
    var numberOfDecks: Int = 0
    var lowerDeckSeatType: String = ""
    var lowerLeftColumnCount: Int = 0
    var lowerRightColumnCount: Int = 0
    var lowerLeftSeatCount: Int = 0
    var lowerRightSeatCount: Int = 0
    var upperLeftColumnCount: Int = 0
    var upperRightColumnCount: Int = 0
    var upperLeftSeatCount: Int = 0
    var upperRightSeatCount: Int = 0

}