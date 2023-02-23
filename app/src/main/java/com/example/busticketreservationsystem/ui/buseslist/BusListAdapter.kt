package com.example.busticketreservationsystem.ui.buseslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.data.entity.Bus
import com.example.busticketreservationsystem.databinding.ItemBusBinding
import com.example.busticketreservationsystem.listeners.OnItemClickListener

class BusListAdapter: RecyclerView.Adapter<BusListAdapter.BusListViewHolder>() {

    private var busList = listOf<Bus>()

    fun setBusList(busList: List<Bus>){
        this.busList = busList
    }

    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    inner class BusListViewHolder(val binding: ItemBusBinding): RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBusBinding.inflate(inflater, parent, false)
        return BusListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BusListViewHolder, position: Int) {
        holder.binding.apply {
            busTextView.text = busList[position].busName
        }
    }

    override fun getItemCount(): Int {
        return busList.size
    }
}