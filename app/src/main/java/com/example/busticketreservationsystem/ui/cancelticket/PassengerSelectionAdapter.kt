package com.example.busticketreservationsystem.ui.cancelticket

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.data.entity.PassengerInformation
import com.example.busticketreservationsystem.databinding.ItemCheckboxBinding
import com.example.busticketreservationsystem.listeners.OnItemClickListener

class PassengerSelectionAdapter: RecyclerView.Adapter<PassengerSelectionAdapter.PassengerSelectionViewHolder>() {

    private var passengerInformation = listOf<PassengerInformation>()
    private var checkedStatus = listOf<Boolean>()

    fun setPassengerInformation(passengerInformation: List<PassengerInformation>,
                                checkedStatus: List<Boolean>
    ) {
        this.passengerInformation = passengerInformation
        this.checkedStatus = checkedStatus
    }

    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }


    inner class PassengerSelectionViewHolder(val binding: ItemCheckboxBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.passengerCheckboxItem.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerSelectionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCheckboxBinding.inflate(layoutInflater, parent, false)
        return PassengerSelectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PassengerSelectionViewHolder, position: Int) {
        holder.binding.apply {
            passengerName.text = passengerInformation[position].passengerName
            seatNumber.text = passengerInformation[position].passengerSeatCode
            checkBox.isChecked = checkedStatus[position]
        }
    }

    override fun getItemCount(): Int {
        return passengerInformation.size
    }


}