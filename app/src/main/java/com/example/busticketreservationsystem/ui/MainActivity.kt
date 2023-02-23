package com.example.busticketreservationsystem.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.enums.Themes
import com.example.busticketreservationsystem.data.database.AppDatabase
import com.example.busticketreservationsystem.data.entity.*
import com.example.busticketreservationsystem.data.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.ui.adminpanel.AdminPanelFragment
import com.example.busticketreservationsystem.ui.homepage.HomePageFragment
import com.example.busticketreservationsystem.ui.welcome.WelcomeFragment
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.livedata.BookingViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.livedata.BusViewModel
import com.example.busticketreservationsystem.viewmodel.livedata.UserViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BookingViewModelFactory
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val loginStatusViewModel: LoginStatusViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()

    private lateinit var busViewModel: BusViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var bookingViewModel: BookingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = AppDatabase.getDatabase(this.applicationContext)
        val repository = AppRepositoryImpl(database)

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModel = ViewModelProvider(this, busViewModelFactory)[BusViewModel::class.java]

        val userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]

        val bookingViewModelFactory = BookingViewModelFactory(repository)
        bookingViewModel = ViewModelProvider(this, bookingViewModelFactory)[BookingViewModel::class.java]


//      Theme preference start

        appThemeOperations()

//      login preference start

        loginPreferenceOperations(savedInstanceState)

//      fetch location data from json

        fetchLocationData()

//        update all ticket status

        updateTicketStatus()

    }

    private fun updateTicketStatus() {
        bookingViewModel.updateTicketStatus()
    }

    private fun fetchLocationData() {
        locationViewModel.locationData.clear()
        var stateName: String
        var cities = mutableListOf<String>()
        var areaList = mutableListOf<List<String>>()
        var areas = mutableListOf<String>()


        val jsonData = applicationContext.resources.openRawResource(
            applicationContext.resources.getIdentifier(
                "location",
                "raw",
                applicationContext.packageName
            )
        ).bufferedReader().use{it.readText()}

        val jsonString = JSONObject(jsonData)

        val statesList = jsonString.getJSONArray("states") as JSONArray

        for(i in 0 until statesList.length()){

            stateName = statesList.getJSONObject(i).get("name").toString()

            val cityList = statesList.getJSONObject(i).getJSONArray("cities") as JSONArray

            for(j in 0 until cityList.length()){

                cities.add(cityList.getJSONObject(j).get("name").toString())

                val areasList = cityList.getJSONObject(j).getJSONArray("areas") as JSONArray

                for(k in 0 until areasList.length()){
                    areas.add(areasList[k].toString())
                }

                areaList.add(areas)
                areas = mutableListOf()
            }

            locationViewModel.locationData.add(LocationModel(stateName, cities, areaList))
            cities = mutableListOf()
            areaList = mutableListOf()

        }
    }

    private fun loginPreferenceOperations(savedInstanceState: Bundle?) {
        val writeSharedPreferences: SharedPreferences = getSharedPreferences("LoginStatus", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = writeSharedPreferences.edit()

        if(savedInstanceState == null) {
            when (writeSharedPreferences.getString("status", "")) {
                "" -> {
                    getBusData()
                    editor.putString("status", LoginStatus.NEW.name)
                    loginStatusViewModel.status = LoginStatus.NEW
                    editor.apply()
                    supportFragmentManager.commit {
                        setCustomAnimations(R.anim.from_right, R.anim.to_left)
                        replace(R.id.main_fragment_container, WelcomeFragment())
                    }
                }
                LoginStatus.SKIPPED.name -> {
                    loginStatusViewModel.status = LoginStatus.SKIPPED
                    supportFragmentManager.commit {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        replace(R.id.main_fragment_container, HomePageFragment())
                    }
                }
                LoginStatus.LOGGED_IN.name -> {
                    loginStatusViewModel.status = LoginStatus.LOGGED_IN
                    supportFragmentManager.commit {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        replace(R.id.main_fragment_container, HomePageFragment())
                    }
                }
                LoginStatus.NEW.name -> {
                    loginStatusViewModel.status = LoginStatus.NEW
                    supportFragmentManager.commit {
                        setCustomAnimations(R.anim.from_right, R.anim.to_left)
                        replace(R.id.main_fragment_container, WelcomeFragment())
                    }
                }
                LoginStatus.LOGGED_OUT.name -> {
                    loginStatusViewModel.status = LoginStatus.LOGGED_OUT
                    supportFragmentManager.commit {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        replace(R.id.main_fragment_container, WelcomeFragment())
                    }
                }
                LoginStatus.ADMIN_LOGGED_IN.name -> {
                    loginStatusViewModel.status = LoginStatus.ADMIN_LOGGED_IN
                    supportFragmentManager.commit {
                        setCustomAnimations(R.anim.from_right, R.anim.to_left)
                        replace(R.id.main_fragment_container, AdminPanelFragment())
                    }
                }
            }
        }
    }

    private fun appThemeOperations() {
        val themePreference: SharedPreferences = getSharedPreferences("ThemeStatus", MODE_PRIVATE)

        val themeEditor: SharedPreferences.Editor = themePreference.edit()

        when(themePreference.getString("theme", "")){
            Themes.LIGHT.name -> {
                themeEditor.putString("theme", Themes.LIGHT.name)
                themeEditor.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            Themes.DARK.name -> {
                themeEditor.putString("theme", Themes.DARK.name)
                themeEditor.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            Themes.SYSTEM_DEFAULT.name -> {
                themeEditor.putString("theme", Themes.SYSTEM_DEFAULT.name)
                themeEditor.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            "" -> {
                themeEditor.putString("theme", Themes.LIGHT.name)
                themeEditor.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun getBusData() {

        val partnersList = mutableListOf<Partners>()
        val busList = mutableListOf<Bus>()
        val amenitiesList = mutableListOf<BusAmenities>()

        val pList = mutableListOf<Partners>()
        val bList = mutableListOf<List<Bus>>()
        val aList = mutableListOf<List<List<BusAmenities>>>()


        val jsonData = applicationContext.resources.openRawResource(
            applicationContext.resources.getIdentifier(
                "buses",
                "raw",
                applicationContext.packageName
            )
        ).bufferedReader().use{it.readText()}

        val jsonString = JSONObject(jsonData)

        val partners = jsonString.getJSONArray("partners") as JSONArray

        for(i in 0 until partners.length()){
            val partnerId: Int = i
            val partnerName = partners.getJSONObject(i).get("name").toString()
            val partnerMobile = partners.getJSONObject(i).get("mobileNumber").toString()
            val partnerEmail = partners.getJSONObject(i).get("emailId").toString()
            val busesOperated = partners.getJSONObject(i).get("noOfBusOperated").toString().toInt()

            val partner = Partners(partnerId+1, partnerName, busesOperated, partnerEmail, partnerMobile)
            partnersList.add(partner)
            pList.add(partner)

            val buses = partners.getJSONObject(i).getJSONArray("buses") as JSONArray

            val busSet = mutableListOf<Bus>()
            val amenitySet = mutableListOf<List<BusAmenities>>()

            for(j in 0 until buses.length()){

                val busId = busList.size
                val busName = buses.getJSONObject(j).get("busName").toString()
                val sourceLocation = buses.getJSONObject(j).get("sourceLocation").toString()
                val destinationLocation = buses.getJSONObject(j).get("destinationLocation").toString()
                val perTicketCost = buses.getJSONObject(j).get("perTicketCost").toString().toDouble()
                val busType = buses.getJSONObject(j).get("busType").toString()
                val totalSeats = buses.getJSONObject(j).get("totalSeats").toString().toInt()
                val startTime = buses.getJSONObject(j).get("startTime").toString()
                val reachTime = buses.getJSONObject(j).get("reachTime").toString()
                val duration = buses.getJSONObject(j).get("duration").toString()
                val bus = Bus(busId+1, partnerId, busName, sourceLocation, destinationLocation, perTicketCost, busType, totalSeats, totalSeats, startTime, reachTime, duration, 0.0, 0)
                busList.add(bus)
                busSet.add(bus)

                val amenities = buses.getJSONObject(j).getJSONArray("amenities") as JSONArray

                val singleBusAmenities = mutableListOf<BusAmenities>()

                for(k in 0 until amenities.length()){

                    val amenity = BusAmenities(k+1, busId, amenities[k].toString())
                    amenitiesList.add(amenity)
                    singleBusAmenities.add(amenity)

                }

                amenitySet.add(singleBusAmenities)
            }
            bList.add(busSet)
            aList.add(amenitySet)
        }
        insertBusDataToDb(pList, bList, aList)

    }

    private fun insertBusDataToDb(pList: MutableList<Partners>, bList: MutableList<List<Bus>>, aList: MutableList<List<List<BusAmenities>>>) {
        busViewModel.insertInitialBusData(pList, bList, aList)
    }

}