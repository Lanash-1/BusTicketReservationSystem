package com.example.busticketreservationsystem.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.R
import com.example.busticketreservationsystem.databinding.ItemAmenityBinding
import com.example.busticketreservationsystem.enums.BusAmenities.*

class AmenitiesAdapter: RecyclerView.Adapter<AmenitiesAdapter.AmenitiesViewHolder>() {

    private var amenitiesList = listOf<String>()

    fun setAmenitiesList(amenitiesList: List<String>){
        this.amenitiesList = amenitiesList
    }

    class AmenitiesViewHolder(val binding: ItemAmenityBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmenitiesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemAmenityBinding.inflate(layoutInflater, parent, false)
        return AmenitiesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AmenitiesViewHolder, position: Int) {
        holder.binding.apply {
            when(amenitiesList[position]){
                BLANKETS.name -> {
                    amenityIcon.setImageResource(R.drawable.bed_sheets)
                    amenityText.text = "Blankets"
                }
                CHARGING_POINT.name -> {
                    amenityIcon.setImageResource(R.drawable.ic_baseline_charging_station_24)
                    amenityText.text = "Charging Point"
                }
                EMERGENCY_CONTACT_NUMBER.name -> {
                    amenityIcon.setImageResource(R.drawable.ic_baseline_local_phone_24)
                    amenityText.text = "Emergency Contact Number"
                }
                TRACK_MY_BUS.name -> {
                    amenityIcon.setImageResource(R.drawable.ic_baseline_directions_bus_24)
                    amenityText.text = "Bus Tracking"
                }
                WIFI.name -> {
                    amenityIcon.setImageResource(R.drawable.ic_baseline_wifi_24)
                    amenityText.text = "WIFI"
                }
                WATER_BOTTLE.name -> {
                    amenityIcon.setImageResource(R.drawable.plastic_bottle)
                    amenityText.text = "Water Bottle"
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return amenitiesList.size
    }
}