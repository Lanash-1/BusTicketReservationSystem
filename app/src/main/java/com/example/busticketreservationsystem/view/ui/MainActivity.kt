package com.example.busticketreservationsystem.view.ui

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
import com.example.busticketreservationsystem.model.data.AppDatabase
import com.example.busticketreservationsystem.model.entity.*
import com.example.busticketreservationsystem.model.repository.AppRepositoryImpl
import com.example.busticketreservationsystem.viewmodel.*
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.BusViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodelfactory.UserViewModelFactory
import com.example.busticketreservationsystem.viewmodel.viewmodeltest.BusViewModel
import com.example.busticketreservationsystem.viewmodel.viewmodeltest.UserViewModel
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val loginStatusViewModel: LoginStatusViewModel by viewModels()

    private lateinit var busViewModel: BusViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = AppDatabase.getDatabase(this.applicationContext)
        val repository = AppRepositoryImpl(database)

        val busViewModelFactory = BusViewModelFactory(repository)
        busViewModel = ViewModelProvider(this, busViewModelFactory)[BusViewModel::class.java]

        val userViewModelFactory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(this, userViewModelFactory)[UserViewModel::class.java]


//      Check
//        GlobalScope.launch {
//            busViewModel.apply {
//                busList = busDbViewModel.getBusData()
//                partnerList = busDbViewModel.getPartnerData()
//            }
//        }

//        Theme preference start
        appThemeOperations()
//        val themePreference: SharedPreferences = getSharedPreferences("ThemeStatus", MODE_PRIVATE)
//
//        val themeEditor: SharedPreferences.Editor = themePreference.edit()
//
//        when(themePreference.getString("theme", "")){
//            Themes.LIGHT.name -> {
//                themeEditor.putString("theme", Themes.LIGHT.name)
//                themeEditor.apply()
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            }
//            Themes.DARK.name -> {
//                themeEditor.putString("theme", Themes.DARK.name)
//                themeEditor.apply()
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            }
//            Themes.SYSTEM_DEFAULT.name -> {
//                themeEditor.putString("theme", Themes.SYSTEM_DEFAULT.name)
//                themeEditor.apply()
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
//            }
//            "" -> {
//                themeEditor.putString("theme", Themes.SYSTEM_DEFAULT.name)
//                themeEditor.apply()
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
//            }
//        }


//      login preference start

        loginPreferenceOperations(savedInstanceState)

//        val writeSharedPreferences: SharedPreferences = getSharedPreferences("LoginStatus", MODE_PRIVATE)
//        val editor: SharedPreferences.Editor = writeSharedPreferences.edit()
//
//        if(savedInstanceState == null) {
//            when (writeSharedPreferences.getString("status", "")) {
//                "" -> {
//                    getBusData()
//                    editor.putString("status", LoginStatus.NEW.name)
//                    loginStatusViewModel.status = LoginStatus.NEW
//                    editor.apply()
//                    supportFragmentManager.commit {
//                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                        replace(R.id.main_fragment_container, RegisterFragment())
//                    }
//                }
//                LoginStatus.SKIPPED.name -> {
//                    loginStatusViewModel.status = LoginStatus.SKIPPED
//                    supportFragmentManager.commit {
//                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                        replace(R.id.main_fragment_container, HomePageFragment())
//                    }
//                }
//                LoginStatus.LOGGED_IN.name -> {
//                    loginStatusViewModel.status = LoginStatus.LOGGED_IN
//                    supportFragmentManager.commit {
//                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                        replace(R.id.main_fragment_container, HomePageFragment())
//                    }
//                    GlobalScope.launch {
//                        lateinit var user: User
//                        val job = launch {
//                            user = userDbViewModel.getUserAccount(writeSharedPreferences.getInt("userId", 0))
//                        }
//                        job.join()
//                        withContext(Dispatchers.IO){
//                            userViewModel.user = user
//                            bookingHistoryOperations(user.userId)
//                        }
//                    }
//
//                }
//                LoginStatus.NEW.name -> {
//                    getBusData()
//                    loginStatusViewModel.status = LoginStatus.NEW
//                    supportFragmentManager.commit {
//                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                        replace(R.id.main_fragment_container, RegisterFragment())
//                    }
//                }
//                LoginStatus.LOGGED_OUT.name -> {
//                    loginStatusViewModel.status = LoginStatus.LOGGED_OUT
//                    supportFragmentManager.commit {
//                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                        replace(R.id.main_fragment_container, LoginFragment())
//                    }
//                }
//            }
//        }
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
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        replace(R.id.main_fragment_container, RegisterFragment())
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

//                    fetch data of user already logged in
                    userViewModel.fetchUserData(writeSharedPreferences.getInt("userId", 0))

                    supportFragmentManager.commit {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        replace(R.id.main_fragment_container, HomePageFragment())
                    }

//                    check
//                    GlobalScope.launch {
//                        lateinit var user: User
//                        val job = launch {
//                            user = userDbViewModel.getUserAccount(writeSharedPreferences.getInt("userId", 0))
//                        }
//                        job.join()
//                        withContext(Dispatchers.IO){
//                            userViewModel.user = user
////                            bookingHistoryOperations(user.userId)
//                        }
//                    }

                }
                LoginStatus.NEW.name -> {
                    getBusData()
                    loginStatusViewModel.status = LoginStatus.NEW
                    supportFragmentManager.commit {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        replace(R.id.main_fragment_container, RegisterFragment())
                    }
                }
                LoginStatus.LOGGED_OUT.name -> {
                    loginStatusViewModel.status = LoginStatus.LOGGED_OUT
                    supportFragmentManager.commit {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        replace(R.id.main_fragment_container, LoginFragment())
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
                themeEditor.putString("theme", Themes.SYSTEM_DEFAULT.name)
                themeEditor.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun bookingHistoryOperations(userId: Int) {
//        getBookingHistoryList(userId)
    }

//    private fun getBookingHistoryList(userId: Int) {
//        GlobalScope.launch {
//            var bookingList = listOf<Bookings>()
//            val busList = mutableListOf<Bus>()
//            val partnerList = mutableListOf<String>()
//            var passengerList = listOf<PassengerInformation>()
//            val job = launch {
//                bookingList = bookingDbViewModel.getUserBookings(userId)
//                passengerList = bookingDbViewModel.getPassengerInfo()
//                for (booking in bookingList){
//                    busList.add(busDbViewModel.getBus(booking.busId))
//                }
//                for(bus in busList){
//                    partnerList.add(busDbViewModel.getPartnerName(bus.partnerId))
//                }
//                for(i in bookingList.indices){
//                    if(bookingList[i].bookedTicketStatus == BookedTicketStatus.UPCOMING.name){
//                        val sdf = SimpleDateFormat("dd/MM/yyyy")
//                        val strDate: Date = sdf.parse(bookingList[i].date)
//                        val time = Calendar.getInstance().time
//                        val current = sdf.format(time)
//                        val currentDate = sdf.parse(current)
//
//                        if (currentDate.compareTo(strDate) > 0) {
//                            bookingList[i].bookedTicketStatus = BookedTicketStatus.COMPLETED.name
//                            bookingDbViewModel.updateTicketStatus(BookedTicketStatus.COMPLETED.name, bookingList[i].bookingId)
//                        }
//                    }
//                }
//            }
//            job.join()
//            withContext(Dispatchers.IO){
//                bookingViewModel.bookingHistory = bookingList
//                bookingViewModel.bookedBusesList = busList
//                bookingViewModel.bookedPartnerList = partnerList
//                bookingViewModel.bookedPassengerInfo = passengerList
//            }
//        }
//    }

    private fun getBusData() {

        val partnersList = mutableListOf<Partners>()
        val busList = mutableListOf<Bus>()
        val amenitiesList = mutableListOf<BusAmenities>()

        val jsonData = applicationContext.resources.openRawResource(
            applicationContext.resources.getIdentifier(
                "jsonformatter",
                "raw",
                applicationContext.packageName
            )
        ).bufferedReader().use{it.readText()}

        val jsonString = JSONObject(jsonData)

        println(jsonString)

        val partners = jsonString.getJSONArray("partners") as JSONArray

        for(i in 0 until partners.length()){
            val partnerId: Int = i
            val partnerName = partners.getJSONObject(i).get("name").toString()
            val partnerMobile = partners.getJSONObject(i).get("mobileNumber").toString()
            val partnerEmail = partners.getJSONObject(i).get("emailId").toString()
            val busesOperated = partners.getJSONObject(i).get("noOfBusOperated").toString().toInt()

            val partner = Partners(partnerId, partnerName, busesOperated, partnerEmail, partnerMobile)
            partnersList.add(partner)

//            println("Name: $partnerName\nMobile: $partnerMobile\n Email: $partnerEmail\nBuses No: $busesOperated")

            val buses = partners.getJSONObject(i).getJSONArray("buses") as JSONArray

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
                val bus = Bus(busId, partnerId, busName, sourceLocation, destinationLocation, perTicketCost, busType, totalSeats, totalSeats, startTime, reachTime, duration, 0.0, 0)
                busList.add(bus)

                val amenities = buses.getJSONObject(j).getJSONArray("amenities") as JSONArray

                for(k in 0 until amenities.length()){

                    val amenity = BusAmenities(0, busId, amenities[k].toString())
                    amenitiesList.add(amenity)

                }
            }
        }

        insertDataToDb(partnersList, busList, amenitiesList)

//        GlobalScope.launch {
//            busDbViewModel.insertPartnerData(partnersList)
//            busDbViewModel.insertBusData(busList)
//            busDbViewModel.insertBusAmenitiesData(amenitiesList)
//            busViewModel.apply {
//                this.busList = busDbViewModel.getBusData()
//                this.partnerList = busDbViewModel.getPartnerData()
//            }
//        }
    }

    private fun insertDataToDb(
        partnersList: MutableList<Partners>,
        busList: MutableList<Bus>,
        amenitiesList: MutableList<BusAmenities>
    ) {
        busViewModel.insertInitialData(partnersList, busList, amenitiesList)
    }

}