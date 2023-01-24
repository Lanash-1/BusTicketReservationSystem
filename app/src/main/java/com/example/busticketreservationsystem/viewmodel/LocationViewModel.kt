package com.example.busticketreservationsystem.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.busticketreservationsystem.data.entity.LocationModel

class LocationViewModel: ViewModel() {

    var locationData = mutableListOf<LocationModel>()

    var states = mutableListOf<String>()
    var cities = mutableListOf<String>()
    var areas = mutableListOf<String>()

    var selectedSourceState = MutableLiveData<String>()
    var selectedSourceCity = MutableLiveData<String>()

    var selectedDestinationState = MutableLiveData<String>()
    var selectedDestinationCity = MutableLiveData<String>()

    fun fetchStates(){
        states.clear()
        for(location in locationData){
            states.add(location.state)
        }
    }

    fun fetchCities(state: String){
        cities.clear()
        for(location in locationData){
            if(location.state == state){
                for(city in location.cities){
                    cities.add(city)
                }
            }
        }
    }


    fun fetchAreas(selectedCity: String): List<String>{
        val areas = mutableListOf<String>()
        for(location in locationData){
            for(city in location.cities){
                if(city == selectedCity){
                    for(area in location.areas[location.cities.indexOf(selectedCity)]){
                        areas.add(area)
                    }
                }
            }
        }
        return areas
    }

}