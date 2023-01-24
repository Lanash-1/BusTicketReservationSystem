package com.example.busticketreservationsystem.viewmodel.livedata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busticketreservationsystem.data.entity.Bus
import com.example.busticketreservationsystem.data.entity.RecentlyViewed
import com.example.busticketreservationsystem.data.entity.User
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel(
    private val repository: AppRepositoryImpl
): ViewModel() {

//    User Details Related

    lateinit var user: User

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
                fetchRecentlyViewedData()
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
                fetchRecentlyViewedData()
                isLoggedIn.value = true
            }
        }
    }



//    User Recently Viewed Bus related

    var recentlyViewedList = listOf<RecentlyViewed>()
    var recentlyViewedBusList = listOf<Bus>()
    var recentlyViewedPartnerList = listOf<String>()

    var dataFetched = MutableLiveData<Boolean>()

    fun fetchRecentlyViewedData(){
        viewModelScope.launch(Dispatchers.IO) {
            var recentlyViewed = listOf<RecentlyViewed>()
            val recentlyViewedBus = mutableListOf<Bus>()
            val recentlyViewedPartner = mutableListOf<String>()
            val fetchJob = launch {
                recentlyViewed = repository.getRecentlyViewed(user.userId).reversed()
                for(i in recentlyViewed.indices){
                    recentlyViewedBus.add(repository.getBus(recentlyViewed[i].busId))
                    recentlyViewedPartner.add(repository.getPartnerName(recentlyViewedBus[i].partnerId))
                }
            }
            fetchJob.join()
            withContext(Dispatchers.Main){
                recentlyViewedList = recentlyViewed
                recentlyViewedBusList = recentlyViewedBus
                recentlyViewedPartnerList = recentlyViewedPartner

                dataFetched.value = true

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

    var isNewUserInserted = MutableLiveData<Boolean>()
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
                println("USER : $user")
                repository.updateUserData(user)
                newUser = repository.getUserAccount(user.userId)
            }
            job.join()
            withContext(Dispatchers.Main){
                println("INSIDE UPDATE FUNCTION: $newUser")
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

    fun deleteUserAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteUserAccount(user)
        }
    }


//    fun checkAccountAvailable(mobile: String) {
//        viewModelScope
//    }


}