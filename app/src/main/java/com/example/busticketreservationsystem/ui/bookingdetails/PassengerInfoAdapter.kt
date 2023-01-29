package com.example.busticketreservationsystem.ui.bookingdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.databinding.ItemPassengerInfoBinding
import com.example.busticketreservationsystem.enums.Gender
import com.example.busticketreservationsystem.listeners.PassengerInfoChangeListener
import java.lang.Exception

class PassengerInfoAdapter: RecyclerView.Adapter<PassengerInfoAdapter.PassengerInfoViewHolder>() {

    private lateinit var selectedSeats: List<String>

    private var check: Boolean = false

    fun setSelectedSeats(selectedSeats: List<String>){
        this.selectedSeats = selectedSeats
    }

    fun setCheck(){
        check = true
    }

    private lateinit var infoList: List<PassengerInfoModel>

    fun setPassengerInfoList(infoList: List<PassengerInfoModel>){
        this.infoList = infoList
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
            binding.genderRadioGroup.setOnCheckedChangeListener { _, id ->
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
            when(check){
                true -> {

//                    name check
                    if(infoList[position].name == null || infoList[position].name?.isEmpty() == true){
                        passengerNameLayout.error = "Name should not be empty"
                        passengerNameLayout.isErrorEnabled = true
                    }else{
                        passengerNameLayout.isErrorEnabled = false
                    }

//                    age check

                    if(infoList[position].age == null || infoList[position].age?.toString()!!.toInt() < 1){
                        ageInputLayout.error = "Invalid input"
                        ageInputLayout.isErrorEnabled = true
                    }else{
                        ageInputLayout.isErrorEnabled = false
                    }

//                    gender check

                }
                false -> {
//
                }
            }
            passengerTitleText.text = "Passenger ${position+1}"
            seatText.text = "Seat - ${selectedSeats[position]}"
            if(infoList[position].name != null){
                passengerNameInput.setText(infoList[position].name.toString())
            }
            if(infoList[position].age != null){
                ageInput.setText(infoList[position].age.toString())
            }
            if(infoList[position].gender != null){
                if(infoList[position].gender == Gender.MALE){
                    maleRadioButton.isChecked = true
                    femaleRadioButton.isChecked = false
                }else{
                    femaleRadioButton.isChecked = true
                    maleRadioButton.isChecked = false
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return selectedSeats.size
    }

}