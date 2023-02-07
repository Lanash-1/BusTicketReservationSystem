package com.example.busticketreservationsystem.viewmodel.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busticketreservationsystem.data.entity.Bus
import com.example.busticketreservationsystem.data.entity.Partners
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.buseslist.BusesListFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminViewModel(
    private val repository: AppRepositoryImpl
): ViewModel() {

//    Analytics related data


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



}