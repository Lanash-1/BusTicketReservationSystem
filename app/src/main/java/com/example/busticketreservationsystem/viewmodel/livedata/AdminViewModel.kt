package com.example.busticketreservationsystem.viewmodel.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busticketreservationsystem.data.entity.Bus
import com.example.busticketreservationsystem.data.entity.Partners
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminViewModel(
    private val repository: AppRepositoryImpl
): ViewModel() {

//    Analytics related data

    var partnerCount = MutableLiveData<Int>()
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


}