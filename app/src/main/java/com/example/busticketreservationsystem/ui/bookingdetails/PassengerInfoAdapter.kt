package com.example.busticketreservationsystem.ui.bookingdetails

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.databinding.ItemPassengerInfoBinding
import com.example.busticketreservationsystem.enums.Gender
import com.example.busticketreservationsystem.listeners.PassengerInfoChangeListener
import com.example.busticketreservationsystem.utils.Helper
import java.lang.Exception

class PassengerInfoAdapter: RecyclerView.Adapter<PassengerInfoAdapter.PassengerInfoViewHolder>() {

    private val helper = Helper()

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
                    Log.d("PASSENGER_INFO_ADAPTER", "passenger age changed method in booking details page")
                }
            }
//            binding.genderRadioGroup.setOnCheckedChangeListener { _, id ->
//                if(itemView.findViewById<RadioButton>(id).text == "Male"){
//                    passengerInfoChangeListener.onPassengerGenderSelected(absoluteAdapterPosition, Gender.MALE)
//                }else{
//                    passengerInfoChangeListener.onPassengerGenderSelected(absoluteAdapterPosition, Gender.FEMALE)
//                }
//            }
            binding.maleRadioButton.setOnClickListener{
                binding.maleRadioButton.visibility = View.GONE
                binding.maleRadioButtonSelected.visibility = View.VISIBLE
                binding.femaleRadioButton.visibility = View.VISIBLE
                binding.femaleRadioButtonSelected.visibility = View.GONE
                passengerInfoChangeListener.onPassengerGenderSelected(absoluteAdapterPosition, Gender.MALE)
            }
            binding.femaleRadioButton.setOnClickListener{
                binding.maleRadioButton.visibility = View.VISIBLE
                binding.maleRadioButtonSelected.visibility = View.GONE
                binding.femaleRadioButton.visibility = View.GONE
                binding.femaleRadioButtonSelected.visibility = View.VISIBLE
                passengerInfoChangeListener.onPassengerGenderSelected(absoluteAdapterPosition, Gender.FEMALE)
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
                        ageInput.setText("")
                    }else{
                        passengerNameLayout.isErrorEnabled = false
                        passengerNameInput.setText(infoList[position].name)
                    }

//                    age check

//                    checkAge(infoList[position].age)


                    if(infoList[position].age == null || infoList[position].age?.toString()!!.toInt() < 1){
                        if(infoList[position].age == null){
                            ageInputLayout.error = "Should not be empty"
                        }else{
                            ageInputLayout.error = "Invalid input"
                        }
                        ageInputLayout.isErrorEnabled = true
                        ageInput.setText("")
                    }else{
                        ageInputLayout.isErrorEnabled = false
                        ageInput.setText(infoList[position].age.toString())
                    }

//                    gender check
                    if(infoList[position].gender == null){
                        radioGroupHelperText.visibility = View.VISIBLE
                    }else{
                        radioGroupHelperText.visibility = View.GONE
                    }

                }
                false -> {
//
                }
            }
            passengerTitleText.text = "Passenger ${position+1}"
            seatText.text = selectedSeats[position]
            deckText.text = helper.getDeck(selectedSeats[position])
            if(infoList[position].name != null){
                passengerNameInput.setText(infoList[position].name.toString())
            }
            if(infoList[position].age != null){
                ageInput.setText(infoList[position].age.toString())
            }
            if(infoList[position].gender != null){
                if(infoList[position].gender == Gender.MALE){
//                    maleRadioButton.isChecked = true
                    maleRadioButton.visibility = View.GONE
                    maleRadioButtonSelected.visibility = View.VISIBLE
                    femaleRadioButton.visibility = View.VISIBLE
                    femaleRadioButtonSelected.visibility = View.GONE
//                    femaleRadioButton.isChecked = false
                }else{
//                    femaleRadioButton.isChecked = true
//                    maleRadioButton.isChecked = false
                    maleRadioButton.visibility = View.VISIBLE
                    maleRadioButtonSelected.visibility = View.GONE
                    femaleRadioButton.visibility = View.GONE
                    femaleRadioButtonSelected.visibility = View.VISIBLE
                }
            }
        }
    }

//    private fun checkAge(age: Int?): String {
//        return if(age == null){
//            "Should not be empty"
//        }else{
//            "Enter proper age"
//        }
//    }


    override fun getItemCount(): Int {
        return selectedSeats.size
    }

//    override fun onViewRecycled(holder: PassengerInfoViewHolder) {
//        super.onViewRecycled(holder)
//        println("TEXT IN SECOND = ${holder.binding.passengerNameInput.text}")
//    }

}