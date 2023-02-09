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
import com.example.busticketreservationsystem.ui.login.LoginFragment
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
import java.text.SimpleDateFormat
import java.util.*

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


        println("App THEME OPeration")

//      Theme preference start

        appThemeOperations()

//      login preference start

        loginPreferenceOperations(savedInstanceState)

//      fetch location data from json

        fetchLocationData()

    }

//     END OF ON-CREATE

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
                    println("EMPTY")
                    editor.putString("status", LoginStatus.NEW.name)
                    loginStatusViewModel.status = LoginStatus.NEW
                    editor.apply()
                    supportFragmentManager.commit {
                        setCustomAnimations(R.anim.from_right, R.anim.to_left)
                        replace(R.id.main_fragment_container, WelcomeFragment())
                    }
                }
                LoginStatus.SKIPPED.name -> {
                    println("SKIPPED")

                    loginStatusViewModel.status = LoginStatus.SKIPPED
                    supportFragmentManager.commit {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        replace(R.id.main_fragment_container, HomePageFragment())
                    }
                }
                LoginStatus.LOGGED_IN.name -> {
                    println("LOG IN")

                    loginStatusViewModel.status = LoginStatus.LOGGED_IN

                    updateBookingHistory(writeSharedPreferences.getInt("userId", 0))

                    supportFragmentManager.commit {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        replace(R.id.main_fragment_container, HomePageFragment())
                    }
                }
                LoginStatus.NEW.name -> {
                    println("NEW ")

                    loginStatusViewModel.status = LoginStatus.NEW
                    supportFragmentManager.commit {
                        setCustomAnimations(R.anim.from_right, R.anim.to_left)
                        replace(R.id.main_fragment_container, WelcomeFragment())
                    }
                }
                LoginStatus.LOGGED_OUT.name -> {
                    println("LOG OUT")

                    loginStatusViewModel.status = LoginStatus.LOGGED_OUT
                    supportFragmentManager.commit {
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        replace(R.id.main_fragment_container, WelcomeFragment())
                    }
                }
                LoginStatus.ADMIN_LOGGED_IN.name -> {
                    println("ADMIN LOG IN")

                    loginStatusViewModel.status = LoginStatus.ADMIN_LOGGED_IN
                    supportFragmentManager.commit {
                        setCustomAnimations(R.anim.from_right, R.anim.to_left)
                        replace(R.id.main_fragment_container, AdminPanelFragment())
                    }
                }
                else -> {
                    println("ELSE NOT OPENING")
                }
            }
        }
    }

    private fun updateBookingHistory(userId: Int) {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val time = Calendar.getInstance().time
        val current = sdf.format(time)
        val currentDate = sdf.parse(current)
        bookingViewModel.updateBookingHistoryList(userId, currentDate)
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
}