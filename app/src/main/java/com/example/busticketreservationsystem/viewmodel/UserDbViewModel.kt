package com.example.busticketreservationsystem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.busticketreservationsystem.data.AppDatabase
import com.example.busticketreservationsystem.entity.User

class UserDbViewModel(
    application: Application
): AndroidViewModel(application) {

    private val appDb = AppDatabase.getDatabase(application.applicationContext)

    fun insertUserData(user: User){
        appDb.userDao().insert(user)
    }

    fun updateUserData(user: User){
        appDb.userDao().updateUserData(user.userId, user.username, user.emailId, user.mobileNumber, user.password, user.dob, user.gender)
    }

    fun getAccountCount(mobileNumber: String): Boolean{
//        return withContext(Dispatchers.IO){
            return appDb.userDao().getAccountCount(mobileNumber) == 0
//        }
    }

    fun isEmailExists(email: String): Boolean{
        return appDb.userDao().getEmailCount(email) == 0
    }

    fun getUserAccount(mobileNumber: String): User{
        return appDb.userDao().getUserAccount(mobileNumber)
    }

    fun getUserAccount(userId: Int): User{
        return appDb.userDao().getUserAccountByUserID(userId)
    }

    fun isPasswordMatching(mobileNumber: String, password: String): Boolean {
        return appDb.userDao().getPassword(mobileNumber) == password
    }

    fun updateUserPassword(password: String, mobileNumber: String) {
        appDb.userDao().updateUserPassword(password, mobileNumber)
    }

    fun deleteUserAccount(user: User){
        appDb.userDao().deleteUserAccount(user)
    }
}