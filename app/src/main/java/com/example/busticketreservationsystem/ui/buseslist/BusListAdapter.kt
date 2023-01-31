package com.example.busticketreservationsystem.ui.buseslist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.busticketreservationsystem.data.entity.Bus
import com.example.busticketreservationsystem.databinding.ItemBusBinding

class BusListAdapter: RecyclerView.Adapter<BusListAdapter.BusListViewHolder>() {

    private var busList = listOf<Bus>()

    fun setBusList(busList: List<Bus>){
        this.busList = busList
    }

    inner class BusListViewHolder(val binding: ItemBusBinding): RecyclerView.ViewHolder(binding.root){
        init {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBusBinding.inflate(inflater, parent, false)
        return BusListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BusListViewHolder, position: Int) {
        holder.binding.apply {

        }
    }

    override fun getItemCount(): Int {
        return busList.size
    }
}