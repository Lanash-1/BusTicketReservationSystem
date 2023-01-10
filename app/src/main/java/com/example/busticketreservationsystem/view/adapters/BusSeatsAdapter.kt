package com.example.busticketreservationsystem.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.databinding.ItemSeatBinding
import com.example.busticketreservationsystem.listeners.OnItemClickListener

class BusSeatsAdapter: RecyclerView.Adapter<BusSeatsAdapter.BusSeatsViewHolder>() {

    private var seatsList = listOf<Int>()

    fun setBusSeatsList(seatsList: List<Int>){
        this.seatsList = seatsList
    }

    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    inner class BusSeatsViewHolder(val binding: ItemSeatBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener{
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusSeatsViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSeatBinding.inflate(inflater, parent, false)
        return BusSeatsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BusSeatsViewHolder, position: Int) {
        holder.binding.apply {
            if(position%4 == 1){
                availableSeatIcon.visibility = View.INVISIBLE
            }else if(seatsList[position] == 0){
                availableSeatIcon.visibility = View.VISIBLE
            }else if(seatsList[position] == 1){
                selectedSeatIcon.visibility = View.VISIBLE
            }else if(seatsList[position] == -1){
                notAvailableSeatIcon.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return seatsList.size
    }

    override fun onViewRecycled(holder: BusSeatsViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.selectedSeatIcon.visibility = View.INVISIBLE
        holder.binding.notAvailableSeatIcon.visibility = View.INVISIBLE
    }

//    override fun getItemId(position: Int): Long {
//        println("ITEM ID - $position")
//        return position.toLong()
//    }

//    override fun getItemId(position: Int): Long {
//        return super.getItemId(position)
//    }

//    override fun getItemViewType(position: Int): Int {
//        println("ITEM VIEW TYPE - $position")
//        return position
//    }

//    override fun getItemViewType(position: Int): Int {
//        return super.getItemViewType(position)
//    }
}