package com.example.busticketreservationsystem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.databinding.ItemPassengerInfoBinding
import com.example.busticketreservationsystem.enums.Gender
import com.example.busticketreservationsystem.interfaces.PassengerInfoChangeListener
import java.lang.Exception
import kotlin.math.abs

class PassengerInfoAdapter: RecyclerView.Adapter<PassengerInfoAdapter.PassengerInfoViewHolder>() {

    private lateinit var selectedSeats: List<String>

    fun setSelectedSeats(selectedSeats: List<String>){
        this.selectedSeats = selectedSeats
    }

    private lateinit var passengerInfoChangeListener: PassengerInfoChangeListener

    fun setPassengerInfoChangeListener(passengerInfoChangeListener: PassengerInfoChangeListener){
        this.passengerInfoChangeListener = passengerInfoChangeListener
    }

    inner class PassengerInfoViewHolder(val binding: ItemPassengerInfoBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.passengerNameInput.addTextChangedListener {
                passengerInfoChangeListener.onPassengerNameChanged(absoluteAdapterPosition, it.toString())
            }
            binding.ageInput.addTextChangedListener {
                try {
                    passengerInfoChangeListener.onPassengerAgeChanged(absoluteAdapterPosition, it.toString().toInt())
                }catch (error: Exception){
                    println("Error type")
                }
            }
            binding.genderRadioGroup.setOnCheckedChangeListener { radioGroup, id ->
                if(itemView.findViewById<RadioButton>(id).text == "Male"){
                    passengerInfoChangeListener.onPassengerGenderSelected(absoluteAdapterPosition, Gender.MALE)
                }else{
                    passengerInfoChangeListener.onPassengerGenderSelected(absoluteAdapterPosition, Gender.FEMALE)
                }
            }
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