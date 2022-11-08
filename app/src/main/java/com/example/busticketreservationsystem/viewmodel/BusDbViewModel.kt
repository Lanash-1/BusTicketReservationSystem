package com.example.busticketreservationsystem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.busticketreservationsystem.data.AppDatabase

class BusDbViewModel(application: Application): AndroidViewModel(application) {

    private val appDb = AppDatabase.getDatabase(application.applicationContext)

}