package com.example.busticketreservationsystem

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.commit
import com.example.busticketreservationsystem.entity.Bus
import com.example.busticketreservationsystem.entity.BusAmenities
import com.example.busticketreservationsystem.entity.Partners
import com.example.busticketreservationsystem.enums.LoginStatus
import com.example.busticketreservationsystem.viewmodel.BusDbViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val busDbViewModel: BusDbViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val writeSharedPreferences: SharedPreferences = getSharedPreferences("LoginStatus", MODE_PRIVATE)

        val editor: SharedPreferences.Editor = writeSharedPreferences.edit()

        if(savedInstanceState == null) {
            when (writeSharedPreferences.getString("status", "")) {
                "" -> {
                    getBusData()
                    editor.putString("status", LoginStatus.NEW.name)
                    editor.commit()
                    supportFragmentManager.commit {
                        replace(R.id.main_fragment_container, RegisterFragment())
                    }
                }
                LoginStatus.SKIPPED.name -> {
                    supportFragmentManager.commit {
                        replace(R.id.main_fragment_container, HomePageFragment())
                    }
                }
                LoginStatus.LOGGED_IN.name -> {
                    supportFragmentManager.commit {
                        replace(R.id.main_fragment_container, HomePageFragment())
                    }
                }
                LoginStatus.NEW.name -> {
                    getBusData()
                    supportFragmentManager.commit {
                        replace(R.id.main_fragment_container, RegisterFragment())
                    }
                }
                LoginStatus.LOGGED_OUT.name -> {
                    supportFragmentManager.commit {
                        replace(R.id.main_fragment_container, LoginFragment())
                    }
                }
            }
        }

    }

    private fun getBusData() {

        Toast.makeText(this, "Bus data called", Toast.LENGTH_SHORT).show()

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

                println("BUS NAME: ${buses.getJSONObject(j).get("busname")}")

                val busId = busList.size
                val busName = buses.getJSONObject(j).get("busname").toString()
                val sourceLocation = buses.getJSONObject(j).get("sourceLocation").toString()
                val destinationLocation = buses.getJSONObject(j).get("destinationLocation").toString()
                val perTicketCost = buses.getJSONObject(j).get("perTicketCost").toString().toDouble()
                val busType = buses.getJSONObject(j).get("busType").toString()
                val totalSeats = buses.getJSONObject(j).get("totalSeats").toString().toInt()

                val bus = Bus(busId, partnerId, busName, sourceLocation, destinationLocation, perTicketCost, busType, totalSeats, 0)
                busList.add(bus)

                val amenities = buses.getJSONObject(j).getJSONArray("amenities") as JSONArray

                for(k in 0 until amenities.length()){

//                    println("AMENITIES: ${amenities[k]}")
                    val amenity = BusAmenities(0, busId, amenities[k].toString())
                    amenitiesList.add(amenity)
//                    println("AMENITY k=$k =  ${amenities}")

                }
            }
        }

        GlobalScope.launch {
            busDbViewModel.insertPartnerData(partnersList)
            busDbViewModel.insertBusData(busList)
            busDbViewModel.insertBusAmenitiesData(amenitiesList)
        }


    }

}