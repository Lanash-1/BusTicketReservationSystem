package com.example.busticketreservationsystem.ui.boardingdropping

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.databinding.ItemRadioLocationBinding
import com.example.busticketreservationsystem.listeners.OnItemClickListener

class BoardingDroppingLocationAdapter: RecyclerView.Adapter<BoardingDroppingLocationAdapter.BoardingDroppingLocationViewHolder>() {

    private var currPos = -1

    private lateinit var locationsList: List<String>

    fun setSelectedPosition(position: Int) {
        currPos = position
        notifyDataSetChanged()
    }

    fun setLocationsList(locationList: List<String>){
        this.locationsList = locationList
    }

    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    inner class BoardingDroppingLocationViewHolder(val binding: ItemRadioLocationBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.radioButton.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BoardingDroppingLocationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRadioLocationBinding.inflate(inflater, parent, false)
        return BoardingDroppingLocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BoardingDroppingLocationViewHolder, position: Int) {
        holder.binding.apply {
            radioButton.text = locationsList[position]
            radioButton.isChecked = position == currPos
        }
    }

    override fun getItemCount(): Int {
        return locationsList.size
    }

}