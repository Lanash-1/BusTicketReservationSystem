package com.example.busticketreservationsystem.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.busticketreservationsystem.data.entity.LocationModel

class LocationViewModel: ViewModel() {

    var locationData = mutableListOf<LocationModel>()

}