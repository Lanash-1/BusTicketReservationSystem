package com.example.busticketreservationsystem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.databinding.ItemPassengerInfoBinding

class PassengerInfoAdapter: RecyclerView.Adapter<PassengerInfoAdapter.PassengerInfoViewHolder>() {

    private lateinit var selectedSeats: List<String>

    fun setSelectedSeats(selectedSeats: List<String>){
        this.selectedSeats = selectedSeats
    }

    inner class PassengerInfoViewHolder(val binding: ItemPassengerInfoBinding): RecyclerView.ViewHolder(binding.root) {
        init {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerInfoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemPassengerInfoBinding.inflate(layoutInflater, parent, false)
        return PassengerInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PassengerInfoViewHolder, position: Int) {
        holder.binding.apply {
            passengerTitleText.text = "Passenger ${position+1}"
            seatText.text = "Seat - ${selectedSeats[position]}"
        }
    }

    override fun getItemCount(): Int {
        return selectedSeats.size
    }
}