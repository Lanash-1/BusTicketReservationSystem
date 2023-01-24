package com.example.busticketreservationsystem.viewmodel.livedata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.enums.BusTypes
import com.example.busticketreservationsystem.data.entity.*
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.enums.BusAmenities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BusViewModel(
    private val repository: AppRepositoryImpl
): ViewModel() {

//    Insert bus details fetched from json file

//    fun insertInitialData(
//        partnersList: MutableList<Partners>,
//        busList: MutableList<Bus>,
//        amenitiesList: MutableList<BusAmenities>
//    ) {
//        viewModelScope.launch(Dispatchers.IO) {
////            repository.insertPartnerData(partnersList)
//            repository.insertBusData(busList)
//            repository.insertBusAmenitiesData(amenitiesList)
//        }
//    }

    fun insertPartnerData(partner: Partners) {
        viewModelScope.launch(Dispatchers.IO) {
            val job = launch {
                repository.insertPartnerData(partner)
            }
            job.join()
            withContext(Dispatchers.Main){
                fetchPartners()
            }
        }
    }

    fun registerNewBus(newBus: Bus, amenities: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val job = launch {
                repository.insertBusData(newBus)
                val busId = repository.getBusData().last().busId
                for(amenity in amenities){
                    repository.insertBusAmenitiesData(BusAmenities(0, busId, amenity))
                }
            }
            job.join()
            withContext(Dispatchers.Main){
                //
            }
        }
    }


    var partners = listOf<Partners>()

    fun fetchPartners(){
        viewModelScope.launch(Dispatchers.IO) {
            var partnerList = listOf<Partners>()
            val job = launch {
                partnerList = repository.getPartnerData()
            }
            job.join()
            withContext(Dispatchers.Main){
                partners = partnerList
            }
        }
    }


//    Bus results related data operations

    var checkedList = listOf<Int>()
    var selectedSort: Int? = null

    var busResultDataFetched = MutableLiveData<Boolean>()

    var sourceLocation = ""
    var destinationLocation = ""

    var resultBusList = listOf<Bus>()
    var resultPartnerList = listOf<Partners>()

    fun fetchBusResultsDetails(){
        viewModelScope.launch(Dispatchers.IO) {
            var busList = listOf<Bus>()
            val partnerList = mutableListOf<Partners>()
            val fetchJob = launch {
                busList = repository.getBusOfRoute(sourceLocation, destinationLocation)
                if(checkedList.isNotEmpty() || selectedSort != null){
                    busList = sortAndFilterOperation(busList)
                }
                for(i in busList.indices){
                    partnerList.add(repository.getPartnerDetails(busList[i].partnerId))
                    val notAvailableSeat = repository.getBookedSeats(busList[i].busId, selectedDate)
                    busList[i].availableSeats = busList[i].totalSeats - notAvailableSeat.size
                }
            }
            fetchJob.join()
            withContext(Dispatchers.Main){
                resultBusList = busList
                resultPartnerList = partnerList

                busResultDataFetched.value = true
            }
        }
    }

    private fun sortAndFilterOperation(list: List<Bus>): List<Bus> {
        var newList = mutableListOf<Bus>()
        if(checkedList.isNotEmpty()) {

            for (filterOption in checkedList) {
                when (filterOption) {
                    R.id.ac_checkbox -> {
                        for (bus in list) {
                            if (!newList.contains(bus)) {
                                if (bus.busType == BusTypes.AC_SEATER.name) {
                                    newList.add(bus)
                                }
                            }
                        }
                    }
                    R.id.non_ac_checkbox -> {
                        for (bus in list) {
                            if (!newList.contains(bus)) {
                                if (bus.busType == BusTypes.NON_AC_SEATER.name) {
                                    newList.add(bus)
                                }
                            }
                        }
                    }
                    R.id.seater_checkbox -> {
                        for (bus in list) {
                            if (!newList.contains(bus)) {
                                if (bus.busType == BusTypes.AC_SEATER.name || bus.busType == BusTypes.NON_AC_SEATER.name) {
                                    newList.add(bus)
                                }
                            }
                        }
                    }
                    R.id.sleeper_checkbox -> {
                        for (bus in list) {
                            if (!newList.contains(bus)) {
                                if (bus.busType == BusTypes.SLEEPER.name) {
                                    newList.add(bus)
                                }
                            }
                        }
                    }
                }
            }
        }
        if(checkedList.isEmpty()){
            newList = list as MutableList<Bus>
        }
        var sortedList = listOf<Bus>()
        if(selectedSort != null){
            when(selectedSort){
                R.id.price_high_low -> {
                    sortedList = newList.sortedByDescending {
                        it.perTicketCost
                    }
                }
                R.id.price_low_high -> {
                    sortedList = newList.sortedBy {
                        it.perTicketCost
                    }
                }
                R.id.top_rated -> {
                    sortedList = newList.sortedByDescending {
                        it.ratingOverall
                    }
                }
                R.id.shortest_duration -> {
                    sortedList = newList.sortedBy {
                        it.duration.toFloat()
                    }
                }
            }
            if(newList.isNotEmpty()){
                newList = sortedList as MutableList<Bus>
            }
        }
        return newList
    }


//    Selected bus and seats related operations

    lateinit var selectedBus: Bus

    var selectedBusId = 0
    var selectedDate = ""

    var bookedSeatsList = listOf<String>()

    var seatDataFetched = MutableLiveData<Boolean>()

    var selectedBusSeatDimensions = mutableListOf<Int>()

    var selectedSeats = mutableListOf<String>()

    fun fetchBusSeatsData() {
        viewModelScope.launch(Dispatchers.IO) {
            var seatsList = listOf<String>()
            val fetchJob = launch {
                seatsList = repository.getBookedSeats(selectedBus.busId, selectedDate)
            }
            fetchJob.join()
            withContext(Dispatchers.Main){
                bookedSeatsList = seatsList

                seatDataFetched.value = true
            }
        }
    }


//    Bus info related operations

    var busReviewsList = MutableLiveData<List<Reviews>>()
    var userReview: Reviews? = null
    var busAmenities = MutableLiveData<List<String>>()

    var isUserReviewFetched = MutableLiveData<Boolean>()

    var isBusReviewUpdated = MutableLiveData<Boolean>()

    fun fetchBusAmenities() {
        viewModelScope.launch(Dispatchers.IO) {
            var amenities = listOf<String>()
            val fetchJob = launch {
                amenities = repository.getBusAmenities(selectedBus.busId)
            }
            fetchJob.join()
            withContext(Dispatchers.Main){
                busAmenities.value = amenities
            }
        }
    }

    fun fetchBusReviewData(){
        viewModelScope.launch(Dispatchers.IO) {
            var reviewsList = listOf<Reviews>()
            val fetchJob = launch {
                reviewsList = repository.getReviewData(selectedBus.busId)
            }
            fetchJob.join()
            withContext(Dispatchers.Main){
                selectedBus.ratingPeopleCount = reviewsList.size
                var overall = 0.0
                for(rating in reviewsList){
                    overall += rating.rating
                }
                selectedBus.ratingOverall = overall/selectedBus.ratingPeopleCount

                if(reviewsList.isEmpty()) {
                    selectedBus.ratingOverall = 0.0
                }

                updateBusRating(selectedBusId, selectedBus.ratingPeopleCount, selectedBus.ratingOverall)
                busReviewsList.value = reviewsList
            }
        }
    }

    fun updateBusRating(busId: Int, ratingCount: Int, overallRating: Double){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateBusRating(busId, ratingCount, overallRating)
        }
    }

    fun fetchUserReview(userId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            var usersReview: Reviews? = null
            val fetchJob = launch {
                usersReview = repository.usersBusReview(userId, selectedBus.busId)
            }
            fetchJob.join()
            withContext(Dispatchers.Main){
                userReview = usersReview
                isUserReviewFetched.value = true
            }
        }
    }

    fun insertUserReview(rating: Int, feedback: String, date: String, userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            var peopleCount: Int = 0
            var overallRating: Double = 0.0
            val job = launch {
                repository.insertReview(Reviews(0, userId, selectedBus.busId, rating, feedback, date))
                val reviewList = repository.getReviewData(selectedBus.busId)
                peopleCount = reviewList.size
                overallRating = (reviewList.sumOf {
                    it.rating
                }/peopleCount).toDouble()
                repository.updateBusRating(selectedBus.busId, peopleCount, overallRating)
            }
            job.join()
            withContext(Dispatchers.Main){
                isBusReviewUpdated.value = true
            }
        }
    }

    fun updateUserRating(rating: Int, feedback: String, date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val updateJob = launch {
                repository.updateReviewOfAUser(userReview!!.reviewId, rating, feedback)
            }
            updateJob.join()
            withContext(Dispatchers.Main){
                isBusReviewUpdated.value = true
            }
        }
    }

    var isUserBooked = MutableLiveData<Boolean>()

    fun checkUserBookedBus(userId: Int, busId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            var result: Boolean = false
            val job = launch {
                result = repository.isUserBooked(userId, busId)
            }
            job.join()
            withContext(Dispatchers.Main){
                isUserBooked.value = result
            }
        }
    }




//    Bus boarding and dropping

    var boardingPoints = listOf(
        "Maple Street Station",
        "Park Avenue Terminal",
        "Lakeside Loop",
        "Downtown Transfer Center",
        "Sunset Plaza",
        "Midtown Marketplace",
        "The Foothills Depot",
        "Mountain View Mall",
        "Pine Ridge Platform",
        "City Centre Station"
    )

    var droppingPoints = listOf(
        "Riverfront Road",
        "The Woodlands Park",
        "Rockville Corner",
        "Glenbrook Plaza",
        "Brookdale Junction",
        "Meadowlands Avenue",
        "Hampton Hills",
        "The Pines Terminal",
        "Tanglewood Mall",
        "Maplewood Meadows"
    )


    var boardingPoint = MutableLiveData("")

    var droppingPoint = MutableLiveData("")


//    Sort and filter related operations






}