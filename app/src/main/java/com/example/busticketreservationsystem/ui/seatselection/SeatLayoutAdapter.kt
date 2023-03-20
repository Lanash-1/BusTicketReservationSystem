package com.example.busticketreservationsystem.ui.seatselection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.databinding.ItemSeatBinding
import com.example.busticketreservationsystem.databinding.ItemSleeperBinding
import com.example.busticketreservationsystem.enums.BusSeatType
import com.example.busticketreservationsystem.listeners.OnItemClickListener

class SeatLayoutAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var busSeatType: String
    var seatCount: Int = 0
    var seatStatus = listOf<Int>()

    fun setSeatLayoutData(busSeatType: String, seatCount: Int, seatStatus: List<Int>){
        this.busSeatType = busSeatType
        this.seatCount = seatCount
        this.seatStatus = seatStatus
    }

    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    inner class SeaterLayoutViewHolder(val binding: ItemSeatBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    inner class SleeperLayoutViewHolder(val binding: ItemSleeperBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when(busSeatType){
            BusSeatType.SEATER.name -> {
                val binding = ItemSeatBinding.inflate(layoutInflater, parent, false)
                SeaterLayoutViewHolder(binding)
            }
            BusSeatType.SLEEPER.name -> {
                val binding = ItemSleeperBinding.inflate(layoutInflater, parent, false)
                SleeperLayoutViewHolder(binding)
            }
            else -> {
                val binding = ItemSeatBinding.inflate(layoutInflater, parent, false)
                SeaterLayoutViewHolder(binding)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(busSeatType){
            BusSeatType.SEATER.name -> {
                holder as SeaterLayoutViewHolder
                if(seatStatus[position] == 0){
                    holder.binding.apply{
                        availableSeatIcon.visibility = View.VISIBLE
                    }
                }
                if(seatStatus[position] == 1){
                    holder.binding.apply{
                        selectedSeatIcon.visibility = View.VISIBLE
                    }
                }
                if(seatStatus[position] == -1){
                    holder.binding.apply{
                        notAvailableSeatIcon.visibility = View.VISIBLE
                    }
                }

            }
            BusSeatType.SLEEPER.name -> {
                holder as SleeperLayoutViewHolder
                if(seatStatus[position] == 0){
                    holder.binding.apply{
                        availableSeatIcon.visibility = View.VISIBLE
                    }
                }
                if(seatStatus[position] == 1){
                    holder.binding.apply{
                        selectedSeatIcon.visibility = View.VISIBLE
                    }
                }
                if(seatStatus[position] == -1){
                    holder.binding.apply{
                        notAvailableSeatIcon.visibility = View.VISIBLE
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return seatCount
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        when(busSeatType){
            BusSeatType.SEATER.name -> {
                holder as SeaterLayoutViewHolder
//                holder.binding.availableSeatIcon.visibility = View.INVISIBLE
                holder.binding.selectedSeatIcon.visibility = View.INVISIBLE
                holder.binding.notAvailableSeatIcon.visibility = View.INVISIBLE
            }
            BusSeatType.SLEEPER.name -> {
                holder as SleeperLayoutViewHolder
//                holder.binding.availableSeatIcon.visibility = View.INVISIBLE
                holder.binding.selectedSeatIcon.visibility = View.INVISIBLE
                holder.binding.notAvailableSeatIcon.visibility = View.INVISIBLE
            }
        }

    }

}