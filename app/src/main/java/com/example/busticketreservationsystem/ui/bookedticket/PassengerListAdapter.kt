package com.example.busticketreservationsystem.ui.bookedticket

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.data.entity.PassengerInformation
import com.example.busticketreservationsystem.databinding.ItemPassengerBinding
import com.example.busticketreservationsystem.utils.Helper

class PassengerListAdapter: RecyclerView.Adapter<PassengerListAdapter.PassengerListViewHolder>() {

    private val helper = Helper()

    private var passengerInformationList = listOf<PassengerInformation>()

    fun setPassengerInformationList(passengerInformationList: List<PassengerInformation>){
        this.passengerInformationList = passengerInformationList
    }

    inner class PassengerListViewHolder(val binding: ItemPassengerBinding): RecyclerView.ViewHolder(binding.root) {
        init {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPassengerBinding.inflate(layoutInflater, parent, false)
        return PassengerListViewHolder(binding)
    }



    override fun onBindViewHolder(holder: PassengerListViewHolder, position: Int) {
        holder.binding.apply {
            passengerNameText.text = passengerInformationList[position].passengerName
            ageGenderText.text = "${passengerInformationList[position].passengerAge}, ${helper.getGenderText(passengerInformationList[position].passengerGender)}"
            seatNameText.text = "${passengerInformationList[position].passengerSeatCode}"
        }
    }

    override fun getItemCount(): Int {
        return passengerInformationList.size
    }
}