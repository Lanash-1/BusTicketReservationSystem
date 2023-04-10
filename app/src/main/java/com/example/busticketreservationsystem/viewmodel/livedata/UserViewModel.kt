package com.example.busticketreservationsystem.viewmodel.livedata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busticketreservationsystem.data.entity.Bus
import com.example.busticketreservationsystem.data.entity.RecentlyViewed
import com.example.busticketreservationsystem.data.entity.User
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.enums.BookedTicketStatus
import com.example.busticketreservationsystem.utils.Helper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(
    private val repository: AppRepositoryImpl
): ViewModel() {

    private val helper = Helper()

//    User Details Related

    lateinit var user: User

    var isUserFetched = MutableLiveData<Boolean>()

    var isLoggedIn = MutableLiveData<Boolean>()

    fun fetchUserData(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            lateinit var userData: User
            val fetchJob = launch {
                userData = repository.getUserAccount(userId)
            }
            fetchJob.join()
            withContext(Dispatchers.Main){
                user = userData
                isUserFetched.value = true
//                fetchRecentlyViewedData()
            }
        }
    }

    fun fetchUserData(mobile: String) {
        viewModelScope.launch(Dispatchers.IO) {
            lateinit var userData: User
            val fetchJob = launch {
                userData = repository.getUserAccount(mobile)
            }
            fetchJob.join()
            withContext(Dispatchers.Main){
                user = userData
//                fetchRecentlyViewedData()
                isLoggedIn.value = true
            }
        }
    }



//    User Recently Viewed Bus related

    var recentlyViewedList = MutableLiveData<MutableList<RecentlyViewed>>()
    var recentlyViewedBusList = mutableListOf<Bus>()
    var recentlyViewedPartnerList = mutableListOf<String>()

//    var dataFetched = MutableLiveData<Boolean>()

    fun fetchRecentlyViewedData(){
        viewModelScope.launch(Dispatchers.IO) {
            var recentlyViewed = listOf<RecentlyViewed>()
            val recentlyViewedBus = mutableListOf<Bus>()
            val recentlyViewedPartner = mutableListOf<String>()
            val filteredRecentlyViewed = mutableListOf<RecentlyViewed>()
            val fetchJob = launch {
                recentlyViewed = repository.getRecentlyViewed(user.userId).reversed()
                for(i in recentlyViewed.indices){
                    if(helper.compareToCurrentDate(recentlyViewed[i].date)){
//                        println("Older DATE")
                    }else{
                        recentlyViewedBus.add(repository.getBus(recentlyViewed[i].busId))
                        recentlyViewedPartner.add(repository.getPartnerName(recentlyViewedBus[i].partnerId))
                        filteredRecentlyViewed.add(recentlyViewed[i])
                    }
                }
            }
            fetchJob.join()
            withContext(Dispatchers.Main){
                recentlyViewedBusList = recentlyViewedBus
                recentlyViewedPartnerList = recentlyViewedPartner
//                if(filteredRecentlyViewed.isNotEmpty()){
                    recentlyViewedList.value = filteredRecentlyViewed
//                }
            }
        }
    }

    fun removeRecentlyViewedData(recentlyViewed: RecentlyViewed) {
        viewModelScope.launch(Dispatchers.IO) {
            val removeJob = launch {
                repository.removeRecentlyViewed(recentlyViewed)
            }
            removeJob.join()
            fetchRecentlyViewedData()
        }
    }

    fun addRecentlyViewed(selectedBusId: Int, date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val addJob = launch {
                if(!repository.isRecentlyViewedAvailable(user.userId, selectedBusId, date)){
                    repository.insertRecentlyViewed(RecentlyViewed(0, selectedBusId, user.userId, date))
                }
            }
            addJob.join()
            withContext(Dispatchers.Main){
                fetchRecentlyViewedData()
            }
        }
    }


//    Login and Signup related

    var isNewUserInserted = MutableLiveData<Boolean>(null)
    var isMobileExists = MutableLiveData<Boolean>()
    var isPasswordMatching = MutableLiveData<Boolean>()

    fun isNumberAlreadyExists(number: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var result: Boolean? = null
            val job = launch {
                result = repository.getAccountCount(number)
            }
            job.join()
            withContext(Dispatchers.Main){
                isMobileExists.value = result!!
            }
        }
    }

    fun insertNewUser(password: String, mobile: String) {
        viewModelScope.launch(Dispatchers.IO) {
            lateinit var newUser: User
            val job = launch {
                repository.insertUserData(User(0, "", "", mobile, password, "", ""))
                newUser = repository.getUserAccount(mobile)
            }
            job.join()
            withContext(Dispatchers.Main){
                user = newUser
                isNewUserInserted.value = true
            }
        }
    }

    fun updateUserDetails(){
        viewModelScope.launch(Dispatchers.IO) {
            lateinit var newUser: User
            val job = launch {
                repository.updateUserData(user)
                newUser = repository.getUserAccount(user.userId)
            }
            job.join()
            withContext(Dispatchers.Main){
                user = newUser
            }
        }
    }

    fun isPasswordMatching(mobile: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var result = false
            val job = launch {
                result = repository.isPasswordMatching(mobile, password)
            }
            job.join()
            withContext(Dispatchers.Main){
                isPasswordMatching.value = result
            }
        }
    }

    var isEmailExists = MutableLiveData<Boolean>()

    fun isEmailExists(emailText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var result: Boolean = false
            val job = launch {
                result = repository.isEmailExists(emailText)
            }
            job.join()
            withContext(Dispatchers.Main){
                isEmailExists.value = !result
            }
        }
    }


    var isAccountAvailable = MutableLiveData<Boolean>()

    fun updateNewPassword(password: String, mobileNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var result: Boolean = false
            val job = launch {
                result = repository.getAccountCount(mobileNumber)
                if(result){
                    repository.updateUserPassword(password, mobileNumber)
                }else{
                    result = false
                }
            }
            job.join()
            withContext(Dispatchers.Main){
                isAccountAvailable.value = result
            }
        }
    }

    var isUserPasswordUpdated = MutableLiveData<Boolean>(null)

    fun updateUserPassword(mobileNumber: String, password: String){
        viewModelScope.launch(Dispatchers.IO) {
            val job = launch {
                repository.updateUserPassword(password, mobileNumber)
            }
            job.join()
            withContext(Dispatchers.Main){
                isUserPasswordUpdated.value = true
            }
        }
    }

    fun deleteUserAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteUserAccount(user.userId)
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

}